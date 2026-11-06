package com.example.parser

data class ParsedStep(
    val title: String,
    val category: String, // "TRIGGER", "BOT_ACTION", "DECISION", "VALIDATION"
    val description: String,
    val botPayload: String,
    val xpReward: Int = 35
)

data class ParsedWorkflowResult(
    val title: String,
    val description: String,
    val category: String,
    val steps: List<ParsedStep>
)

object WorkflowParser {

    fun parse(document: String): ParsedWorkflowResult {
        val lines = document.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) {
            return ParsedWorkflowResult(
                title = "Untitled Workflow",
                description = "Empty workflow outline",
                category = "General",
                steps = emptyList()
            )
        }

        var detectedTitle = "Bot Recipe Tree"
        var detectedDescription = ""
        val stepBlocks = mutableListOf<MutableList<String>>()
        var currentBlock: MutableList<String>? = null

        val stepStartRegex = Regex("""^(?:(?:\d+[\.\)]|step\s*\d+[:\.]?|[-*]|\#{1,4})\s+)(.*)""", RegexOption.IGNORE_CASE)

        var headerParsed = false
        val descLines = mutableListOf<String>()

        for (line in lines) {
            // Check for title header
            if (!headerParsed && (line.startsWith("# ") || line.startsWith("Workflow:") || line.startsWith("Title:"))) {
                detectedTitle = line.removePrefix("# ")
                    .removePrefix("Workflow:")
                    .removePrefix("Title:")
                    .trim()
                headerParsed = true
                continue
            }

            val stepMatch = stepStartRegex.find(line)
            val isStepHeader = stepMatch != null && (
                line.contains("trigger", ignoreCase = true) ||
                line.contains("action", ignoreCase = true) ||
                line.contains("if ", ignoreCase = true) ||
                line.contains("bot", ignoreCase = true) ||
                line.contains("verify", ignoreCase = true) ||
                line.contains("step", ignoreCase = true) ||
                line.matches(Regex("""^\d+[\.\)]\s+.*"""))
            )

            if (isStepHeader) {
                if (currentBlock != null && currentBlock.isNotEmpty()) {
                    stepBlocks.add(currentBlock)
                }
                currentBlock = mutableListOf(line)
            } else if (currentBlock != null) {
                currentBlock.add(line)
            } else {
                // Intro / description line before steps start
                if (!headerParsed && detectedTitle == "Bot Recipe Tree" && !line.startsWith("#")) {
                    detectedTitle = line
                    headerParsed = true
                } else {
                    descLines.add(line)
                }
            }
        }

        if (currentBlock != null && currentBlock.isNotEmpty()) {
            stepBlocks.add(currentBlock)
        }

        if (detectedDescription.isEmpty() && descLines.isNotEmpty()) {
            detectedDescription = descLines.take(3).joinToString(" ")
        }
        if (detectedDescription.isEmpty()) {
            detectedDescription = "Automated execution hierarchy generated from workflow document."
        }

        // If no structured step lines were matched, split by bullet points or paragraphs
        if (stepBlocks.isEmpty()) {
            for (line in lines) {
                if (line != detectedTitle) {
                    stepBlocks.add(mutableListOf(line))
                }
            }
        }

        val parsedSteps = stepBlocks.mapIndexed { index, block ->
            val firstLine = block.first()
            val rawHeader = firstLine.replace(Regex("""^(?:(?:\d+[\.\)]|step\s*\d+[:\.]?|[-*]|\#{1,4})\s+)""", RegexOption.IGNORE_CASE), "").trim()
            val remainingDesc = if (block.size > 1) block.drop(1).joinToString("\n") else rawHeader

            val category = classifyStep(firstLine)
            val xp = when (category) {
                "TRIGGER" -> 30
                "DECISION" -> 40
                "BOT_ACTION" -> 50
                "VALIDATION" -> 45
                else -> 35
            }

            val payload = generateBotPayload(category, rawHeader, remainingDesc)

            ParsedStep(
                title = if (rawHeader.length > 50) rawHeader.take(47) + "..." else rawHeader,
                category = category,
                description = remainingDesc,
                botPayload = payload,
                xpReward = xp
            )
        }

        return ParsedWorkflowResult(
            title = detectedTitle.ifEmpty { "Bot Automation Recipe" },
            description = detectedDescription,
            category = inferCategory(detectedTitle, lines),
            steps = parsedSteps
        )
    }

    private fun classifyStep(line: String): String {
        val lower = line.lowercase()
        return when {
            lower.contains("trigger") || lower.contains("when ") || lower.contains("on ") ||
                lower.contains("webhook") || lower.contains("schedule") || lower.contains("listen") ||
                lower.contains("event:") -> "TRIGGER"

            lower.contains("if ") || lower.contains("condition") || lower.contains("check") ||
                lower.contains("branch") || lower.contains("evaluate") || lower.contains("decision") -> "DECISION"

            lower.contains("verify") || lower.contains("validate") || lower.contains("assert") ||
                lower.contains("confirm") || lower.contains("test") || lower.contains("monitor") -> "VALIDATION"

            lower.contains("bot") || lower.contains("execute") || lower.contains("call") ||
                lower.contains("post") || lower.contains("send") || lower.contains("deploy") ||
                lower.contains("notify") || lower.contains("generate") || lower.contains("extract") ||
                lower.contains("action") -> "BOT_ACTION"

            else -> "BOT_ACTION"
        }
    }

    private fun inferCategory(title: String, lines: List<String>): String {
        val full = (title + " " + lines.joinToString(" ")).lowercase()
        return when {
            full.contains("incident") || full.contains("alert") || full.contains("sentry") || full.contains("triage") -> "DevOps / Reliability"
            full.contains("deploy") || full.contains("ci/cd") || full.contains("release") || full.contains("build") -> "Engineering Pipeline"
            full.contains("content") || full.contains("social") || full.contains("post") || full.contains("marketing") -> "Content Engine"
            full.contains("customer") || full.contains("support") || full.contains("ticket") || full.contains("churn") -> "Operations & Support"
            full.contains("data") || full.contains("scraper") || full.contains("etl") -> "Data Automation"
            else -> "Mind Garden Pathway"
        }
    }

    private fun generateBotPayload(category: String, title: String, desc: String): String {
        val sanitizedTitle = title.replace("\"", "'")
        return when (category) {
            "TRIGGER" -> """{"mode":"EVENT_LISTENER","source":"Webhook/Bus","event":"${sanitizedTitle}","debounceMs":500}"""
            "DECISION" -> """{"mode":"LOGIC_GATE","evaluator":"Script","rules":[{"condition":"payload.status == 'VALID'","route":"NextBranch"}]}"""
            "VALIDATION" -> """{"mode":"ASSERTION","healthcheck":"/api/health","timeoutSec":15,"retryLimit":3}"""
            else -> """{"mode":"AUTONOMOUS_BOT","action":"${sanitizedTitle}","runtime":"WorkerV2","concurrency":4}"""
        }
    }
}

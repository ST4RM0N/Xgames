package com.example.parser

data class WorkflowTemplate(
    val name: String,
    val summary: String,
    val iconName: String,
    val sampleDocument: String
)

object WorkflowTemplates {
    val templates = listOf(
        WorkflowTemplate(
            name = "DevOps Incident Response Bot",
            summary = "Automatic triage, alert escalation, war room creation & post-mortem logging",
            iconName = "Security",
            sampleDocument = """
# Automated Incident Response Bot Tree
When production error rates exceed 2.5% over a 3-minute sliding window, orchestrate immediate mitigation.

1. Trigger: PagerDuty / Sentry Webhook Ingestion
Listen on webhook /v1/alerts/critical with payload signature verification and rate-limit guard.

2. Condition: Evaluate Incident Severity Matrix
If severity is P0 or P1, branch to immediate executive alert; if P2, post to duty-engineer channel.

3. Bot Action: Provision Dedicated Slack War Room
Autonomous Slack bot creates #incident-YYYYMMDD, invites duty leads, and pins stack traces.

4. Bot Action: Fetch Telemetry & Isolate Bad Canary Deploy
Query Datadog trace logs, identify failing microservice commit SHA, and initiate canary rollback.

5. Validation: Run Synthetic Health Checks
Issue ping and integration test requests to endpoints; confirm error rate drops below 0.05%.

6. Bot Action: Publish Post-Mortem Draft & Archive Room
Aggregate timeline of events, generate Markdown post-mortem template, and notify stakeholders.
            """.trimIndent()
        ),
        WorkflowTemplate(
            name = "Autonomous Content Engine",
            summary = "Ingest long-form articles, distill insights, generate social threads & schedule",
            iconName = "Share",
            sampleDocument = """
# Autonomous Content Repurposing Pipeline
Transform raw technical memos and blog posts into high-engagement multi-platform content nodes.

1. Trigger: New Draft Published to Notion / Markdown Folder
Listen for file additions or webhook event with article markdown body.

2. Bot Action: Extract Core Thesis & High-Impact Quotes
Run semantic analyzer to extract 3 punchy takeaways and 5 memorable pull-quotes.

3. Bot Action: Synthesize Viral 7-Tweet Discussion Thread
Format opening hook, bulleted insights, visual diagram placeholder, and call-to-action link.

4. Condition: Review Flag Check
If content contains external benchmark statistics, require human verification before queueing.

5. Bot Action: Schedule Sprout to Buffer & Bluesky
Dispatch timed API payloads to publishing queue spaced 3 hours apart.

6. Validation: Measure 24h Resonance & Log Thought Experience
Track click-throughs, reposts, and bookmark metrics; update Mind Garden knowledge node.
            """.trimIndent()
        ),
        WorkflowTemplate(
            name = "VIP Churn Prevention Bot",
            summary = "Early warning churn detection, customer health scoring & executive reach-out",
            iconName = "Group",
            sampleDocument = """
# VIP Customer Retention Bot Tree
Proactively catch retention drops in enterprise accounts before renewal discussions.

1. Trigger: Account Weekly Activity Drops Over 40%
Triggered by daily analytics ETL when 7-day rolling active user count drops sharply.

2. Condition: Verify Contract Tier & Renewal Window
If account ARR > ${'$'}25,000 and renewal date < 90 days, escalate priority to Urgent.

3. Bot Action: Generate Account Health Dossier
Aggregate recent support tickets, feature requests, and login frequencies into executive PDF brief.

4. Bot Action: Draft Personalized Check-in from Account Exec
Compose bespoke email draft addressing specific unadopted features with personalized calendar invite.

5. Validation: Monitor Account Engagement Spike
Verify if client books meeting or resumes session within 5 business days; log outcome to CRM.
            """.trimIndent()
        ),
        WorkflowTemplate(
            name = "Smart CI/CD Gatekeeper",
            summary = "Enforce unit testing, security linting, preview deploy & regression smoke tests",
            iconName = "Code",
            sampleDocument = """
# Smart CI/CD Security Gatekeeper
Gate code merging through autonomous multi-stage test harnesses and ephemeral previews.

1. Trigger: Pull Request Opened or Synchronized
Listen for GitHub webhook pull_request.opened targeting main branch.

2. Bot Action: Run Parallel Unit & Static Security Linters
Spin up ephemeral runners; test for CVE vulnerabilities, memory leaks, and lint violations.

3. Condition: Check Test Coverage Delta
If test coverage decreases by more than 0.5%, reject build with actionable line diffs.

4. Bot Action: Spin Up Ephemeral Staging Environment
Deploy isolated Docker container cluster with seeded staging database for end-to-end verification.

5. Validation: Execute End-to-End Cypress Smoke Suite
Simulate critical user journeys (signup, checkout, data export); assert 0 fatal errors.
            """.trimIndent()
        )
    )
}

package com.example.ai

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val suggestedThoughtTitle: String? = null,
    val suggestedThoughtSummary: String? = null
)

enum class GeminiModelChoice(val modelId: String, val displayName: String, val tag: String) {
    FLASH("gemini-3.8-flash", "Gemini 3.8 Flash", "General Tasks (Default)"),
    ARGON("gemini-4-argon", "Gemini 4 Argon", "Complex Reasoning & Architecture"),
    LITE("gemini-3.1-flash-lite", "Gemini 3.1 Flash-Lite", "Ultra Fast Tasks")
}

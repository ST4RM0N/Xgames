package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiChatService"

class GeminiChatService {

    private val systemInstructionContent: Content = content {
        text(
            """
            You are the Grokpot Automation Architect and Mind Garden Sage.
            Your purpose is to help users design bot recipe trees, decompose complex workflow documents into intuitive automation hierarchies (Triggers, Conditions, Bot Actions, Validations), and optimize their Mind Garden knowledge graphs.
            Always maintain a helpful, technically sharp, bioluminescent-inspired persona. Provide structured automation steps and clear recommendations.
            """.trimIndent()
        )
    }

    private fun getModel(modelChoice: GeminiModelChoice) =
        Firebase.ai.generativeModel(
            modelName = modelChoice.modelId,
            systemInstruction = systemInstructionContent
        )

    fun streamChat(
        history: List<ChatMessage>,
        userMessage: String,
        modelChoice: GeminiModelChoice
    ): Flow<String> = flow {
        val model = getModel(modelChoice)

        // Convert existing chat history into Firebase AI Content format
        val chatHistory = history.filter { !it.isStreaming && it.text.isNotBlank() }.map { msg ->
            content(if (msg.role == "user") "user" else "model") {
                text(msg.text)
            }
        }

        val chatSession = model.startChat(history = chatHistory)

        try {
            withTimeout(60_000L) {
                chatSession.sendMessageStream(userMessage).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    if (text.isNotEmpty()) {
                        emit(text)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat response timed out", e)
            emit("\n[Generation timed out after 60s. Please check network and retry.]")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat inference failed", e)
            val errorMsg = e.localizedMessage ?: "Unknown error"
            emit("\n[Inference Notice: $errorMsg]")
        }
    }
}

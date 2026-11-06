package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.ChatMessage
import com.example.ai.GeminiModelChoice
import com.example.ui.theme.BioAmber
import com.example.ui.theme.BioBackground
import com.example.ui.theme.BioCyan
import com.example.ui.theme.BioCyanGlow
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.BioEmeraldGlow
import com.example.ui.theme.BioSurface
import com.example.ui.theme.BioSurfaceHigh
import com.example.ui.theme.BioSurfaceVariant
import com.example.ui.theme.BioTextMuted
import com.example.ui.theme.BioTextPrimary
import com.example.ui.theme.BioTextSecondary
import com.example.ui.theme.BioViolet
import com.example.ui.theme.BioVioletGlow
import com.example.viewmodel.GrokpotViewModel

@Composable
fun GeminiChatScreen(
    viewModel: GrokpotViewModel,
    onPlantThought: (title: String, summary: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedGeminiModel.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to bottom when messages update
    LaunchedEffect(chatMessages.size, chatMessages.lastOrNull()?.text?.length) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BioBackground)
            .imePadding()
            .testTag("gemini_chat_screen")
    ) {
        // Chat Header with Persona & Model Selector
        Surface(
            color = BioSurface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = BioViolet.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = BioVioletGlow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Grokpot Sage & Architect",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BioTextPrimary
                                )
                            )
                            Text(
                                text = "AI Bot Recipe & Mind Garden Sage",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BioCyanGlow,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    if (chatMessages.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearChatHistory() },
                            modifier = Modifier.size(32.dp).testTag("clear_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Chat",
                                tint = BioTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Model Selection Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GeminiModelChoice.values().forEach { choice ->
                        FilterChip(
                            selected = selectedModel == choice,
                            onClick = { viewModel.selectGeminiModel(choice) },
                            label = { Text(choice.displayName, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BioViolet.copy(alpha = 0.3f),
                                selectedLabelColor = BioVioletGlow,
                                containerColor = BioSurfaceVariant,
                                labelColor = BioTextSecondary
                            ),
                            modifier = Modifier.testTag("model_chip_${choice.modelId}")
                        )
                    }
                }
            }
        }

        // Message Thread (Scrollable)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    EmptyChatGreeting(
                        onSelectPrompt = { prompt ->
                            inputText = prompt
                            viewModel.sendChatMessage(prompt)
                        }
                    )
                }
            } else {
                items(chatMessages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onPlantSuggested = {
                            val title = message.text.lines().firstOrNull { it.isNotBlank() }?.take(40) ?: "Synthesized Pathway"
                            onPlantThought(title, message.text.take(150))
                        }
                    )
                }
            }

            if (isChatLoading && chatMessages.lastOrNull()?.role != "model") {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BioCyanGlow,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Synthesizing automation pathways...",
                            style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                        )
                    }
                }
            }
        }

        // Chat Input Bar
        Surface(
            color = BioSurface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask the Sage about bot recipes, branches, SOPs...", color = BioTextMuted, fontSize = 13.sp) },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BioTextPrimary,
                        unfocusedTextColor = BioTextPrimary,
                        focusedBorderColor = BioCyan,
                        unfocusedBorderColor = BioSurfaceHigh,
                        focusedContainerColor = BioBackground,
                        unfocusedContainerColor = BioBackground
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotBlank() && !isChatLoading) {
                            inputText = ""
                            viewModel.sendChatMessage(text)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isChatLoading,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isChatLoading) BioCyan else BioSurfaceHigh)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isChatLoading) Color(0xFF00363D) else BioTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    onPlantSuggested: () -> Unit
) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Surface(
                color = if (isUser) BioEmerald.copy(alpha = 0.2f) else BioSurfaceVariant,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isUser) BioEmerald.copy(alpha = 0.4f) else BioCyan.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isUser) "You" else "Automation Sage",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isUser) BioEmeraldGlow else BioCyanGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BioTextPrimary,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            if (!isUser && message.text.length > 50) {
                TextButton(
                    onClick = onPlantSuggested,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = BioEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Plant Idea into Garden (+25 XP)",
                        color = BioEmerald,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatGreeting(
    onSelectPrompt: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = BioViolet.copy(alpha = 0.2f),
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BioVioletGlow,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Welcome to the Grokpot Sage",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BioTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Powered by Gemini to architect bot recipes, decompose messy documents, and cultivate mind garden hierarchies.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = BioTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SAMPLE ARCHITECTURE PROMPTS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = BioCyanGlow,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        val starterPrompts = listOf(
            "How should I structure a DevOps rollback bot recipe tree?",
            "Decompose a customer onboarding SOP into triggers & actions",
            "What thought experience thresholds make sense for self-healing automations?"
        )

        starterPrompts.forEach { prompt ->
            Surface(
                color = BioSurface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, BioSurfaceHigh, RoundedCornerShape(12.dp))
            ) {
                TextButton(
                    onClick = { onSelectPrompt(prompt) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = prompt,
                        color = BioTextPrimary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

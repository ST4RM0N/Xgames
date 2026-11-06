package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GardenNodeEntity
import com.example.data.entity.RecipeStepEntity
import com.example.ui.theme.BioAmber
import com.example.ui.theme.BioBackground
import com.example.ui.theme.BioCyan
import com.example.ui.theme.BioCyanGlow
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.BioEmeraldGlow
import com.example.ui.theme.BioRose
import com.example.ui.theme.BioSurface
import com.example.ui.theme.BioSurfaceHigh
import com.example.ui.theme.BioSurfaceVariant
import com.example.ui.theme.BioTextMuted
import com.example.ui.theme.BioTextPrimary
import com.example.ui.theme.BioTextSecondary
import com.example.ui.theme.BioViolet
import com.example.ui.theme.BioVioletGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeInspectorSheet(
    node: GardenNodeEntity,
    associatedStep: RecipeStepEntity?,
    onDismiss: () -> Unit,
    onExecuteStep: (Long) -> Unit,
    onAddNotes: (Long, String) -> Unit,
    onSproutChild: (GardenNodeEntity) -> Unit,
    onDeleteNode: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isEditingNotes by remember { mutableStateOf(false) }
    var notesText by remember(node.notes) { mutableStateOf(node.notes) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BioSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("node_inspector_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Category, Level & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category = node.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    LevelBadge(level = node.level)
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_inspector_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = BioTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = node.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = BioTextPrimary,
                    lineHeight = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = node.summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BioTextSecondary,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Thought XP Progression Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BioSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BioCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Thought XP",
                                tint = BioAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Thought Experience",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = BioTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Text(
                            text = "${node.experience} / ${node.experienceToNext} XP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = BioCyanGlow,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progress = (node.experience.toFloat() / node.experienceToNext.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BioCyanGlow,
                        trackColor = BioSurfaceHigh
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = getLevelDescription(node.level),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BioTextMuted
                        )
                    )
                }
            }

            // Associated Bot Recipe Step Execution Section (if applicable)
            if (associatedStep != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = BioSurfaceHigh),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BOT RECIPE STEP #${associatedStep.stepIndex}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BioEmeraldGlow,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            )

                            if (associatedStep.isExecuted) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Executed",
                                        tint = BioEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Executed (+${associatedStep.xpReward} XP)",
                                        style = MaterialTheme.typography.labelSmall.copy(color = BioEmerald)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = associatedStep.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BioTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Payload snippet
                        Surface(
                            color = BioBackground,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = associatedStep.botPayload,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BioCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        if (associatedStep.executionLog.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = associatedStep.executionLog,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BioTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onExecuteStep(associatedStep.id) },
                            enabled = !associatedStep.isExecuted,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BioEmerald,
                                disabledContainerColor = BioEmerald.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("execute_step_button")
                        ) {
                            Icon(
                                imageVector = if (associatedStep.isExecuted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (associatedStep.isExecuted) "Step Executed & Verified" else "Simulate & Execute Bot Action (+${associatedStep.xpReward} XP)",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reflection / Field Notes Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BioSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thought Notes & Insights",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = BioTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        TextButton(
                            onClick = { isEditingNotes = !isEditingNotes },
                            modifier = Modifier.testTag("toggle_notes_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = BioCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEditingNotes) "Done" else if (node.notes.isEmpty()) "Add Note (+20 XP)" else "Edit",
                                color = BioCyan
                            )
                        }
                    }

                    if (isEditingNotes) {
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = { Text("Log observation, edge cases, or synthesis...", color = BioTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BioTextPrimary,
                                unfocusedTextColor = BioTextPrimary,
                                focusedBorderColor = BioCyan,
                                unfocusedBorderColor = BioSurfaceHigh
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notes_input_field")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilledTonalButton(
                            onClick = {
                                onAddNotes(node.id, notesText)
                                isEditingNotes = false
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = BioCyan),
                            modifier = Modifier.align(Alignment.End).testTag("save_notes_button")
                        ) {
                            Text("Save Insight", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        if (node.notes.isNotEmpty()) {
                            Text(
                                text = node.notes,
                                style = MaterialTheme.typography.bodyMedium.copy(color = BioTextSecondary)
                            )
                        } else {
                            Text(
                                text = "No personal reflection logged yet. Reflect on this thought to earn +20 XP.",
                                style = MaterialTheme.typography.bodySmall.copy(color = BioTextMuted)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Sprout Sub-Thought & Delete Node
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onSproutChild(node) },
                    colors = ButtonDefaults.buttonColors(containerColor = BioViolet),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sprout_child_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sprout Child Thought")
                }

                OutlinedButton(
                    onClick = { onDeleteNode(node.id) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BioRose),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("delete_node_button")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val (bgColor, textColor) = when (category) {
        "TRIGGER" -> Pair(BioCyan.copy(alpha = 0.2f), BioCyanGlow)
        "DECISION" -> Pair(BioAmber.copy(alpha = 0.2f), BioAmber)
        "VALIDATION" -> Pair(BioEmerald.copy(alpha = 0.2f), BioEmeraldGlow)
        "BOT_ACTION" -> Pair(BioEmerald.copy(alpha = 0.2f), BioEmerald)
        else -> Pair(BioViolet.copy(alpha = 0.2f), BioVioletGlow)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = category.replace("_", " "),
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun LevelBadge(level: Int) {
    Surface(
        color = BioSurfaceHigh,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "Level $level",
            color = BioTextPrimary,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getLevelDescription(level: Int): String {
    return when (level) {
        1 -> "Sprout: Nascent thought. Complete steps and log observations to grow."
        2 -> "Sapling: Grounded automation node. Radiates energy to connected pathways."
        3 -> "Blooming Node: High-resonance beacon. Unlocks deeper procedural sub-branches."
        4 -> "Ancient Arbor: Master autonomous orchestrator. Maximum garden thought power."
        else -> "Grown Thought"
    }
}

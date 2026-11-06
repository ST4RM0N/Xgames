package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.GardenNodeEntity
import com.example.ui.theme.BioCyan
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.BioSurface
import com.example.ui.theme.BioSurfaceHigh
import com.example.ui.theme.BioSurfaceVariant
import com.example.ui.theme.BioTextMuted
import com.example.ui.theme.BioTextPrimary
import com.example.ui.theme.BioTextSecondary
import com.example.ui.theme.BioViolet

@Composable
fun SeedThoughtDialog(
    parentThought: GardenNodeEntity?,
    onDismiss: () -> Unit,
    onPlantThought: (title: String, summary: String, category: String, parentId: Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("THOUGHT") }

    val categories = listOf("THOUGHT", "TRIGGER", "BOT_ACTION", "DECISION", "VALIDATION")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BioSurface,
        title = {
            Row {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = BioEmerald,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (parentThought != null) "Sprout from '${parentThought.title}'" else "Plant New Seed Thought",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BioTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "A seed thought forms a new pathway in the Mind Garden that levels up as you add sub-steps and automation logic.",
                    style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Thought / Node Title") },
                    placeholder = { Text("e.g., Dynamic Rate Limiter", color = BioTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BioTextPrimary,
                        unfocusedTextColor = BioTextPrimary,
                        focusedBorderColor = BioCyan,
                        unfocusedBorderColor = BioSurfaceHigh,
                        focusedLabelColor = BioCyan,
                        unfocusedLabelColor = BioTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("seed_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Core Hypothesis / Purpose") },
                    placeholder = { Text("Describe the logic or automation goal...", color = BioTextMuted) },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BioTextPrimary,
                        unfocusedTextColor = BioTextPrimary,
                        focusedBorderColor = BioCyan,
                        unfocusedBorderColor = BioSurfaceHigh,
                        focusedLabelColor = BioCyan,
                        unfocusedLabelColor = BioTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("seed_summary_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pathway Type:",
                    style = MaterialTheme.typography.labelMedium.copy(color = BioTextSecondary)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.replace("_", " "), style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BioEmerald.copy(alpha = 0.25f),
                                selectedLabelColor = BioEmerald,
                                containerColor = BioSurfaceVariant,
                                labelColor = BioTextSecondary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onPlantThought(
                            title.trim(),
                            summary.trim().ifEmpty { "Grounded node in mind garden." },
                            selectedCategory,
                            parentThought?.id
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BioEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_plant_button")
            ) {
                Text("Plant (+25 XP)", color = Color(0xFF002A19), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BioTextSecondary)
            }
        }
    )
}

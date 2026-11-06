package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GardenNodeEntity
import com.example.parser.WorkflowParser
import com.example.parser.WorkflowTemplates
import com.example.ui.components.CategoryBadge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeStudioScreen(
    viewModel: GrokpotViewModel,
    modifier: Modifier = Modifier
) {
    val allNodes by viewModel.allNodes.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    var documentText by remember { mutableStateOf("") }
    var selectedParentThoughtId by remember { mutableStateOf<Long?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Live preview parsing
    val parsedPreview by remember(documentText) {
        derivedStateOf {
            if (documentText.length >= 10) {
                WorkflowParser.parse(documentText)
            } else {
                null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BioBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("recipe_studio_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = BioCyan.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BioCyanGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Recipe Studio",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = BioTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Decompose SOPs and outlines into Bot Recipe Trees",
                    style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pre-built Template Presets
        Text(
            text = "LOAD WORKFLOW TEMPLATE",
            style = MaterialTheme.typography.labelSmall.copy(
                color = BioCyanGlow,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkflowTemplates.templates.forEach { template ->
                FilterChip(
                    selected = false,
                    onClick = {
                        documentText = template.sampleDocument
                    },
                    label = { Text(template.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BioEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = BioSurface,
                        labelColor = BioTextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Document Input Card
        Card(
            colors = CardDefaults.cardColors(containerColor = BioSurface),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BioSurfaceHigh, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workflow Document / Outline",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = BioTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val text = clipboardManager.getText()?.text
                                if (!text.isNullOrBlank()) {
                                    documentText = text
                                }
                            },
                            modifier = Modifier.size(32.dp).testTag("paste_clipboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard",
                                tint = BioCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (documentText.isNotEmpty()) {
                            IconButton(
                                onClick = { documentText = "" },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                    tint = BioTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = documentText,
                    onValueChange = { documentText = it },
                    placeholder = {
                        Text(
                            text = "Paste standard operating procedure, markdown guide, or outlines with triggers, conditions, bot actions, and validations...\n\nExample:\n# Incident Response\n1. Trigger: Sentry Alert\n2. Condition: If Error Rate > 2%\n3. Bot Action: Deploy Canary Rollback\n4. Validation: Verify Healthcheck",
                            color = BioTextMuted,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                        )
                    },
                    minLines = 8,
                    maxLines = 14,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BioTextPrimary,
                        unfocusedTextColor = BioTextPrimary,
                        focusedBorderColor = BioCyan,
                        unfocusedBorderColor = BioSurfaceHigh,
                        focusedContainerColor = BioBackground,
                        unfocusedContainerColor = BioBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workflow_document_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Anchor to Thought Node Dropdown (Optional)
        val thoughtCandidates = remember(allNodes) { allNodes.filter { it.category == "THOUGHT" } }
        if (thoughtCandidates.isNotEmpty()) {
            Text(
                text = "ANCHOR TO MIND THOUGHT (OPTIONAL)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BioTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                val selectedTitle = thoughtCandidates.firstOrNull { it.id == selectedParentThoughtId }?.title ?: "Root Garden Level (None)"

                OutlinedTextField(
                    value = selectedTitle,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BioTextPrimary,
                        unfocusedTextColor = BioTextPrimary,
                        focusedBorderColor = BioCyan,
                        unfocusedBorderColor = BioSurfaceHigh,
                        focusedContainerColor = BioSurface,
                        unfocusedContainerColor = BioSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(BioSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("None (Sprout independently in garden)", color = BioTextPrimary) },
                        onClick = {
                            selectedParentThoughtId = null
                            dropdownExpanded = false
                        }
                    )
                    thoughtCandidates.forEach { candidate ->
                        DropdownMenuItem(
                            text = { Text("${candidate.title} (Lv.${candidate.level})", color = BioTextPrimary) },
                            onClick = {
                                selectedParentThoughtId = candidate.id
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Button: Parse and Sprout
        Button(
            onClick = {
                if (documentText.isNotBlank()) {
                    viewModel.parseAndPlantWorkflow(
                        document = documentText,
                        parentThoughtId = selectedParentThoughtId
                    )
                }
            },
            enabled = documentText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BioEmerald,
                disabledContainerColor = BioSurfaceHigh
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("decompose_workflow_button")
        ) {
            Icon(
                imageVector = Icons.Default.Forest,
                contentDescription = null,
                tint = Color(0xFF002A19),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Decompose into Bot Recipe Tree (+100 XP)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF002A19)
                )
            )
        }

        // Live Preview Breakdown Card (if document parsed)
        parsedPreview?.let { preview ->
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PARSED BOT RECIPE TREE PREVIEW",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BioCyanGlow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = BioSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BioCyan.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = preview.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = BioTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${preview.category} • ${preview.steps.size} Actionable Hierarchy Steps",
                        style = MaterialTheme.typography.bodySmall.copy(color = BioCyanGlow)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    preview.steps.forEachIndexed { idx, step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Surface(
                                color = BioSurfaceHigh,
                                shape = CircleShape,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BioTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            CategoryBadge(category = step.category)

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = BioTextPrimary,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

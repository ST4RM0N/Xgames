package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.BotRecipeEntity
import com.example.data.entity.RecipeStepEntity
import com.example.ui.components.CategoryBadge
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
import com.example.viewmodel.GrokpotViewModel

@Composable
fun RecipeListScreen(
    viewModel: GrokpotViewModel,
    onViewInGarden: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val recipes by viewModel.allRecipes.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    var expandedRecipeId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BioBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("recipe_list_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = BioEmerald.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = null,
                        tint = BioEmeraldGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Bot Recipe Trees",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = BioTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${recipes.size} Active Automation Hierarchies",
                    style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = BioTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Bot Recipe Trees yet",
                        style = MaterialTheme.typography.titleMedium.copy(color = BioTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Switch to the Recipe Studio tab to ingest workflow documents and plant new automation trees.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        isExpanded = expandedRecipeId == recipe.id,
                        onToggleExpand = {
                            expandedRecipeId = if (expandedRecipeId == recipe.id) null else recipe.id
                        },
                        viewModel = viewModel,
                        onCopyJson = { json ->
                            clipboardManager.setText(AnnotatedString(json))
                        },
                        onDelete = {
                            viewModel.deleteRecipe(recipe.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: BotRecipeEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    viewModel: GrokpotViewModel,
    onCopyJson: (String) -> Unit,
    onDelete: () -> Unit
) {
    val stepsFlow = remember(recipe.id) { viewModel.getStepsForRecipe(recipe.id) }
    val steps by stepsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Card(
        colors = CardDefaults.cardColors(containerColor = BioSurface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BioSurfaceHigh, RoundedCornerShape(18.dp))
            .clickable { onToggleExpand() }
            .testTag("recipe_card_${recipe.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BioCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = recipe.category.uppercase(),
                        color = BioCyanGlow,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val recipeExport = buildRecipeJson(recipe, steps)
                            onCopyJson(recipeExport)
                        },
                        modifier = Modifier.size(32.dp).testTag("copy_recipe_json_${recipe.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Export Recipe",
                            tint = BioCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete recipe",
                            tint = BioRose.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = BioTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BioTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary),
                maxLines = if (isExpanded) Int.MAX_VALUE else 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val completed = steps.count { it.isExecuted }
            val progress = if (steps.isNotEmpty()) completed.toFloat() / steps.size.toFloat() else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hierarchy Execution: $completed / ${steps.size} steps",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (completed == steps.size && steps.isNotEmpty()) BioEmeraldGlow else BioTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(color = BioCyanGlow, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = BioEmerald,
                trackColor = BioSurfaceHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            // Expanded Steps Checklist
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "BOT RECIPE STEP HIERARCHY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BioCyanGlow,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    steps.forEach { step ->
                        StepItemRow(
                            step = step,
                            onExecute = { viewModel.executeStep(step.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StepItemRow(
    step: RecipeStepEntity,
    onExecute: () -> Unit
) {
    Surface(
        color = BioSurfaceHigh,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (step.isExecuted) BioEmerald.copy(alpha = 0.2f) else BioSurfaceVariant,
                shape = CircleShape,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (step.isExecuted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BioEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${step.stepIndex}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BioTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category = step.actionType)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BioTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1
                    )
                }

                if (step.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BioTextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!step.isExecuted) {
                IconButton(
                    onClick = onExecute,
                    modifier = Modifier.size(32.dp).testTag("execute_step_item_${step.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Execute step",
                        tint = BioEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun buildRecipeJson(recipe: BotRecipeEntity, steps: List<RecipeStepEntity>): String {
    val stepsJson = steps.joinToString(",\n    ") { step ->
        """{"index": ${step.stepIndex}, "type": "${step.actionType}", "title": "${step.title.replace("\"", "'")}", "executed": ${step.isExecuted}, "payload": ${step.botPayload.ifEmpty { "{}" }}}"""
    }

    return """{
  "grokpotRecipe": {
    "title": "${recipe.title.replace("\"", "'")}",
    "category": "${recipe.category}",
    "totalSteps": ${steps.size},
    "steps": [
    $stepsJson
    ]
  }
}"""
}

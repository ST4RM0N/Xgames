package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GardenNodeEntity
import com.example.ui.components.CategoryBadge
import com.example.ui.components.LevelBadge
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
fun ThoughtCodexScreen(
    viewModel: GrokpotViewModel,
    onNodeSelected: (GardenNodeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val allNodes by viewModel.allNodes.collectAsStateWithLifecycle()
    val totalXp by viewModel.totalGardenXp.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(allNodes, searchQuery) {
        if (searchQuery.isBlank()) {
            allNodes
        } else {
            allNodes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.summary.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BioBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("thought_codex_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = BioViolet.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = BioVioletGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Thought Codex",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = BioTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Mind Garden Pathways & Knowledge Nodes",
                    style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search thoughts, triggers, actions...", color = BioTextMuted) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BioTextSecondary)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = BioTextPrimary,
                unfocusedTextColor = BioTextPrimary,
                focusedBorderColor = BioCyan,
                unfocusedBorderColor = BioSurfaceHigh,
                focusedContainerColor = BioSurface,
                unfocusedContainerColor = BioSurface
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("codex_search_field")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Summary Pill
        Surface(
            color = BioSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredList.size} Knowledge Nodes",
                    style = MaterialTheme.typography.labelMedium.copy(color = BioTextSecondary)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = BioAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalXp Total XP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = BioCyanGlow,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.id }) { node ->
                CodexNodeCard(
                    node = node,
                    onClick = {
                        onNodeSelected(node)
                    }
                )
            }
        }
    }
}

@Composable
fun CodexNodeCard(
    node: GardenNodeEntity,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BioSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BioSurfaceHigh, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("codex_node_${node.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (node.bloomUnlocked) BioEmerald.copy(alpha = 0.2f) else BioSurfaceHigh,
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = if (node.bloomUnlocked) BioEmeraldGlow else BioTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryBadge(category = node.category)
                    LevelBadge(level = node.level)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = node.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BioTextPrimary
                    )
                )

                if (node.summary.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = node.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BioTextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 2
                    )
                }

                if (node.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reflected: \"${node.notes}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BioCyan,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${node.experience} XP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BioCyanGlow,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = if (node.isCompleted) "Verified" else "Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (node.isCompleted) BioEmerald else BioTextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

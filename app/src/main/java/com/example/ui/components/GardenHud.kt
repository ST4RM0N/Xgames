package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BioAmber
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

@Composable
fun GardenTopHud(
    totalXp: Int,
    nodeCount: Int,
    recipeCount: Int,
    selectedFilter: String,
    onFilterChanged: (String) -> Unit,
    onResetCamera: () -> Unit,
    onOpenEmailSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    val gardenTier = when {
        totalXp >= 1000 -> "Arbor Nexus (Tier IV)"
        totalXp >= 500 -> "Blooming Canopy (Tier III)"
        totalXp >= 200 -> "Sapling Grove (Tier II)"
        else -> "Seedling Garden (Tier I)"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main HUD Bar
        Surface(
            color = BioSurface.copy(alpha = 0.88f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BioCyan.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Garden Identity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(BioEmerald, BioSurfaceVariant)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Park,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Mind Garden",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BioTextPrimary
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BioAmber,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$gardenTier • $totalXp XP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BioCyanGlow,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Controls: Email Passthrough, Filters toggle & Center Focus
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenEmailSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("open_email_passthrough_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Passthrough",
                            tint = BioCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { showFilters = !showFilters },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_filters_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Toggle filters",
                            tint = if (showFilters || selectedFilter != "ALL") BioCyan else BioTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onResetCamera,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("reset_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Center canvas",
                            tint = BioTextSecondary
                        )
                    }
                }
            }
        }

        // Animated Filter Chips Bar
        AnimatedVisibility(
            visible = showFilters,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Nodes ($nodeCount)",
                    "THOUGHT" to "Thoughts",
                    "BOT_ACTION" to "Bot Actions",
                    "TRIGGER" to "Triggers",
                    "HIGH_XP" to "Blooming (Lv 2+)"
                )

                filters.forEach { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { onFilterChanged(key) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BioEmerald.copy(alpha = 0.25f),
                            selectedLabelColor = BioEmeraldGlow,
                            containerColor = BioSurface.copy(alpha = 0.75f),
                            labelColor = BioTextSecondary
                        )
                    )
                }
            }
        }
    }
}

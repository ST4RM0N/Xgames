package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GardenNodeEntity
import com.example.ui.canvas.GardenCanvas
import com.example.ui.canvas.GardenCanvasState
import com.example.ui.components.EmailPassthroughSheet
import com.example.ui.components.GardenTopHud
import com.example.ui.components.NodeInspectorSheet
import com.example.ui.components.SeedThoughtDialog
import com.example.ui.theme.BioBackground
import com.example.ui.theme.BioEmerald
import com.example.viewmodel.GrokpotViewModel

@Composable
fun MindGardenScreen(
    viewModel: GrokpotViewModel,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filteredNodes by viewModel.filteredNodes.collectAsStateWithLifecycle()
    val allNodes by viewModel.allNodes.collectAsStateWithLifecycle()
    val allEdges by viewModel.allEdges.collectAsStateWithLifecycle()
    val allRecipes by viewModel.allRecipes.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedNode by viewModel.selectedNode.collectAsStateWithLifecycle()
    val associatedStep by viewModel.associatedStep.collectAsStateWithLifecycle()
    val totalXp by viewModel.totalGardenXp.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val emailConfig by viewModel.emailConfig.collectAsStateWithLifecycle()
    val growthAlerts by viewModel.growthAlerts.collectAsStateWithLifecycle()
    val latestAlert by viewModel.latestGrowthAlert.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val canvasState = remember { GardenCanvasState() }

    var showPlantDialog by remember { mutableStateOf(false) }
    var parentForSprout by remember { mutableStateOf<GardenNodeEntity?>(null) }
    var showEmailSheet by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // When an email passthrough growth alert fires, show an actionable snackbar
    LaunchedEffect(latestAlert) {
        latestAlert?.let { alert ->
            val result = snackbarHostState.showSnackbar(
                message = "${alert.subject} routed to ${alert.recipient}",
                actionLabel = "Open Email",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                val emailIntent = viewModel.emailAlertManager.createEmailIntent(alert)
                try {
                    context.startActivity(emailIntent)
                } catch (e: Exception) {
                    // Fallback if no email client installed
                }
            }
            viewModel.clearLatestGrowthAlert()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BioBackground)
            .testTag("mind_garden_screen")
    ) {
        // Interactive Bioluminescent Canvas
        GardenCanvas(
            nodes = filteredNodes,
            edges = allEdges,
            selectedNodeId = selectedNode?.id,
            canvasState = canvasState,
            onNodeSelected = { node ->
                viewModel.selectNode(node)
            },
            onCanvasDoubleTapped = { worldOffset ->
                parentForSprout = null
                showPlantDialog = true
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top HUD with garden stats, tier, filter chips, and Email Passthrough launcher
        GardenTopHud(
            totalXp = totalXp,
            nodeCount = allNodes.size,
            recipeCount = allRecipes.size,
            selectedFilter = selectedFilter,
            onFilterChanged = { viewModel.setFilter(it) },
            onResetCamera = { canvasState.reset() },
            onOpenEmailSettings = { showEmailSheet = true },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Floating Action Button for quick planting
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    parentForSprout = null
                    showPlantDialog = true
                },
                containerColor = BioEmerald,
                contentColor = Color(0xFF002A19),
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = CircleShape,
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
                text = {
                    Text("Plant Seed Thought")
                },
                modifier = Modifier.testTag("plant_seed_fab")
            )
        }

        // Node Inspector Bottom Sheet
        selectedNode?.let { node ->
            NodeInspectorSheet(
                node = node,
                associatedStep = associatedStep,
                onDismiss = { viewModel.selectNode(null) },
                onExecuteStep = { stepId ->
                    viewModel.executeStep(stepId)
                },
                onAddNotes = { nodeId, notes ->
                    viewModel.addNotesToNode(nodeId, notes)
                },
                onSproutChild = { parentNode ->
                    parentForSprout = parentNode
                    showPlantDialog = true
                },
                onDeleteNode = { nodeId ->
                    viewModel.deleteNode(nodeId)
                }
            )
        }

        // Seed Thought Planting Dialog
        if (showPlantDialog) {
            SeedThoughtDialog(
                parentThought = parentForSprout,
                onDismiss = { showPlantDialog = false },
                onPlantThought = { title, summary, category, parentId ->
                    viewModel.plantSeedThought(title, summary, category, parentId)
                    showPlantDialog = false
                }
            )
        }

        // Email Passthrough Alert Sheet
        if (showEmailSheet) {
            EmailPassthroughSheet(
                config = emailConfig,
                alerts = growthAlerts,
                onUpdateConfig = { viewModel.updateEmailConfig(it) },
                onSendTestAlert = {
                    viewModel.sendTestEmailAlert()
                },
                onDismiss = { showEmailSheet = false }
            )
        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

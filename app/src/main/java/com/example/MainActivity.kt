package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.GeminiChatScreen
import com.example.ui.screens.MindGardenScreen
import com.example.ui.screens.RecipeListScreen
import com.example.ui.screens.RecipeStudioScreen
import com.example.ui.screens.ThoughtCodexScreen
import com.example.ui.theme.BioCyanGlow
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.BioSurface
import com.example.ui.theme.BioTextMuted
import com.example.ui.theme.BioTextSecondary
import com.example.ui.theme.BioVioletGlow
import com.example.ui.theme.GrokpotTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.GrokpotViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            GrokpotTheme {
                GrokpotMainApp()
            }
        }
    }
}

@Composable
fun GrokpotMainApp(viewModel: GrokpotViewModel = viewModel()) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedNode by viewModel.selectedNode.collectAsStateWithLifecycle()

    // BackHandler: If a node is selected, unselect it. If on another tab, return to Garden.
    BackHandler(enabled = selectedNode != null || activeTab != 0) {
        if (selectedNode != null) {
            viewModel.selectNode(null)
        } else {
            viewModel.setActiveTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF080C14),
        bottomBar = {
            NavigationBar(
                containerColor = BioSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = { Icon(imageVector = Icons.Default.Park, contentDescription = "Mind Garden") },
                    label = { Text("Garden", fontSize = 11.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF002A19),
                        selectedTextColor = BioEmerald,
                        indicatorColor = BioEmerald,
                        unselectedIconColor = BioTextMuted,
                        unselectedTextColor = BioTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_garden")
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Recipe Studio") },
                    label = { Text("Studio", fontSize = 11.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00363D),
                        selectedTextColor = BioCyanGlow,
                        indicatorColor = BioCyanGlow,
                        unselectedIconColor = BioTextMuted,
                        unselectedTextColor = BioTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_studio")
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = { Icon(imageVector = Icons.Default.AccountTree, contentDescription = "Bot Recipes") },
                    label = { Text("Recipes", fontSize = 11.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF002A19),
                        selectedTextColor = BioEmerald,
                        indicatorColor = BioEmerald,
                        unselectedIconColor = BioTextMuted,
                        unselectedTextColor = BioTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_recipes")
                )

                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.setActiveTab(3) },
                    icon = { Icon(imageVector = Icons.Default.Psychology, contentDescription = "Thought Codex") },
                    label = { Text("Codex", fontSize = 11.sp, fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2E1065),
                        selectedTextColor = Color(0xFFA78BFA),
                        indicatorColor = Color(0xFFA78BFA),
                        unselectedIconColor = BioTextMuted,
                        unselectedTextColor = BioTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_codex")
                )

                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { viewModel.setActiveTab(4) },
                    icon = { Icon(imageVector = Icons.Default.Forum, contentDescription = "Gemini Sage") },
                    label = { Text("Sage AI", fontSize = 11.sp, fontWeight = if (activeTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2E1065),
                        selectedTextColor = BioVioletGlow,
                        indicatorColor = BioVioletGlow,
                        unselectedIconColor = BioTextMuted,
                        unselectedTextColor = BioTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_sage")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> MindGardenScreen(
                    viewModel = viewModel,
                    onNavigateToStudio = { viewModel.setActiveTab(1) }
                )
                1 -> RecipeStudioScreen(
                    viewModel = viewModel
                )
                2 -> RecipeListScreen(
                    viewModel = viewModel,
                    onViewInGarden = {
                        viewModel.setActiveTab(0)
                    }
                )
                3 -> ThoughtCodexScreen(
                    viewModel = viewModel,
                    onNodeSelected = { node ->
                        viewModel.selectNode(node)
                        viewModel.setActiveTab(0)
                    }
                )
                4 -> GeminiChatScreen(
                    viewModel = viewModel,
                    onPlantThought = { title, summary ->
                        viewModel.plantSeedThought(title, summary, "THOUGHT", null)
                        viewModel.setActiveTab(0)
                    }
                )
            }
        }
    }
}

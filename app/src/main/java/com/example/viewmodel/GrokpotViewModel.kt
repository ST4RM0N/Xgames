package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ChatMessage
import com.example.ai.GeminiChatService
import com.example.ai.GeminiModelChoice
import com.example.data.alert.EmailAlertManager
import com.example.data.alert.EmailPassthroughConfig
import com.example.data.alert.GrowthAlert
import com.example.data.db.GrokpotDatabase
import com.example.data.entity.BotRecipeEntity
import com.example.data.entity.GardenEdgeEntity
import com.example.data.entity.GardenNodeEntity
import com.example.data.entity.RecipeStepEntity
import com.example.data.repository.GrokpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GrokpotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GrokpotRepository
    val emailAlertManager: EmailAlertManager = EmailAlertManager(application)
    private val geminiChatService: GeminiChatService = GeminiChatService()

    val allNodes: StateFlow<List<GardenNodeEntity>>
    val allEdges: StateFlow<List<GardenEdgeEntity>>
    val allRecipes: StateFlow<List<BotRecipeEntity>>

    val emailConfig: StateFlow<EmailPassthroughConfig> = emailAlertManager.config
    val growthAlerts: StateFlow<List<GrowthAlert>> = emailAlertManager.alerts
    val latestGrowthAlert: StateFlow<GrowthAlert?> = emailAlertManager.latestAlert

    // Gemini Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _selectedGeminiModel = MutableStateFlow(GeminiModelChoice.FLASH)
    val selectedGeminiModel: StateFlow<GeminiModelChoice> = _selectedGeminiModel.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedNode = MutableStateFlow<GardenNodeEntity?>(null)
    val selectedNode: StateFlow<GardenNodeEntity?> = _selectedNode.asStateFlow()

    private val _associatedStep = MutableStateFlow<RecipeStepEntity?>(null)
    val associatedStep: StateFlow<RecipeStepEntity?> = _associatedStep.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Garden, 1: Studio, 2: Recipes, 3: Codex, 4: AI Chat
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = GrokpotDatabase.getDatabase(application)
        repository = GrokpotRepository(database.gardenDao(), emailAlertManager)

        allNodes = repository.allNodes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allEdges = repository.allEdges.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allRecipes = repository.allRecipes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val filteredNodes: StateFlow<List<GardenNodeEntity>> = combine(allNodes, selectedFilter) { nodes, filter ->
        when (filter) {
            "THOUGHT" -> nodes.filter { it.category == "THOUGHT" }
            "BOT_ACTION" -> nodes.filter { it.category == "BOT_ACTION" }
            "TRIGGER" -> nodes.filter { it.category == "TRIGGER" }
            "HIGH_XP" -> nodes.filter { it.level >= 2 }
            else -> nodes
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGardenXp: StateFlow<Int> = allNodes.combine(allRecipes) { nodes, _ ->
        nodes.sumOf { it.experience }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectNode(node: GardenNodeEntity?) {
        _selectedNode.value = node
        if (node != null && node.recipeId != null) {
            viewModelScope.launch {
                val database = GrokpotDatabase.getDatabase(getApplication())
                val steps = database.gardenDao().getStepsForRecipeDirect(node.recipeId)
                _associatedStep.value = steps.firstOrNull { it.associatedNodeId == node.id }
            }
        } else {
            _associatedStep.value = null
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setActiveTab(index: Int) {
        _activeTab.value = index
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun updateEmailConfig(config: EmailPassthroughConfig) {
        emailAlertManager.updateConfig(config)
        _toastMessage.value = "Email passthrough settings updated."
    }

    fun sendTestEmailAlert() {
        val alert = emailAlertManager.notifyGrowth(
            nodeTitle = "Neural Automation Core",
            eventType = "LEVEL_UP",
            level = 2,
            xp = 150,
            notes = "Test verification of real-time growth alert passthrough."
        )
        if (alert != null) {
            _toastMessage.value = "Growth alert sent to ${alert.recipient}!"
        } else {
            _toastMessage.value = "Email passthrough is disabled. Enable it to receive alerts."
        }
    }

    fun clearLatestGrowthAlert() {
        emailAlertManager.clearLatestAlert()
    }

    fun parseAndPlantWorkflow(document: String, parentThoughtId: Long? = null, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val existingCount = allRecipes.value.size
                val originX = if (existingCount == 0) 0f else (existingCount % 2 * 600f - 300f)
                val originY = if (existingCount == 0) 0f else (existingCount * 450f - 200f)

                val recipeId = repository.parseAndPlantWorkflow(
                    document = document,
                    parentThoughtId = parentThoughtId,
                    originX = originX,
                    originY = originY
                )
                _toastMessage.value = "Workflow decomposed into Bot Recipe Tree! (+100 XP)"
                _activeTab.value = 0
                onComplete()
            } catch (e: Exception) {
                _toastMessage.value = "Failed to parse workflow: ${e.message}"
            }
        }
    }

    fun plantSeedThought(title: String, summary: String, category: String, parentId: Long?) {
        viewModelScope.launch {
            val id = repository.plantSeedThought(
                title = title,
                summary = summary,
                category = category,
                parentThoughtId = parentId
            )
            _toastMessage.value = "Seed Thought planted! (+25 XP)"
        }
    }

    fun executeStep(stepId: Long) {
        viewModelScope.launch {
            val success = repository.executeStep(stepId)
            if (success) {
                _toastMessage.value = "Bot action executed! Step verified & XP gained."
                _selectedNode.value?.let { current ->
                    val database = GrokpotDatabase.getDatabase(getApplication())
                    _selectedNode.value = database.gardenDao().getNodeByIdDirect(current.id)
                    val steps = database.gardenDao().getStepsForRecipeDirect(current.recipeId ?: 0)
                    _associatedStep.value = steps.firstOrNull { it.id == stepId }
                }
            }
        }
    }

    fun addNotesToNode(nodeId: Long, notes: String) {
        viewModelScope.launch {
            repository.updateNodeNotes(nodeId, notes)
            _toastMessage.value = "Insight saved to codex. (+20 XP)"
            _selectedNode.value?.let { current ->
                if (current.id == nodeId) {
                    val database = GrokpotDatabase.getDatabase(getApplication())
                    _selectedNode.value = database.gardenDao().getNodeByIdDirect(nodeId)
                }
            }
        }
    }

    fun deleteNode(nodeId: Long) {
        viewModelScope.launch {
            repository.deleteNode(nodeId)
            if (_selectedNode.value?.id == nodeId) {
                _selectedNode.value = null
                _associatedStep.value = null
            }
            _toastMessage.value = "Node pruned from garden."
        }
    }

    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            repository.deleteRecipe(recipeId)
            _toastMessage.value = "Bot recipe and connected branch removed."
        }
    }

    fun getStepsForRecipe(recipeId: Long) = repository.getStepsForRecipe(recipeId)

    // Gemini Chatbot Actions
    fun selectGeminiModel(choice: GeminiModelChoice) {
        _selectedGeminiModel.value = choice
    }

    fun clearChatHistory() {
        _chatMessages.value = emptyList()
        _toastMessage.value = "Chat history cleared."
    }

    fun sendChatMessage(prompt: String) {
        val userMsg = ChatMessage(role = "user", text = prompt)
        val currentHistory = _chatMessages.value
        _chatMessages.value = currentHistory + userMsg

        val modelMsgId = java.util.UUID.randomUUID().toString()
        val placeholderModelMsg = ChatMessage(id = modelMsgId, role = "model", text = "", isStreaming = true)
        _chatMessages.value = _chatMessages.value + placeholderModelMsg

        _isChatLoading.value = true

        viewModelScope.launch {
            val responseAccumulator = StringBuilder()
            try {
                geminiChatService.streamChat(
                    history = currentHistory,
                    userMessage = prompt,
                    modelChoice = _selectedGeminiModel.value
                ).collect { chunk ->
                    responseAccumulator.append(chunk)
                    _chatMessages.value = _chatMessages.value.map { msg ->
                        if (msg.id == modelMsgId) {
                            msg.copy(text = responseAccumulator.toString())
                        } else msg
                    }
                }
            } catch (e: Exception) {
                responseAccumulator.append("\n[Error: ${e.localizedMessage ?: "Failed to generate response"}]")
                _chatMessages.value = _chatMessages.value.map { msg ->
                    if (msg.id == modelMsgId) {
                        msg.copy(text = responseAccumulator.toString())
                    } else msg
                }
            } finally {
                _chatMessages.value = _chatMessages.value.map { msg ->
                    if (msg.id == modelMsgId) {
                        msg.copy(isStreaming = false)
                    } else msg
                }
                _isChatLoading.value = false
            }
        }
    }
}

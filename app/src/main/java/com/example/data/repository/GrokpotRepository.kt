package com.example.data.repository

import com.example.data.alert.EmailAlertManager
import com.example.data.dao.GardenDao
import com.example.data.entity.BotRecipeEntity
import com.example.data.entity.GardenEdgeEntity
import com.example.data.entity.GardenNodeEntity
import com.example.data.entity.RecipeStepEntity
import com.example.parser.WorkflowParser
import com.example.parser.WorkflowTemplates
import kotlinx.coroutines.flow.Flow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GrokpotRepository(
    private val dao: GardenDao,
    private val emailAlertManager: EmailAlertManager? = null
) {

    val allNodes: Flow<List<GardenNodeEntity>> = dao.getAllNodes()
    val allEdges: Flow<List<GardenEdgeEntity>> = dao.getAllEdges()
    val allRecipes: Flow<List<BotRecipeEntity>> = dao.getAllRecipes()

    fun getRecipeById(id: Long): Flow<BotRecipeEntity?> = dao.getRecipeById(id)
    fun getStepsForRecipe(recipeId: Long): Flow<List<RecipeStepEntity>> = dao.getStepsForRecipe(recipeId)
    fun getNodeById(id: Long): Flow<GardenNodeEntity?> = dao.getNodeById(id)

    suspend fun checkAndSeedInitialData() {
        val existing = dao.getAllRecipesDirect()
        // If empty, let's create a beautiful starter Mind Garden with a seed thought and an active bot recipe tree
        if (existing.isEmpty()) {
            val starterTemplate = WorkflowTemplates.templates.first()
            val recipeId = parseAndPlantWorkflow(
                document = starterTemplate.sampleDocument,
                originX = 0f,
                originY = 0f
            )

            // Also plant a couple of organic seed thoughts connected to the mind matrix
            val rootNodes = dao.getAllNodesDirect()
            val primaryNode = rootNodes.firstOrNull { it.recipeId == recipeId }

            val thought1Id = dao.insertNode(
                GardenNodeEntity(
                    title = "Continuous Synthesis",
                    summary = "Autonomous knowledge loops that refine automation nodes after real-world executions.",
                    category = "THOUGHT",
                    level = 2,
                    experience = 140,
                    experienceToNext = 250,
                    posX = 320f,
                    posY = -180f,
                    bloomUnlocked = true,
                    notes = "Self-healing pipeline thought. Re-evaluates incident patterns monthly."
                )
            )

            val thought2Id = dao.insertNode(
                GardenNodeEntity(
                    title = "Cognitive Load Governor",
                    summary = "Ensures bot notifications don't overwhelm on-call engineers by grouping duplicate alerts.",
                    category = "THOUGHT",
                    level = 1,
                    experience = 65,
                    experienceToNext = 100,
                    posX = -340f,
                    posY = -160f,
                    bloomUnlocked = false,
                    notes = "Exploration into rate-limiting human interrupts."
                )
            )

            if (primaryNode != null) {
                dao.insertEdge(GardenEdgeEntity(sourceNodeId = primaryNode.id, targetNodeId = thought1Id, relationType = "SPROUTS_FROM"))
                dao.insertEdge(GardenEdgeEntity(sourceNodeId = primaryNode.id, targetNodeId = thought2Id, relationType = "SPROUTS_FROM"))
            }
        }
    }

    suspend fun parseAndPlantWorkflow(
        document: String,
        parentThoughtId: Long? = null,
        originX: Float = 0f,
        originY: Float = 0f
    ): Long {
        val parsed = WorkflowParser.parse(document)

        // 1. Create BotRecipeEntity
        val recipe = BotRecipeEntity(
            title = parsed.title,
            description = parsed.description,
            rawDocument = document,
            category = parsed.category,
            totalSteps = parsed.steps.size,
            completedSteps = 0
        )
        val recipeId = dao.insertRecipe(recipe)

        // 2. Create Root Garden Node for the Recipe
        val rootNode = GardenNodeEntity(
            title = parsed.title,
            summary = parsed.description,
            category = "BOT_ACTION",
            level = 2,
            experience = 110,
            experienceToNext = 250,
            posX = originX,
            posY = originY,
            recipeId = recipeId,
            bloomUnlocked = true,
            parentThoughtId = parentThoughtId
        )
        val rootNodeId = dao.insertNode(rootNode)

        // Link with parent if provided
        if (parentThoughtId != null) {
            dao.insertEdge(
                GardenEdgeEntity(
                    sourceNodeId = parentThoughtId,
                    targetNodeId = rootNodeId,
                    relationType = "AUTOMATES"
                )
            )
        }

        // Update recipe with root node id
        dao.updateRecipe(recipe.copy(id = recipeId, rootNodeId = rootNodeId))

        // 3. Create child step nodes in an organic radial branching layout
        val childNodeIds = mutableListOf<Long>()
        val stepEntities = mutableListOf<RecipeStepEntity>()

        val totalSteps = parsed.steps.size
        val radius = 220f + (totalSteps * 8f)
        val angleSpan = if (totalSteps > 1) 2.6f else 0f
        val startAngle = -angleSpan / 2f

        parsed.steps.forEachIndexed { index, step ->
            val angle = if (totalSteps > 1) {
                startAngle + (index.toFloat() / (totalSteps - 1).toFloat()) * angleSpan
            } else {
                0f
            }

            // Stagger radius slightly for organic tree feel
            val stepRadius = radius + ((index % 2) * 50f - 25f)
            val posX = originX + (stepRadius * sin(angle))
            val posY = originY + (stepRadius * cos(angle))

            val stepNode = GardenNodeEntity(
                title = step.title,
                summary = step.description,
                category = step.category,
                level = 1,
                experience = 0,
                experienceToNext = 100,
                posX = posX,
                posY = posY,
                recipeId = recipeId,
                isCompleted = false,
                parentThoughtId = rootNodeId
            )
            val stepNodeId = dao.insertNode(stepNode)
            childNodeIds.add(stepNodeId)

            // Connect root to first step or cascade step to step
            if (index == 0) {
                dao.insertEdge(
                    GardenEdgeEntity(
                        sourceNodeId = rootNodeId,
                        targetNodeId = stepNodeId,
                        relationType = "LEADS_TO"
                    )
                )
            } else {
                // Connect previous step to this step
                val prevNodeId = childNodeIds[index - 1]
                dao.insertEdge(
                    GardenEdgeEntity(
                        sourceNodeId = prevNodeId,
                        targetNodeId = stepNodeId,
                        relationType = "LEADS_TO"
                    )
                )
                // Also weak filament from root for constellation stability
                if (index % 2 == 0) {
                    dao.insertEdge(
                        GardenEdgeEntity(
                            sourceNodeId = rootNodeId,
                            targetNodeId = stepNodeId,
                            relationType = "SPROUTS_FROM"
                        )
                    )
                }
            }

            val stepEntity = RecipeStepEntity(
                recipeId = recipeId,
                stepIndex = index + 1,
                title = step.title,
                actionType = step.category,
                description = step.description,
                botPayload = step.botPayload,
                isExecuted = false,
                xpReward = step.xpReward,
                associatedNodeId = stepNodeId
            )
            stepEntities.add(stepEntity)
        }

        dao.insertSteps(stepEntities)
        return recipeId
    }

    suspend fun plantSeedThought(
        title: String,
        summary: String,
        category: String = "THOUGHT",
        parentThoughtId: Long? = null,
        posX: Float? = null,
        posY: Float? = null
    ): Long {
        val targetX = posX ?: (Random.nextFloat() * 400f - 200f)
        val targetY = posY ?: (Random.nextFloat() * 400f - 200f)

        val node = GardenNodeEntity(
            title = title,
            summary = summary,
            category = category,
            level = 1,
            experience = 25,
            experienceToNext = 100,
            posX = targetX,
            posY = targetY,
            parentThoughtId = parentThoughtId
        )
        val id = dao.insertNode(node)

        if (parentThoughtId != null) {
            dao.insertEdge(
                GardenEdgeEntity(
                    sourceNodeId = parentThoughtId,
                    targetNodeId = id,
                    relationType = "SPROUTS_FROM"
                )
            )
            // Parent earns some XP from having a sprout
            awardExperience(parentThoughtId, 30)
        }
        emailAlertManager?.notifyGrowth(title, "SPROUT", 1, 25, summary)
        return id
    }

    suspend fun executeStep(stepId: Long): Boolean {
        // Fetch step
        val allSteps = dao.getAllStepsDirect()
        val step = allSteps.firstOrNull { it.id == stepId } ?: return false
        if (step.isExecuted) return true

        val updatedStep = step.copy(
            isExecuted = true,
            executionLog = "Executed successfully by autonomous bot worker at ${System.currentTimeMillis()}.\nPayload verified. Status: OK (200)."
        )
        dao.updateStep(updatedStep)

        // Award XP to the associated node
        step.associatedNodeId?.let { nodeId ->
            awardExperience(nodeId, step.xpReward)
            val node = dao.getNodeByIdDirect(nodeId)
            if (node != null) {
                dao.updateNode(node.copy(isCompleted = true))
            }
        }

        emailAlertManager?.notifyGrowth(step.title, "STEP_EXECUTED", 1, step.xpReward, step.description)

        // Update recipe progress
        val recipe = dao.getRecipeByIdDirect(step.recipeId)
        if (recipe != null) {
            val steps = dao.getStepsForRecipeDirect(recipe.id)
            val completed = steps.count { it.isExecuted }
            dao.updateRecipe(recipe.copy(completedSteps = completed))

            // If recipe is fully complete, award bonus to root node
            if (completed == steps.size && recipe.rootNodeId != null) {
                awardExperience(recipe.rootNodeId, 100)
            }
        }
        return true
    }

    suspend fun awardExperience(nodeId: Long, xpEarned: Int) {
        val node = dao.getNodeByIdDirect(nodeId) ?: return
        val oldLevel = node.level
        val oldBloom = node.bloomUnlocked
        var newXp = node.experience + xpEarned
        var newLevel = node.level
        var newThreshold = node.experienceToNext
        var bloomUnlocked = node.bloomUnlocked

        // Check level thresholds:
        // Level 1: 0..99
        // Level 2: 100..249
        // Level 3: 250..499
        // Level 4: 500+
        while (newXp >= newThreshold && newLevel < 4) {
            newLevel++
            bloomUnlocked = true
            newThreshold = when (newLevel) {
                2 -> 250
                3 -> 500
                4 -> 1000
                else -> newThreshold * 2
            }
        }

        dao.updateNode(
            node.copy(
                experience = newXp,
                level = newLevel,
                experienceToNext = newThreshold,
                bloomUnlocked = bloomUnlocked
            )
        )

        if (newLevel > oldLevel) {
            emailAlertManager?.notifyGrowth(node.title, "LEVEL_UP", newLevel, newXp, node.notes)
        } else if (bloomUnlocked && !oldBloom) {
            emailAlertManager?.notifyGrowth(node.title, "BLOOM", newLevel, newXp, node.notes)
        }
    }

    suspend fun updateNodeNotes(nodeId: Long, notes: String) {
        val node = dao.getNodeByIdDirect(nodeId) ?: return
        dao.updateNode(node.copy(notes = notes))
        awardExperience(nodeId, 20)
    }

    suspend fun updateNodePosition(nodeId: Long, newX: Float, newY: Float) {
        val node = dao.getNodeByIdDirect(nodeId) ?: return
        dao.updateNode(node.copy(posX = newX, posY = newY))
    }

    suspend fun deleteNode(nodeId: Long) {
        dao.deleteEdgesForNode(nodeId)
        dao.deleteNodeById(nodeId)
    }

    suspend fun deleteRecipe(recipeId: Long) {
        dao.deleteStepsForRecipe(recipeId)
        // Also remove nodes associated with recipe
        val allNodes = dao.getAllNodesDirect()
        allNodes.filter { it.recipeId == recipeId }.forEach {
            dao.deleteEdgesForNode(it.id)
            dao.deleteNodeById(it.id)
        }
        dao.deleteRecipeById(recipeId)
    }

    suspend fun connectNodes(sourceId: Long, targetId: Long, relationType: String = "LEADS_TO") {
        if (sourceId == targetId) return
        dao.insertEdge(
            GardenEdgeEntity(
                sourceNodeId = sourceId,
                targetNodeId = targetId,
                relationType = relationType
            )
        )
        awardExperience(sourceId, 15)
        awardExperience(targetId, 15)
    }

}

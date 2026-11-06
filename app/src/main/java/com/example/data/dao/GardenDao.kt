package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.BotRecipeEntity
import com.example.data.entity.GardenEdgeEntity
import com.example.data.entity.GardenNodeEntity
import com.example.data.entity.RecipeStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {
    @Query("SELECT * FROM garden_nodes ORDER BY id ASC")
    fun getAllNodes(): Flow<List<GardenNodeEntity>>

    @Query("SELECT * FROM garden_nodes")
    suspend fun getAllNodesDirect(): List<GardenNodeEntity>

    @Query("SELECT * FROM garden_nodes WHERE id = :id LIMIT 1")
    fun getNodeById(id: Long): Flow<GardenNodeEntity?>

    @Query("SELECT * FROM garden_nodes WHERE id = :id LIMIT 1")
    suspend fun getNodeByIdDirect(id: Long): GardenNodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: GardenNodeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<GardenNodeEntity>): List<Long>

    @Update
    suspend fun updateNode(node: GardenNodeEntity)

    @Delete
    suspend fun deleteNode(node: GardenNodeEntity)

    @Query("DELETE FROM garden_nodes WHERE id = :nodeId")
    suspend fun deleteNodeById(nodeId: Long)

    @Query("SELECT * FROM garden_edges")
    fun getAllEdges(): Flow<List<GardenEdgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdge(edge: GardenEdgeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdges(edges: List<GardenEdgeEntity>): List<Long>

    @Query("DELETE FROM garden_edges WHERE sourceNodeId = :nodeId OR targetNodeId = :nodeId")
    suspend fun deleteEdgesForNode(nodeId: Long)

    @Query("SELECT * FROM bot_recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<BotRecipeEntity>>

    @Query("SELECT * FROM bot_recipes ORDER BY createdAt DESC")
    suspend fun getAllRecipesDirect(): List<BotRecipeEntity>

    @Query("SELECT * FROM bot_recipes WHERE id = :id LIMIT 1")
    fun getRecipeById(id: Long): Flow<BotRecipeEntity?>

    @Query("SELECT * FROM bot_recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeByIdDirect(id: Long): BotRecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: BotRecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: BotRecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: BotRecipeEntity)

    @Query("DELETE FROM bot_recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)

    @Query("SELECT * FROM recipe_steps ORDER BY id ASC")
    suspend fun getAllStepsDirect(): List<RecipeStepEntity>

    @Query("SELECT * FROM recipe_steps WHERE recipeId = :recipeId ORDER BY stepIndex ASC")
    fun getStepsForRecipe(recipeId: Long): Flow<List<RecipeStepEntity>>

    @Query("SELECT * FROM recipe_steps WHERE recipeId = :recipeId ORDER BY stepIndex ASC")
    suspend fun getStepsForRecipeDirect(recipeId: Long): List<RecipeStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<RecipeStepEntity>): List<Long>

    @Update
    suspend fun updateStep(step: RecipeStepEntity)

    @Query("DELETE FROM recipe_steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsForRecipe(recipeId: Long)

    @Query("DELETE FROM garden_nodes")
    suspend fun clearNodes()

    @Query("DELETE FROM garden_edges")
    suspend fun clearEdges()

    @Query("DELETE FROM bot_recipes")
    suspend fun clearRecipes()

    @Query("DELETE FROM recipe_steps")
    suspend fun clearSteps()
}

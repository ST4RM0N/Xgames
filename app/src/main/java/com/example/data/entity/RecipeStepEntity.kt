package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_steps")
data class RecipeStepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val stepIndex: Int,
    val title: String,
    val actionType: String, // "TRIGGER", "BOT_EXECUTE", "CONDITION_CHECK", "API_DISPATCH", "NOTIFY", "VALIDATION"
    val description: String,
    val botPayload: String = "",
    val isExecuted: Boolean = false,
    val xpReward: Int = 35,
    val associatedNodeId: Long? = null,
    val executionLog: String = ""
)

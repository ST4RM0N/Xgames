package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bot_recipes")
data class BotRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val rawDocument: String,
    val category: String = "Automation",
    val rootNodeId: Long? = null,
    val totalSteps: Int = 0,
    val completedSteps: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_nodes")
data class GardenNodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val summary: String,
    val category: String, // "THOUGHT", "TRIGGER", "BOT_ACTION", "DECISION", "VALIDATION"
    val level: Int = 1, // 1: Sprout, 2: Sapling, 3: Blooming, 4: Arbor
    val experience: Int = 0,
    val experienceToNext: Int = 100,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val recipeId: Long? = null,
    val isCompleted: Boolean = false,
    val bloomUnlocked: Boolean = false,
    val parentThoughtId: Long? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

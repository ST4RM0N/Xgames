package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_edges")
data class GardenEdgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceNodeId: Long,
    val targetNodeId: Long,
    val relationType: String = "LEADS_TO", // "LEADS_TO", "AUTOMATES", "SPROUTS_FROM", "REQUIRES"
    val energyFlowActive: Boolean = true
)

package com.example.data.alert

data class EmailPassthroughConfig(
    val enabled: Boolean = true,
    val recipientEmail: String = "ItsKoolKidd@gmail.com",
    val alertOnLevelUp: Boolean = true,
    val alertOnBloom: Boolean = true,
    val alertOnSprout: Boolean = true,
    val alertOnStepExecuted: Boolean = true
)

data class GrowthAlert(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val nodeTitle: String,
    val eventType: String, // "LEVEL_UP", "BLOOM", "SPROUT", "STEP_EXECUTED"
    val subject: String,
    val body: String,
    val recipient: String,
    val deliveredVia: String = "IN_APP_PASSTHROUGH"
)

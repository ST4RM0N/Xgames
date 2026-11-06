package com.example.data.alert

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmailAlertManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("grokpot_email_passthrough", Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<EmailPassthroughConfig> = _config.asStateFlow()

    private val _alerts = MutableStateFlow<List<GrowthAlert>>(emptyList())
    val alerts: StateFlow<List<GrowthAlert>> = _alerts.asStateFlow()

    private val _latestAlert = MutableStateFlow<GrowthAlert?>(null)
    val latestAlert: StateFlow<GrowthAlert?> = _latestAlert.asStateFlow()

    private fun loadConfig(): EmailPassthroughConfig {
        return EmailPassthroughConfig(
            enabled = prefs.getBoolean("enabled", true),
            recipientEmail = prefs.getString("recipientEmail", "ItsKoolKidd@gmail.com") ?: "ItsKoolKidd@gmail.com",
            alertOnLevelUp = prefs.getBoolean("alertOnLevelUp", true),
            alertOnBloom = prefs.getBoolean("alertOnBloom", true),
            alertOnSprout = prefs.getBoolean("alertOnSprout", true),
            alertOnStepExecuted = prefs.getBoolean("alertOnStepExecuted", true)
        )
    }

    fun updateConfig(newConfig: EmailPassthroughConfig) {
        prefs.edit()
            .putBoolean("enabled", newConfig.enabled)
            .putString("recipientEmail", newConfig.recipientEmail)
            .putBoolean("alertOnLevelUp", newConfig.alertOnLevelUp)
            .putBoolean("alertOnBloom", newConfig.alertOnBloom)
            .putBoolean("alertOnSprout", newConfig.alertOnSprout)
            .putBoolean("alertOnStepExecuted", newConfig.alertOnStepExecuted)
            .apply()
        _config.value = newConfig
    }

    fun clearLatestAlert() {
        _latestAlert.value = null
    }

    fun notifyGrowth(
        nodeTitle: String,
        eventType: String,
        level: Int,
        xp: Int,
        notes: String = ""
    ): GrowthAlert? {
        val currentConfig = _config.value
        if (!currentConfig.enabled) return null

        val shouldAlert = when (eventType) {
            "LEVEL_UP" -> currentConfig.alertOnLevelUp
            "BLOOM" -> currentConfig.alertOnBloom
            "SPROUT" -> currentConfig.alertOnSprout
            "STEP_EXECUTED" -> currentConfig.alertOnStepExecuted
            else -> true
        }

        if (!shouldAlert) return null

        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())

        val eventLabel = when (eventType) {
            "LEVEL_UP" -> "Leveled Up to Level $level"
            "BLOOM" -> "Reached Bioluminescent Bloom"
            "SPROUT" -> "New Pathway Sprouted"
            "STEP_EXECUTED" -> "Automation Step Executed"
            else -> "Mind Garden Growth Event"
        }

        val subject = "[Grokpot Growth Alert] $nodeTitle - $eventLabel"

        val body = """
Growth Event: $eventLabel
Node Title: $nodeTitle
Experience: $xp XP
Timestamp: $formattedDate
Recipient: ${currentConfig.recipientEmail}

--- Operational Context ---
${if (notes.isNotBlank()) "Notes: $notes\n" else ""}
This growth alert was routed through Grokpot's automated email passthrough.
Your mind garden and bot recipe trees have updated and expanded in real-time.
        """.trimIndent()

        val alert = GrowthAlert(
            nodeTitle = nodeTitle,
            eventType = eventType,
            subject = subject,
            body = body,
            recipient = currentConfig.recipientEmail,
            deliveredVia = "EMAIL_PASSTHROUGH"
        )

        _alerts.value = listOf(alert) + _alerts.value.take(49)
        _latestAlert.value = alert
        return alert
    }

    fun createEmailIntent(alert: GrowthAlert): Intent {
        val uri = Uri.parse("mailto:${alert.recipient}")
        return Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(alert.recipient))
            putExtra(Intent.EXTRA_SUBJECT, alert.subject)
            putExtra(Intent.EXTRA_TEXT, alert.body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}

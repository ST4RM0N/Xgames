package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.alert.EmailAlertManager
import com.example.data.alert.EmailPassthroughConfig
import com.example.data.alert.GrowthAlert
import com.example.ui.theme.BioAmber
import com.example.ui.theme.BioBackground
import com.example.ui.theme.BioCyan
import com.example.ui.theme.BioCyanGlow
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.BioEmeraldGlow
import com.example.ui.theme.BioSurface
import com.example.ui.theme.BioSurfaceHigh
import com.example.ui.theme.BioSurfaceVariant
import com.example.ui.theme.BioTextMuted
import com.example.ui.theme.BioTextPrimary
import com.example.ui.theme.BioTextSecondary
import com.example.ui.theme.BioViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailPassthroughSheet(
    config: EmailPassthroughConfig,
    alerts: List<GrowthAlert>,
    onUpdateConfig: (EmailPassthroughConfig) -> Unit,
    onSendTestAlert: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var enabled by remember(config.enabled) { mutableStateOf(config.enabled) }
    var emailInput by remember(config.recipientEmail) { mutableStateOf(config.recipientEmail) }
    var onLevelUp by remember(config.alertOnLevelUp) { mutableStateOf(config.alertOnLevelUp) }
    var onBloom by remember(config.alertOnBloom) { mutableStateOf(config.alertOnBloom) }
    var onSprout by remember(config.alertOnSprout) { mutableStateOf(config.alertOnSprout) }
    var onStep by remember(config.alertOnStepExecuted) { mutableStateOf(config.alertOnStepExecuted) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BioSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("email_passthrough_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BioCyan.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = BioCyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Email Passthrough Alerts",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BioTextPrimary
                            )
                        )
                        Text(
                            text = "Real-time notifications on garden growth",
                            style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BioTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Enable Toggle Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BioSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BioSurfaceHigh, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Growth Alert Passthrough",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BioTextPrimary
                                )
                            )
                            Text(
                                text = "Dispatches email alerts whenever thoughts level up, bloom, or sprout.",
                                style = MaterialTheme.typography.bodySmall.copy(color = BioTextSecondary)
                            )
                        }

                        Switch(
                            checked = enabled,
                            onCheckedChange = {
                                enabled = it
                                onUpdateConfig(
                                    config.copy(
                                        enabled = it,
                                        recipientEmail = emailInput,
                                        alertOnLevelUp = onLevelUp,
                                        alertOnBloom = onBloom,
                                        alertOnSprout = onSprout,
                                        alertOnStepExecuted = onStep
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BioEmerald,
                                checkedTrackColor = BioEmerald.copy(alpha = 0.3f),
                                uncheckedThumbColor = BioTextMuted,
                                uncheckedTrackColor = BioSurfaceHigh
                            ),
                            modifier = Modifier.testTag("email_passthrough_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            onUpdateConfig(
                                config.copy(
                                    enabled = enabled,
                                    recipientEmail = it,
                                    alertOnLevelUp = onLevelUp,
                                    alertOnBloom = onBloom,
                                    alertOnSprout = onSprout,
                                    alertOnStepExecuted = onStep
                                )
                            )
                        },
                        label = { Text("Recipient Email Address") },
                        placeholder = { Text("e.g. ItsKoolKidd@gmail.com") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = BioCyan)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BioTextPrimary,
                            unfocusedTextColor = BioTextPrimary,
                            focusedBorderColor = BioCyan,
                            unfocusedBorderColor = BioSurfaceHigh,
                            focusedLabelColor = BioCyan,
                            unfocusedLabelColor = BioTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recipient_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ALERT TRIGGERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BioCyanGlow,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AlertCheckboxRow(
                        label = "When a thought levels up (XP Threshold reached)",
                        checked = onLevelUp,
                        onCheckedChange = {
                            onLevelUp = it
                            onUpdateConfig(config.copy(alertOnLevelUp = it))
                        }
                    )

                    AlertCheckboxRow(
                        label = "When a node blooms and unlocks new branches",
                        checked = onBloom,
                        onCheckedChange = {
                            onBloom = it
                            onUpdateConfig(config.copy(alertOnBloom = it))
                        }
                    )

                    AlertCheckboxRow(
                        label = "When a new seed thought or recipe sprouts",
                        checked = onSprout,
                        onCheckedChange = {
                            onSprout = it
                            onUpdateConfig(config.copy(alertOnSprout = it))
                        }
                    )

                    AlertCheckboxRow(
                        label = "When an automated bot step executes successfully",
                        checked = onStep,
                        onCheckedChange = {
                            onStep = it
                            onUpdateConfig(config.copy(alertOnStepExecuted = it))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Test Alert Button
            Button(
                onClick = onSendTestAlert,
                colors = ButtonDefaults.buttonColors(containerColor = BioCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_test_alert_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = Color(0xFF00363D),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dispatch Test Growth Alert Email",
                    color = Color(0xFF00363D),
                    fontWeight = FontWeight.Bold
                )
            }

            // Recent Alerts Log
            if (alerts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RECENT GROWTH PASSTHROUGH ALERTS (${alerts.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BioTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(180.dp)
                ) {
                    items(alerts.take(10), key = { it.id }) { alert ->
                        Surface(
                            color = BioSurfaceHigh,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = BioAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.subject,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = BioTextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Routed to ${alert.recipient}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = BioTextMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = BioEmerald,
                uncheckedColor = BioTextMuted,
                checkmarkColor = Color(0xFF002A19)
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = BioTextPrimary)
        )
    }
}

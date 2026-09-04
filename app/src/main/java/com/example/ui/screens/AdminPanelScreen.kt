package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    currentUser: User,
    onSwitchUser: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var rainfallThreshold by remember { mutableFloatStateOf(50f) }
    var slopeRiskSensitivity by remember { mutableFloatStateOf(75f) }
    var autoRerouteEnabled by remember { mutableStateOf(true) }
    var smsAlertsEnabled by remember { mutableStateOf(true) }
    var offlineSyncIntervalMinutes by remember { mutableFloatStateOf(15f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CyanHighlight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ADMINISTRATION & SYSTEM CONFIGURATION",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                }
                Text(
                    text = "Manage user credentials, AI threshold triggers, vehicle registries & regional emergency contacts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Active User & Quick Switch Role
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACTIVE SESSION IDENTITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${currentUser.email} • ${currentUser.role.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, BlueAccent)
                        ) {
                            Text(
                                text = currentUser.role.badge,
                                color = BlueAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "SWITCH USER PROFILE (DEMO SIMULATION)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    MockDataProvider.demoUsers.forEach { user ->
                        val isCurrent = user.id == currentUser.id
                        Surface(
                            onClick = { onSwitchUser(user) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) Color(0xFFF1F5F9) else Color.Transparent,
                            border = BorderStroke(0.8.dp, if (isCurrent) CyanHighlight else SlateBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${user.name} (${user.role.displayName})",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = TextPrimary
                                )
                                if (isCurrent) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CyanHighlight, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // AI Alert & Risk Threshold Configuration
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI RISK ENGINE THRESHOLD SENSITIVITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(14.dp))

                    // Rainfall threshold slider
                    Text(
                        text = "Rainfall Auto-Alert Threshold: ${rainfallThreshold.toInt()} mm/h",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Slider(
                        value = rainfallThreshold,
                        onValueChange = { rainfallThreshold = it },
                        valueRange = 20f..120f,
                        colors = SliderDefaults.colors(thumbColor = CyanHighlight, activeTrackColor = CyanHighlight)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Slope sensitivity
                    Text(
                        text = "Steep Slope Landslide Sensitivity: ${slopeRiskSensitivity.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Slider(
                        value = slopeRiskSensitivity,
                        onValueChange = { slopeRiskSensitivity = it },
                        valueRange = 40f..100f,
                        colors = SliderDefaults.colors(thumbColor = RiskHighOrange, activeTrackColor = RiskHighOrange)
                    )

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(Modifier.height(10.dp))

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic Safe Rerouting Suggestions", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                            Text("Notify operators instantly when risk > 70", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(checked = autoRerouteEnabled, onCheckedChange = { autoRerouteEnabled = it })
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Driver Emergency SMS Broadcast", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                            Text("Low-bandwidth SMS fallback for remote valley drivers", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(checked = smsAlertsEnabled, onCheckedChange = { smsAlertsEnabled = it })
                    }
                }
            }
        }

        // System Health & Data Synchronizer
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SYSTEM ARCHITECTURE HEALTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    HealthRow("PostGIS Spatial Database", "ONLINE • Latency 14ms", RiskLowGreen)
                    HealthRow("Scikit-learn / XGBoost Risk Service", "OPERATIONAL • v2.4.1", RiskLowGreen)
                    HealthRow("IMD Regional Weather Feeds", "CONNECTED • Updated 2m ago", RiskLowGreen)
                    HealthRow("PWD Road Disruption Sentinel", "SYNCED • 3 Incidents Logged", RiskLowGreen)
                    HealthRow("Satellite Link / Low-Bandwidth Gateway", "STANDBY • Ready for failover", BlueAccent)
                }
            }
        }
    }
}

@Composable
fun HealthRow(system: String, status: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = system, fontSize = 11.5.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(Modifier.width(5.dp))
            Text(text = status, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

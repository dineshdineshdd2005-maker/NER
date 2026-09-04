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
import com.example.model.AlertItem
import com.example.model.RiskLevel
import com.example.ui.components.AlertBadge
import com.example.ui.theme.*

@Composable
fun AlertsCenterScreen(
    alerts: List<AlertItem>,
    onAcknowledge: (String) -> Unit,
    onReroute: (String) -> Unit,
    onViewRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<RiskLevel?>(null) }

    val filteredAlerts = if (selectedFilter == null) alerts else alerts.filter { it.severity == selectedFilter }

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
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = RiskCriticalRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "REAL-TIME ALERT SYSTEM",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                }
                Text(
                    text = "Automated threat detection, threshold triggers & rapid dispatch emergency alerts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Filter Bar (All, Critical, High, Medium, Low)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All (${alerts.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == RiskLevel.CRITICAL,
                    onClick = { selectedFilter = if (selectedFilter == RiskLevel.CRITICAL) null else RiskLevel.CRITICAL },
                    label = { Text("Critical (${alerts.count { it.severity == RiskLevel.CRITICAL }})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == RiskLevel.HIGH,
                    onClick = { selectedFilter = if (selectedFilter == RiskLevel.HIGH) null else RiskLevel.HIGH },
                    label = { Text("High (${alerts.count { it.severity == RiskLevel.HIGH }})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == RiskLevel.MEDIUM,
                    onClick = { selectedFilter = if (selectedFilter == RiskLevel.MEDIUM) null else RiskLevel.MEDIUM },
                    label = { Text("Medium (${alerts.count { it.severity == RiskLevel.MEDIUM }})", fontSize = 11.sp) }
                )
            }
        }

        items(filteredAlerts.size) { index ->
            val alert = filteredAlerts[index]
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    if (alert.isAcknowledged) SlateBorder else when (alert.severity) {
                        RiskLevel.CRITICAL -> RiskCriticalRed
                        RiskLevel.HIGH -> RiskHighOrange
                        RiskLevel.MEDIUM -> RiskMediumYellow
                        RiskLevel.LOW -> RiskLowGreen
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlertBadge(level = alert.severity)
                        Text(
                            text = alert.timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = alert.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = CyanHighlight, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = alert.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(Modifier.height(10.dp))

                    // Action Buttons (View Route, Reroute Vehicle, Acknowledge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (alert.relatedVehicleId != null && !alert.isAcknowledged) {
                            Button(
                                onClick = { onReroute(alert.relatedVehicleId) },
                                colors = ButtonDefaults.buttonColors(containerColor = RiskLowGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.AltRoute, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Reroute Vehicle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onViewRoute,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("View Route", fontSize = 11.sp)
                        }

                        if (!alert.isAcknowledged) {
                            TextButton(
                                onClick = { onAcknowledge(alert.id) },
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Acknowledge", fontSize = 11.sp, color = TextMuted)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RiskLowGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Acknowledged", fontSize = 11.sp, color = RiskLowGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

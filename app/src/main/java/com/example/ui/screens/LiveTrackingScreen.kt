package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Vehicle
import com.example.model.VehicleStatus
import com.example.ui.components.AlertBadge
import com.example.ui.components.InteractiveGisMapView
import com.example.ui.theme.*

@Composable
fun LiveTrackingScreen(
    vehicles: List<Vehicle>,
    selectedVehicleId: String?,
    isSimulating: Boolean,
    onToggleSimulation: () -> Unit,
    onSelectVehicle: (String) -> Unit,
    onRerouteVehicle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedVehicle = vehicles.find { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Control Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = CyanHighlight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "LIVE VEHICLE TRACKING",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }
                    Text(
                        text = "Real-time automated telemetry, GPS tracking & route integrity monitor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Simulate Vehicle Movement Button (Mandatory as per prompt)
                Button(
                    onClick = onToggleSimulation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSimulating) RiskHighOrange else RiskLowGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isSimulating) "STOP GPS SIMULATION" else "Simulate Vehicle Movement",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Simulation Status Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSimulating) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, if (isSimulating) BlueAccent else SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isSimulating) Color(0xFF3B82F6) else TextMuted)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isSimulating)
                            "ACTIVE SIMULATION: Real-time GPS coordinates updating at 1.5s interval. Vehicle speed and progress dynamic."
                        else
                            "TELEMETRY READY: Press 'Simulate Vehicle Movement' to simulate live mountain driving progress.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Selected Vehicle Highlight Card (Example NER-TRUCK-104)
        selectedVehicle?.let { vehicle ->
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyanHighlight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = vehicle.id,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Driver: ${vehicle.driverName} • ${vehicle.driverPhone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            AlertBadge(level = vehicle.routeRisk)
                        }

                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFF1E293B))
                        Spacer(Modifier.height(12.dp))

                        // Grid of Telemetry Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TelemetryCell("Current Location", vehicle.currentLocation, Color.White)
                            TelemetryCell("Speed", "${vehicle.speedKmh} km/h", CyanHighlight)
                            TelemetryCell("ETA", vehicle.eta, Color(0xFF34D399))
                            TelemetryCell("Status", vehicle.status.label, if (vehicle.status == VehicleStatus.REROUTED) RiskLowGreen else Color.White)
                        }

                        Spacer(Modifier.height(12.dp))

                        // Progress Along Corridor
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Route Transit Progress",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${(vehicle.progressPercent * 100).toInt()}% towards ${vehicle.destination}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { vehicle.progressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyanHighlight,
                                trackColor = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }

        // Fleet Vehicle List
        item {
            Text(
                text = "ALL ACTIVE FLEET VEHICLES IN SECTOR",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }

        items(vehicles.size) { index ->
            val v = vehicles[index]
            val isSelected = v.id == selectedVehicleId

            Card(
                onClick = { onSelectVehicle(v.id) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) CyanHighlight else SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (v.status == VehicleStatus.REROUTED) RiskLowGreen else BlueAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${v.id} (${v.type.take(18)})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Loc: ${v.currentLocation} • Speed: ${v.speedKmh} km/h • ETA: ${v.eta}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Cargo: ${v.cargo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        AlertBadge(level = v.routeRisk)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = v.status.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (v.status == VehicleStatus.REROUTED) RiskLowGreen else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryCell(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, fontSize = 9.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

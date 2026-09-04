package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RiskLevel
import com.example.model.WeatherZone
import com.example.ui.components.AlertBadge
import com.example.ui.theme.*

@Composable
fun WeatherDisasterScreen(
    weatherZones: List<WeatherZone>,
    onTriggerDisasterSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Thunderstorm,
                            contentDescription = null,
                            tint = RiskHighOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "WEATHER & DISASTER INTELLIGENCE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }
                    Text(
                        text = "Real-time precipitation radar, CWC hydrological river telemetry & geological mass-wasting alerts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onTriggerDisasterSimulation,
                    colors = ButtonDefaults.buttonColors(containerColor = RiskCriticalRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("SIMULATE RAIN SURGE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // The 4 Specific Critical Threat Indicator Cards (Mandatory as per user prompt)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThreatCard(
                        title = "HEAVY RAINFALL",
                        value = "62.4 mm/h",
                        severity = RiskLevel.CRITICAL,
                        status = "Monsoon Cloud Burst",
                        icon = Icons.Default.WaterDrop,
                        modifier = Modifier.weight(1f)
                    )
                    ThreatCard(
                        title = "LANDSLIDE RISK",
                        value = "86% Prob.",
                        severity = RiskLevel.CRITICAL,
                        status = "High Rockfall Hazard",
                        icon = Icons.Default.Terrain,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThreatCard(
                        title = "FLOOD RISK",
                        value = "+1.8m Inund.",
                        severity = RiskLevel.HIGH,
                        status = "Submerged Lowlands",
                        icon = Icons.Default.Flood,
                        modifier = Modifier.weight(1f)
                    )
                    ThreatCard(
                        title = "ROAD BLOCKAGE",
                        value = "3 Corridors",
                        severity = RiskLevel.CRITICAL,
                        status = "NH-13 Bomdila Km 142",
                        icon = Icons.Default.Block,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Regional Risk Level Color Legend
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RiskZoneLegend(RiskLowGreen, "Green", "Safe (<20%)")
                    RiskZoneLegend(RiskMediumYellow, "Yellow", "Moderate (20-50%)")
                    RiskZoneLegend(RiskHighOrange, "Orange", "High (50-75%)")
                    RiskZoneLegend(RiskCriticalRed, "Red", "Critical (>75%)")
                }
            }
        }

        // Regional Weather Zones Cards List
        item {
            Text(
                text = "REGIONAL WEATHER TELEMETRY BY SECTOR",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }

        items(weatherZones.size) { index ->
            val zone = weatherZones[index]
            WeatherZoneDetailCard(zone = zone)
        }
    }
}

@Composable
fun ThreatCard(
    title: String,
    value: String,
    severity: RiskLevel,
    status: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                AlertBadge(level = severity)
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = when (severity) {
                        RiskLevel.CRITICAL -> RiskCriticalRed
                        RiskLevel.HIGH -> RiskHighOrange
                        RiskLevel.MEDIUM -> RiskMediumYellow
                        RiskLevel.LOW -> RiskLowGreen
                    },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 10.5.sp
            )
        }
    }
}

@Composable
fun WeatherZoneDetailCard(zone: WeatherZone) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = zone.regionName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Road Condition: ${zone.roadCondition}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                AlertBadge(level = zone.riskLevel)
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = SlateBorder)
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherMetricItem("Rainfall", "${zone.rainfallMmPerHour} mm/h", Icons.Default.WaterDrop)
                WeatherMetricItem("Temperature", "${zone.temperatureCelsius}°C", Icons.Default.Thermostat)
                WeatherMetricItem("Wind Speed", "${zone.windKmh} km/h", Icons.Default.Air)
                WeatherMetricItem("Landslide Prob.", "${zone.landslideProbabilityPercent}%", Icons.Default.Terrain)
            }
        }
    }
}

@Composable
fun WeatherMetricItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = CyanHighlight, modifier = Modifier.size(13.dp))
            Spacer(Modifier.width(3.dp))
            Text(text = label, fontSize = 9.sp, color = TextMuted)
        }
        Spacer(Modifier.height(2.dp))
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun RiskZoneLegend(color: Color, name: String, range: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(Modifier.width(4.dp))
        Column {
            Text(text = name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = range, fontSize = 8.5.sp, color = TextMuted)
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.*
import com.example.ui.components.InteractiveGisMapView
import com.example.ui.theme.*

@Composable
fun GisMapScreen(
    vehicles: List<Vehicle>,
    alerts: List<AlertItem>,
    fieldReports: List<FieldReport>,
    filters: MapFilterState,
    isRoadBlocked: Boolean,
    selectedRouteId: String,
    onToggleFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = CyanHighlight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "GIS MAP & RISK VISUALIZATION",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                }
                Text(
                    text = "High-precision geographic topology covering the 8 North Eastern states.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Layer Filter Checkbox Chips (All 7 required by user prompt: Vehicles, Weather, Landslides, Floods, Road Blocks, Risk Zones, Field Reports)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    selected = filters.showVehicles,
                    onClick = { onToggleFilter("vehicles") },
                    label = { Text("Vehicles", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(14.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = filters.showWeather,
                    onClick = { onToggleFilter("weather") },
                    label = { Text("Weather", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Cloud, null, modifier = Modifier.size(14.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = filters.showLandslides,
                    onClick = { onToggleFilter("landslides") },
                    label = { Text("Landslides", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Terrain, null, modifier = Modifier.size(14.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = filters.showFloods,
                    onClick = { onToggleFilter("floods") },
                    label = { Text("Floods", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Water, null, modifier = Modifier.size(14.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = filters.showRoadBlocks,
                    onClick = { onToggleFilter("roadblocks") },
                    label = { Text("Road Blocks", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Block, null, modifier = Modifier.size(14.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = filters.showRiskZones,
                    onClick = { onToggleFilter("riskzones") },
                    label = { Text("Risk Zones", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Layers, null, modifier = Modifier.size(14.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = filters.showFieldReports,
                    onClick = { onToggleFilter("fieldreports") },
                    label = { Text("Field Reports", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Assignment, null, modifier = Modifier.size(14.dp)) }
                )
            }
        }

        // Full Screen GIS Interactive Map View
        InteractiveGisMapView(
            vehicles = vehicles,
            alerts = alerts,
            fieldReports = fieldReports,
            filters = filters,
            isRoadBlocked = isRoadBlocked,
            selectedRouteId = selectedRouteId,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Bottom Helper Bar
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tip: Pinch to zoom or drag to pan across the Himalayan corridors. Tap any city node for sector telemetry.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

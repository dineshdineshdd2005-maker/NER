package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    vehicles: List<Vehicle>,
    alerts: List<AlertItem>,
    fieldReports: List<FieldReport>,
    mapFilters: MapFilterState,
    isRoadBlocked: Boolean,
    selectedRouteId: String,
    weatherZones: List<WeatherZone> = MockDataProvider.weatherZones,
    deliveries: List<DeliveryItem> = MockDataProvider.initialDeliveries,
    activeDeliveryId: String? = null,
    onSelectDelivery: ((String) -> Unit)? = null,
    onFilterToggle: (String) -> Unit,
    onAcknowledgeAlert: (String) -> Unit,
    onRerouteVehicle: (String) -> Unit,
    onNavigateToOptimizer: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isGoogleMapsMode by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Mission Statement
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NER-LOGIX COMMAND CENTER",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                                color = RiskLowGreen
                            ) {
                                Text(
                                    text = "LIVE GIS",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Predictive & Disaster-Resilient Logistics for North Eastern States",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = onNavigateToDemo,
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAi),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("START LIVE DEMO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Top 6 Statistics Cards (Requested specifically in user prompt)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Active Vehicles",
                        value = "14",
                        icon = Icons.Default.DirectionsCar,
                        accentColor = CyanHighlight,
                        subtext = "Fleet in transit",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Routes Monitored",
                        value = "28",
                        icon = Icons.Default.AltRoute,
                        accentColor = BlueAccent,
                        subtext = "Trans-Arunachal/Assam",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "High Risk Routes",
                        value = "6",
                        icon = Icons.Default.WarningAmber,
                        accentColor = RiskHighOrange,
                        subtext = "Rainfall > 50mm",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Active Alerts",
                        value = "${alerts.count { !it.isAcknowledged }}",
                        icon = Icons.Default.NotificationsActive,
                        accentColor = RiskCriticalRed,
                        subtext = "Immediate action req.",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Deliveries in Transit",
                        value = "12",
                        icon = Icons.Default.LocalShipping,
                        accentColor = RiskLowGreen,
                        subtext = "Medical & emergency rations",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Roads Blocked",
                        value = if (isRoadBlocked) "3" else "2",
                        icon = Icons.Default.Block,
                        accentColor = RiskCriticalRed,
                        subtext = "NH-13 Bomdila sector",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Main Dashboard Feature: Interactive GIS Map of North Eastern India
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isGoogleMapsMode) "GOOGLE MAPS REGIONAL CORRIDOR" else "GIS RADAR INTELLIGENCE MAP",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(6.dp))
                                if (isGoogleMapsMode) {
                                    Surface(
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                                        color = BlueAccent.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BlueAccent)
                                    ) {
                                        Text(
                                            text = "COMPOSE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BlueAccent,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "North Eastern Corridor • Vehicle Locations & Weather Hazard Markers",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Map Engine Switcher: Google Maps Compose vs GIS Radar
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = SlateBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                Surface(
                                    onClick = { isGoogleMapsMode = true },
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                                    color = if (isGoogleMapsMode) BlueAccent else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = if (isGoogleMapsMode) Color.White else TextMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Google Maps",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isGoogleMapsMode) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isGoogleMapsMode) Color.White else TextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    onClick = { isGoogleMapsMode = false },
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                                    color = if (!isGoogleMapsMode) NavyDark else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Radar,
                                            contentDescription = null,
                                            tint = if (!isGoogleMapsMode) CyanHighlight else TextMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "GIS Radar",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (!isGoogleMapsMode) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!isGoogleMapsMode) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(onClick = onNavigateToTracking) {
                            Icon(Icons.Default.Fullscreen, contentDescription = "Full Map", tint = CyanHighlight)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Map Container: Google Maps Compose Placeholder vs GIS Radar
                    if (isGoogleMapsMode) {
                        GoogleMapsPlaceholderView(
                            vehicles = vehicles,
                            alerts = alerts,
                            fieldReports = fieldReports,
                            filters = mapFilters,
                            isRoadBlocked = isRoadBlocked,
                            selectedRouteId = selectedRouteId,
                            weatherZones = weatherZones,
                            deliveries = deliveries,
                            activeDeliveryId = activeDeliveryId,
                            onSelectDelivery = onSelectDelivery,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp)
                        )
                    } else {
                        InteractiveGisMapView(
                            vehicles = vehicles,
                            alerts = alerts,
                            fieldReports = fieldReports,
                            filters = mapFilters,
                            isRoadBlocked = isRoadBlocked,
                            selectedRouteId = selectedRouteId,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Map Filter Toggles (Checkboxes)
                    Text(
                        text = "GIS MAP OVERLAY FILTERS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = mapFilters.showVehicles,
                            onClick = { onFilterToggle("vehicles") },
                            label = { Text("Vehicles", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(14.dp)) }
                        )
                        FilterChip(
                            selected = mapFilters.showWeather,
                            onClick = { onFilterToggle("weather") },
                            label = { Text("Weather", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Cloud, null, modifier = Modifier.size(14.dp)) }
                        )
                        FilterChip(
                            selected = mapFilters.showLandslides,
                            onClick = { onFilterToggle("landslides") },
                            label = { Text("Landslides", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Terrain, null, modifier = Modifier.size(14.dp)) }
                        )
                        FilterChip(
                            selected = mapFilters.showRoadBlocks,
                            onClick = { onFilterToggle("roadblocks") },
                            label = { Text("Blocks", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Block, null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }

        // Right-Side / Integrated Real-Time Alert Panel
        item {
            RealTimeAlertPanel(
                alerts = alerts,
                onAcknowledge = onAcknowledgeAlert,
                onReroute = onRerouteVehicle,
                onViewRoute = onNavigateToOptimizer,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

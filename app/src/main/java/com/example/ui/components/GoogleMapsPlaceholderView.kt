package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.*
import com.example.ui.theme.*

enum class GoogleMapType(val displayName: String) {
    TERRAIN("Terrain"),
    SATELLITE("Satellite"),
    ROADMAP("Roadmap")
}

data class MapMarkerInfo(
    val id: String,
    val title: String,
    val snippet: String,
    val type: String,
    val riskLevel: RiskLevel,
    val latitude: Double,
    val longitude: Double
)

/**
 * Google Maps Compose Placeholder Component
 * Prepares the Dashboard for native Google Maps display with vehicle locations,
 * weather telemetry markers, mountain route polylines, and standard Google Maps UI controls.
 */
@Composable
fun GoogleMapsPlaceholderView(
    vehicles: List<Vehicle>,
    alerts: List<AlertItem>,
    fieldReports: List<FieldReport>,
    filters: MapFilterState,
    isRoadBlocked: Boolean,
    selectedRouteId: String,
    weatherZones: List<WeatherZone> = MockDataProvider.weatherZones,
    deliveries: List<DeliveryItem> = MockDataProvider.initialDeliveries,
    activeDeliveryId: String? = null,
    onSelectDelivery: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    onMarkerClick: ((MapMarkerInfo) -> Unit)? = null
) {
    var mapType by remember { mutableStateOf(GoogleMapType.TERRAIN) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedMarker by remember { mutableStateOf<MapMarkerInfo?>(null) }
    var mapWidthPx by remember { mutableFloatStateOf(0f) }
    var mapHeightPx by remember { mutableFloatStateOf(0f) }

    var localActiveDeliveryId by remember { mutableStateOf(activeDeliveryId ?: deliveries.firstOrNull()?.id) }
    val effectiveActiveDeliveryId = activeDeliveryId ?: localActiveDeliveryId
    val activeDelivery = deliveries.find { it.id == effectiveActiveDeliveryId } ?: deliveries.firstOrNull()

    // Pulsing radar animation for vehicle markers
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarPulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 64f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                when (mapType) {
                    GoogleMapType.TERRAIN -> Color(0xFFE5ECE3)
                    GoogleMapType.SATELLITE -> Color(0xFF1E293B)
                    GoogleMapType.ROADMAP -> Color(0xFFF1F5F9)
                }
            )
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .onSizeChanged { size ->
                mapWidthPx = size.width.toFloat()
                mapHeightPx = size.height.toFloat()
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomLevel = (zoomLevel * zoom).coerceIn(0.8f, 2.5f)
                    panOffset = Offset(
                        x = (panOffset.x + pan.x).coerceIn(-300f, 300f),
                        y = (panOffset.y + pan.y).coerceIn(-200f, 200f)
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    selectedMarker = null
                }
            }
    ) {
        // Base Map Canvas (Roads, Mountain Topography & River Corridors)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cx = width / 2 + panOffset.x
            val cy = height / 2 + panOffset.y

            // Background terrain contour gradients for North Eastern mountain elevations
            if (mapType == GoogleMapType.TERRAIN) {
                // Mountain contour fills
                val hillPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(width * 0.9f, 0f)
                    lineTo(width * 0.7f, height * 0.45f)
                    lineTo(width * 0.2f, height * 0.4f)
                    close()
                }
                drawPath(hillPath, Color(0xFFD7E2D3))

                val mountainPeakPath = Path().apply {
                    moveTo(width * 0.3f, 0f)
                    lineTo(width * 0.65f, 0f)
                    lineTo(width * 0.5f, height * 0.3f)
                    close()
                }
                drawPath(mountainPeakPath, Color(0xFFCBDAC6))
            } else if (mapType == GoogleMapType.SATELLITE) {
                drawRect(Color(0xFF0F172A))
            }

            // Brahmaputra River Basin Corridor
            val riverColor = if (mapType == GoogleMapType.SATELLITE) Color(0xFF0284C7) else Color(0xFFA5D8F3)
            val riverPath = Path().apply {
                moveTo(0f, cy + 30f * zoomLevel)
                cubicTo(
                    width * 0.3f, cy + 10f * zoomLevel,
                    width * 0.6f, cy + 55f * zoomLevel,
                    width, cy + 20f * zoomLevel
                )
                lineTo(width, cy + 45f * zoomLevel)
                cubicTo(
                    width * 0.6f, cy + 75f * zoomLevel,
                    width * 0.3f, cy + 30f * zoomLevel,
                    0f, cy + 50f * zoomLevel
                )
                close()
            }
            drawPath(riverPath, riverColor)

            // Highway NH-27 (East-West Trunk)
            val nh27Path = Path().apply {
                moveTo(width * 0.05f, cy + 25f * zoomLevel)
                lineTo(width * 0.45f, cy + 35f * zoomLevel)
                lineTo(width * 0.85f, cy + 30f * zoomLevel)
            }
            drawPath(
                nh27Path,
                color = if (mapType == GoogleMapType.SATELLITE) Color(0xFFFBBF24) else Color(0xFFF59E0B),
                style = Stroke(width = 4f * zoomLevel, cap = StrokeCap.Round)
            )

            // Highway NH-13 (Guwahati to Tawang Corridor - Route A)
            val nh13Path = Path().apply {
                moveTo(width * 0.25f, cy + 35f * zoomLevel) // Guwahati Hub
                lineTo(width * 0.32f, cy - 20f * zoomLevel) // Tezpur
                lineTo(width * 0.36f, cy - 70f * zoomLevel) // Bhalukpong
                lineTo(width * 0.40f, cy - 120f * zoomLevel) // Bomdila Pass (Blockage site)
                lineTo(width * 0.42f, cy - 170f * zoomLevel) // Tawang
            }

            // Route A: Highlighted red if blocked, orange if high risk
            val routeAColor = if (isRoadBlocked) RiskCriticalRed else RiskHighOrange
            drawPath(
                nh13Path,
                color = routeAColor,
                style = Stroke(
                    width = if (selectedRouteId == "RT-A") 5f * zoomLevel else 3.5f * zoomLevel,
                    cap = StrokeCap.Round
                )
            )

            // Route B: Kalaktang-Rupa Safe Bypass (Green)
            val nh13BypassPath = Path().apply {
                moveTo(width * 0.32f, cy - 20f * zoomLevel) // Tezpur
                lineTo(width * 0.26f, cy - 80f * zoomLevel) // Kalaktang Bypass
                lineTo(width * 0.34f, cy - 140f * zoomLevel) // Rupa / Dirang
                lineTo(width * 0.42f, cy - 170f * zoomLevel) // Tawang
            }
            drawPath(
                nh13BypassPath,
                color = RiskLowGreen,
                style = Stroke(
                    width = if (selectedRouteId == "RT-B") 5f * zoomLevel else 3.5f * zoomLevel,
                    cap = StrokeCap.Round
                )
            )

            // Road Block Marker on NH-13 Km 142
            if (isRoadBlocked && filters.showRoadBlocks) {
                val blockX = width * 0.40f
                val blockY = cy - 120f * zoomLevel
                drawCircle(
                    color = RiskCriticalRed.copy(alpha = 0.3f),
                    radius = 18f * zoomLevel,
                    center = Offset(blockX, blockY)
                )
                drawCircle(
                    color = RiskCriticalRed,
                    radius = 9f * zoomLevel,
                    center = Offset(blockX, blockY)
                )
            }

            // Active Delivery Polyline connecting Start Point -> Waypoints -> Destination Point
            // The polyline changes color dynamically based on the associated route risk score:
            // Score <= 35: Green (Low Risk)
            // Score 36..65: Yellow (Medium Risk)
            // Score > 65: Red (High/Critical Risk)
            if (activeDelivery != null) {
                val geoBounds = MapGeoBounds.NER_DEFAULT
                val deliveryPoints = projectDeliveryRoutePoints(
                    delivery = activeDelivery,
                    bounds = geoBounds,
                    mapWidthPx = width,
                    mapHeightPx = height,
                    zoomLevel = zoomLevel,
                    panOffset = panOffset
                )
                drawDeliveryPolyline(
                    projectedPoints = deliveryPoints,
                    riskScore = activeDelivery.riskScore,
                    zoomLevel = zoomLevel,
                    dashPhase = dashPhase
                )
            }
        }

        // Interactive Vehicle Markers Overlay
        if (filters.showVehicles) {
            vehicles.forEachIndexed { index, vehicle ->
                // Map GPS coordinates to viewport screen space
                val relX = (0.28f + (index * 0.12f)).coerceIn(0.12f, 0.88f)
                val relY = (0.35f + ((index % 3) * 0.18f)).coerceIn(0.15f, 0.85f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = (relX * 300).dp,
                            top = (relY * 260).dp
                        )
                ) {
                    VehicleMarkerPin(
                        vehicle = vehicle,
                        radarPulse = radarPulse,
                        pulseAlpha = pulseAlpha,
                        onClick = {
                            selectedMarker = MapMarkerInfo(
                                id = vehicle.id,
                                title = "${vehicle.id} (${vehicle.driverName})",
                                snippet = "${vehicle.currentLocation} • ${vehicle.speedKmh} km/h • ETA: ${vehicle.eta}",
                                type = "Vehicle",
                                riskLevel = vehicle.routeRisk,
                                latitude = vehicle.currentCoordinate.latitude,
                                longitude = vehicle.currentCoordinate.longitude
                            )
                            onMarkerClick?.invoke(selectedMarker!!)
                        }
                    )
                }
            }
        }

        // Dynamic Weather Warning Markers Overlay based on coordinate data & severity level (Green, Yellow, Red)
        if (filters.showWeather && mapWidthPx > 0f && mapHeightPx > 0f) {
            weatherZones.forEach { zone ->
                WeatherMarker(
                    weatherZone = zone,
                    mapWidthPx = mapWidthPx,
                    mapHeightPx = mapHeightPx,
                    zoomLevel = zoomLevel,
                    panOffset = panOffset,
                    onClick = {
                        selectedMarker = MapMarkerInfo(
                            id = zone.id,
                            title = zone.regionName,
                            snippet = "Rainfall: ${zone.rainfallMmPerHour} mm/h • Severity: ${zone.severity.label} • Temp: ${zone.temperatureCelsius}°C • Wind: ${zone.windKmh} km/h • Road: ${zone.roadCondition}",
                            type = "Weather (${zone.severity.label})",
                            riskLevel = zone.riskLevel,
                            latitude = zone.coordinates.latitude,
                            longitude = zone.coordinates.longitude
                        )
                        onMarkerClick?.invoke(selectedMarker!!)
                    }
                )
            }
        }

        // Dynamic Start Point & Destination Point Markers for Active Delivery
        if (mapWidthPx > 0f && mapHeightPx > 0f && activeDelivery != null) {
            val geoBounds = MapGeoBounds.NER_DEFAULT
            val density = LocalDensity.current.density

            val startPx = geoBounds.project(
                latitude = activeDelivery.startCoordinate.latitude,
                longitude = activeDelivery.startCoordinate.longitude,
                mapWidthPx = mapWidthPx,
                mapHeightPx = mapHeightPx,
                zoomLevel = zoomLevel,
                panOffset = panOffset
            )
            val destPx = geoBounds.project(
                latitude = activeDelivery.destinationCoordinate.latitude,
                longitude = activeDelivery.destinationCoordinate.longitude,
                mapWidthPx = mapWidthPx,
                mapHeightPx = mapHeightPx,
                zoomLevel = zoomLevel,
                panOffset = panOffset
            )

            // Start Point Marker Pin
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = (startPx.x.coerceIn(10f, (mapWidthPx - 130f).coerceAtLeast(10f)) / density).dp,
                        top = (startPx.y.coerceIn(40f, (mapHeightPx - 50f).coerceAtLeast(40f)) / density).dp
                    )
            ) {
                DeliveryStartMarker(
                    locationName = activeDelivery.origin,
                    modifier = Modifier.clickable {
                        selectedMarker = MapMarkerInfo(
                            id = "START-${activeDelivery.id}",
                            title = "Origin: ${activeDelivery.origin}",
                            snippet = "Start point for delivery ${activeDelivery.id} (${activeDelivery.goodsType}). Priority: ${activeDelivery.priority.name}",
                            type = "Delivery Start",
                            riskLevel = RiskLevel.LOW,
                            latitude = activeDelivery.startCoordinate.latitude,
                            longitude = activeDelivery.startCoordinate.longitude
                        )
                        onMarkerClick?.invoke(selectedMarker!!)
                    }
                )
            }

            // Destination Point Marker Pin
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = (destPx.x.coerceIn(10f, (mapWidthPx - 130f).coerceAtLeast(10f)) / density).dp,
                        top = (destPx.y.coerceIn(40f, (mapHeightPx - 50f).coerceAtLeast(40f)) / density).dp
                    )
            ) {
                DeliveryDestinationMarker(
                    locationName = activeDelivery.destination,
                    riskScore = activeDelivery.riskScore,
                    modifier = Modifier.clickable {
                        selectedMarker = MapMarkerInfo(
                            id = "DEST-${activeDelivery.id}",
                            title = "Destination: ${activeDelivery.destination}",
                            snippet = "Destination point for delivery ${activeDelivery.id}. Route Risk Score: ${activeDelivery.riskScore}/100 • ETA: ${activeDelivery.eta}",
                            type = "Delivery Destination",
                            riskLevel = activeDelivery.riskLevel,
                            latitude = activeDelivery.destinationCoordinate.latitude,
                            longitude = activeDelivery.destinationCoordinate.longitude
                        )
                        onMarkerClick?.invoke(selectedMarker!!)
                    }
                )
            }
        }

        // Top Controls: Google Map Type Selector (Terrain, Satellite, Roadmap)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.92f),
                border = BorderStroke(0.8.dp, SlateBorder),
                shadowElevation = 3.dp
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    GoogleMapType.values().forEach { type ->
                        val isSelected = mapType == type
                        Surface(
                            onClick = { mapType = type },
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) BlueAccent else Color.Transparent,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = type.displayName,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else NavyDark,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Google Maps Branding & Grounding Badge (Top Right)
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White.copy(alpha = 0.92f),
            border = BorderStroke(0.8.dp, SlateBorder),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = RiskCriticalRed,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Google Maps Compose",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }
        }

        // Zoom In / Out & Recenter Control Buttons (Bottom Right)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(0.8.dp, SlateBorder),
                shadowElevation = 2.dp
            ) {
                Column {
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(2.5f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = NavyDark, modifier = Modifier.size(18.dp))
                    }
                    HorizontalDivider(color = SlateBorder, thickness = 0.8.dp)
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel * 0.8f).coerceAtLeast(0.8f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = NavyDark, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Recenter Fleet button
            Surface(
                onClick = {
                    zoomLevel = 1.0f
                    panOffset = Offset.Zero
                },
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(0.8.dp, SlateBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = BlueAccent, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Active Delivery Route HUD Overlay (displays start, destination, and dynamic route risk score color)
        if (activeDelivery != null) {
            ActiveDeliveryRouteHUD(
                deliveries = deliveries,
                activeDelivery = activeDelivery,
                onSelectDelivery = { selectedId ->
                    localActiveDeliveryId = selectedId
                    onSelectDelivery?.invoke(selectedId)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 34.dp)
            )
        }

        // Standard Google Maps Legal / Scale Label (Bottom Left)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White.copy(alpha = 0.88f)
            ) {
                Text(
                    text = "Google • 26.85°N, 92.65°E • NER Corridor",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Marker Details Info Window (Callout popover on marker tap)
        selectedMarker?.let { marker ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                border = BorderStroke(1.dp, CyanHighlight),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (marker.type == "Vehicle") Icons.Default.DirectionsCar else Icons.Default.Cloud,
                                contentDescription = null,
                                tint = if (marker.type == "Vehicle") CyanHighlight else RiskHighOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = marker.type.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanHighlight
                            )
                        }

                        IconButton(
                            onClick = { selectedMarker = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = marker.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = marker.snippet,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lat: %.4f, Lng: %.4f".format(marker.latitude, marker.longitude),
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                        AlertBadge(level = marker.riskLevel)
                    }
                }
            }
        }
    }
}

/**
 * Google Maps Vehicle Location Pin with Radar Pulse
 */
@Composable
private fun VehicleMarkerPin(
    vehicle: Vehicle,
    radarPulse: Float,
    pulseAlpha: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulse ring
        Box(
            modifier = Modifier
                .size((radarPulse * 2).dp)
                .clip(CircleShape)
                .background(CyanHighlight.copy(alpha = pulseAlpha))
        )

        // Marker Pin Core
        Surface(
            shape = CircleShape,
            color = when (vehicle.routeRisk) {
                RiskLevel.LOW -> RiskLowGreen
                RiskLevel.MEDIUM -> RiskMediumYellow
                RiskLevel.HIGH -> RiskHighOrange
                RiskLevel.CRITICAL -> RiskCriticalRed
            },
            border = BorderStroke(1.5.dp, Color.White),
            shadowElevation = 4.dp,
            modifier = Modifier.size(26.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = vehicle.id,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * Google Maps Weather Hazard Marker Pin
 */
@Composable
private fun WeatherWarningMarkerPin(
    title: String,
    rate: String,
    riskLevel: RiskLevel,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = when (riskLevel) {
            RiskLevel.CRITICAL -> RiskCriticalRed
            RiskLevel.HIGH -> RiskHighOrange
            else -> BlueAccent
        },
        border = BorderStroke(1.dp, Color.White),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = rate,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

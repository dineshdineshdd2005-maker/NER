package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

data class GisNode(
    val id: String,
    val name: String,
    val state: String,
    val normX: Float, // 0.0 to 1.0 within map canvas
    val normY: Float,
    val isHub: Boolean = false
)

val nerGisNodes = listOf(
    GisNode("GHY", "Guwahati", "Assam", 0.28f, 0.65f, true),
    GisNode("TEZ", "Tezpur", "Assam", 0.44f, 0.54f),
    GisNode("BOM", "Bomdila", "Arunachal", 0.36f, 0.38f),
    GisNode("DIR", "Dirang", "Arunachal", 0.34f, 0.32f),
    GisNode("SELA", "Sela Pass (13.7k ft)", "Arunachal", 0.32f, 0.24f),
    GisNode("TAW", "Tawang", "Arunachal", 0.26f, 0.16f, true),
    GisNode("KAL", "Kalaktang (Bypass)", "Arunachal", 0.32f, 0.46f),
    GisNode("ITA", "Itanagar", "Arunachal", 0.58f, 0.40f, true),
    GisNode("JOR", "Jorhat", "Assam", 0.64f, 0.52f),
    GisNode("DIB", "Dibrugarh", "Assam", 0.78f, 0.42f, true),
    GisNode("DHM", "Dhemaji", "Assam", 0.72f, 0.36f),
    GisNode("SHL", "Shillong", "Meghalaya", 0.32f, 0.76f, true),
    GisNode("SIL", "Silchar", "Assam", 0.48f, 0.88f),
    GisNode("KOH", "Kohima", "Nagaland", 0.70f, 0.66f, true),
    GisNode("IMP", "Imphal", "Manipur", 0.72f, 0.82f, true),
    GisNode("AIZ", "Aizawl", "Mizoram", 0.52f, 0.94f, true),
    GisNode("AGT", "Agartala", "Tripura", 0.22f, 0.86f, true)
)

@Composable
fun InteractiveGisMapView(
    vehicles: List<Vehicle>,
    alerts: List<AlertItem>,
    fieldReports: List<FieldReport>,
    filters: MapFilterState,
    isRoadBlocked: Boolean,
    selectedRouteId: String = "RT-B",
    onVehicleSelected: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedNode by remember { mutableStateOf<GisNode?>(null) }
    var selectedIncidentInfo by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
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

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.8f, 3.5f)
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-400f * scale, 400f * scale),
                            y = (offset.y + pan.y).coerceIn(-400f * scale, 400f * scale)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // Hit-test nodes
                        selectedIncidentInfo = null
                        selectedNode = nerGisNodes.find { node ->
                            val nx = (tapOffset.x - offset.x) / scale
                            val ny = (tapOffset.y - offset.y) / scale
                            val nodePx = node.normX * size.width
                            val nodePy = node.normY * size.height
                            val dist = kotlin.math.hypot(nx - nodePx, ny - nodePy)
                            dist < 28f
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Transform canvas with pan & zoom
            drawContext.canvas.save()
            drawContext.canvas.translate(offset.x, offset.y)
            drawContext.canvas.scale(scale, scale)

            // 1. Draw Regional Background Terrain & River Corridor
            drawNerTerrainBackground(w, h)

            // 2. Draw Risk Polygons (if enabled)
            if (filters.showRiskZones) {
                drawRiskZones(w, h)
            }

            // 3. Draw Road Corridors & Highways
            drawHighwayNetworks(w, h)

            // 4. Draw Route Lines (Route A, B, C)
            drawRoutePaths(w, h, selectedRouteId, isRoadBlocked)

            // 5. Draw City/Hub Nodes
            drawGisNodes(w, h, textMeasurer)

            // 6. Draw Weather Overlays (if enabled)
            if (filters.showWeather) {
                drawWeatherMarkers(w, h, textMeasurer)
            }

            // 7. Draw Landslide & Flood Incident Markers
            if (filters.showLandslides || filters.showFloods || filters.showRoadBlocks) {
                drawIncidentMarkers(w, h, filters, isRoadBlocked, textMeasurer)
            }

            // 8. Draw Field Reports (if enabled)
            if (filters.showFieldReports) {
                drawFieldReportPins(w, h, fieldReports)
            }

            // 9. Draw Active Vehicles (if enabled)
            if (filters.showVehicles) {
                drawVehicles(w, h, vehicles, pulseRadius, pulseAlpha, textMeasurer)
            }

            drawContext.canvas.restore()
        }

        // Map Control Buttons (Zoom In, Zoom Out, Reset Center)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { scale = (scale * 1.3f).coerceAtMost(3.5f) },
                containerColor = NavyMedium,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
            }
            SmallFloatingActionButton(
                onClick = { scale = (scale / 1.3f).coerceAtLeast(0.8f) },
                containerColor = NavyMedium,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
            }
            SmallFloatingActionButton(
                onClick = {
                    scale = 1f
                    offset = Offset.Zero
                    selectedNode = null
                },
                containerColor = NavyDark,
                contentColor = CyanHighlight
            ) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = "Center Map", modifier = Modifier.size(18.dp))
            }
        }

        // Selected Node / Waypoint Callout Info Card
        selectedNode?.let { node ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
                    .widthIn(max = 320.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, CyanHighlight),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = CyanHighlight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = node.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "State: ${node.state} • Sector GIS: ${node.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    IconButton(
                        onClick = { selectedNode = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }
            }
        }

        // Map Legend Indicator Pill (Bottom Left)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xDD0B192C),
            border = BorderStroke(1.dp, Color(0xFF1E3E62))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(Color(0xFF10B981), "Safe")
                LegendItem(Color(0xFFF59E0B), "Moderate")
                LegendItem(Color(0xFFEF4444), "Critical Block")
                LegendItem(Color(0xFF38BDF8), "Vehicles")
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(text = label, color = Color(0xFFCBD5E1), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

// ---------------- Canvas Drawing Subroutines ----------------

private fun DrawScope.drawNerTerrainBackground(w: Float, h: Float) {
    // Subtle background mesh
    drawRect(
        color = Color(0xFF0A1120),
        size = Size(w, h)
    )

    // Arunachal & Bhutan Mountain Contour (North)
    val mountainPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(w, 0f)
        lineTo(w, h * 0.42f)
        cubicTo(w * 0.7f, h * 0.46f, w * 0.4f, h * 0.35f, 0f, h * 0.48f)
        close()
    }
    drawPath(
        path = mountainPath,
        color = Color(0xFF102138)
    )

    // Brahmaputra River Ribbon (West to East curving through Assam)
    val riverPath = Path().apply {
        moveTo(w * 0.12f, h * 0.67f)
        cubicTo(
            w * 0.35f, h * 0.62f,
            w * 0.55f, h * 0.58f,
            w * 0.72f, h * 0.48f
        )
        cubicTo(
            w * 0.82f, h * 0.44f,
            w * 0.92f, h * 0.35f,
            w * 0.98f, h * 0.32f
        )
    }
    drawPath(
        path = riverPath,
        color = Color(0xFF0284C7).copy(alpha = 0.55f),
        style = Stroke(width = 9f, cap = StrokeCap.Round)
    )

    // Meghalaya Plateau Contour (South West)
    val meghalayaPath = Path().apply {
        moveTo(w * 0.20f, h * 0.72f)
        quadraticTo(w * 0.38f, h * 0.70f, w * 0.45f, h * 0.80f)
        lineTo(w * 0.42f, h * 0.90f)
        lineTo(w * 0.18f, h * 0.88f)
        close()
    }
    drawPath(
        path = meghalayaPath,
        color = Color(0xFF152A42)
    )
}

private fun DrawScope.drawRiskZones(w: Float, h: Float) {
    // Critical Risk Polygon in West Kameng Sector
    val criticalZone = Path().apply {
        moveTo(w * 0.28f, h * 0.22f)
        lineTo(w * 0.42f, h * 0.28f)
        lineTo(w * 0.44f, h * 0.44f)
        lineTo(w * 0.28f, h * 0.48f)
        close()
    }
    drawPath(
        path = criticalZone,
        color = Color(0xFFEF4444).copy(alpha = 0.22f)
    )
    drawPath(
        path = criticalZone,
        color = Color(0xFFEF4444).copy(alpha = 0.65f),
        style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
    )

    // Moderate Flood Zone near Dhemaji / Subansiri
    val floodZone = Path().apply {
        moveTo(w * 0.66f, h * 0.32f)
        lineTo(w * 0.78f, h * 0.32f)
        lineTo(w * 0.82f, h * 0.44f)
        lineTo(w * 0.68f, h * 0.42f)
        close()
    }
    drawPath(
        path = floodZone,
        color = Color(0xFFF59E0B).copy(alpha = 0.18f)
    )
}

private fun DrawScope.drawHighwayNetworks(w: Float, h: Float) {
    // NH-27 Guwahati -> Jorhat -> Dibrugarh
    val nh27 = Path().apply {
        moveTo(w * 0.28f, h * 0.65f) // GHY
        lineTo(w * 0.44f, h * 0.54f) // TEZ
        lineTo(w * 0.64f, h * 0.52f) // JOR
        lineTo(w * 0.78f, h * 0.42f) // DIB
    }
    drawPath(nh27, Color(0xFF334155), style = Stroke(width = 4f))

    // NH-29 Jorhat -> Kohima -> Imphal
    val nh29 = Path().apply {
        moveTo(w * 0.64f, h * 0.52f)
        lineTo(w * 0.70f, h * 0.66f)
        lineTo(w * 0.72f, h * 0.82f)
    }
    drawPath(nh29, Color(0xFF334155), style = Stroke(width = 3f))

    // Southern link: Guwahati -> Shillong -> Silchar -> Aizawl
    val southHighway = Path().apply {
        moveTo(w * 0.28f, h * 0.65f)
        lineTo(w * 0.32f, h * 0.76f)
        lineTo(w * 0.48f, h * 0.88f)
        lineTo(w * 0.52f, h * 0.94f)
    }
    drawPath(southHighway, Color(0xFF334155), style = Stroke(width = 3f))
}

private fun DrawScope.drawRoutePaths(w: Float, h: Float, selectedRouteId: String, isRoadBlocked: Boolean) {
    val ghy = Offset(w * 0.28f, h * 0.65f)
    val tez = Offset(w * 0.44f, h * 0.54f)
    val bom = Offset(w * 0.36f, h * 0.38f)
    val dir = Offset(w * 0.34f, h * 0.32f)
    val sela = Offset(w * 0.32f, h * 0.24f)
    val taw = Offset(w * 0.26f, h * 0.16f)
    val kal = Offset(w * 0.32f, h * 0.46f)

    // Route A: Guwahati -> Tezpur -> Bomdila -> Dirang -> Sela -> Tawang (DIRECT)
    val routeAPath = Path().apply {
        moveTo(ghy.x, ghy.y)
        lineTo(tez.x, tez.y)
        lineTo(bom.x, bom.y)
        lineTo(dir.x, dir.y)
        lineTo(sela.x, sela.y)
        lineTo(taw.x, taw.y)
    }

    val routeAColor = if (isRoadBlocked) Color(0xFFEF4444) else Color(0xFFF97316)
    val routeAWidth = if (selectedRouteId == "RT-A") 5f else 2.5f
    drawPath(
        path = routeAPath,
        color = routeAColor.copy(alpha = if (selectedRouteId == "RT-A") 0.9f else 0.4f),
        style = Stroke(
            width = routeAWidth,
            pathEffect = if (isRoadBlocked) PathEffect.dashPathEffect(floatArrayOf(12f, 8f)) else null
        )
    )

    // Route B: Guwahati -> Kalaktang Bypass -> Dirang -> Sela -> Tawang (AI RECOMMENDED SAFE ROUTE)
    val routeBPath = Path().apply {
        moveTo(ghy.x, ghy.y)
        lineTo(kal.x, kal.y)
        lineTo(dir.x, dir.y)
        lineTo(sela.x, sela.y)
        lineTo(taw.x, taw.y)
    }
    val routeBWidth = if (selectedRouteId == "RT-B") 6f else 3f
    drawPath(
        path = routeBPath,
        color = Color(0xFF10B981).copy(alpha = if (selectedRouteId == "RT-B") 1.0f else 0.5f),
        style = Stroke(width = routeBWidth)
    )

    // Highlight active recommended route with a neon glow stroke
    if (selectedRouteId == "RT-B") {
        drawPath(
            path = routeBPath,
            color = Color(0xFF10B981).copy(alpha = 0.25f),
            style = Stroke(width = 14f)
        )
    }
}

private fun DrawScope.drawGisNodes(w: Float, h: Float, textMeasurer: TextMeasurer) {
    nerGisNodes.forEach { node ->
        val x = node.normX * w
        val y = node.normY * h

        // Outer glow for major hubs
        if (node.isHub) {
            drawCircle(
                color = CyanHighlight.copy(alpha = 0.35f),
                radius = 9f,
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 4.5f,
                center = Offset(x, y)
            )
        } else {
            drawCircle(
                color = Color(0xFF94A3B8),
                radius = 3.5f,
                center = Offset(x, y)
            )
        }

        // Label
        val textLayoutResult = textMeasurer.measure(
            text = node.name,
            style = TextStyle(
                color = if (node.isHub) Color.White else Color(0xFF94A3B8),
                fontSize = if (node.isHub) 10.sp else 8.5.sp,
                fontWeight = if (node.isHub) FontWeight.Bold else FontWeight.Normal
            )
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(x + 7f, y - 12f)
        )
    }
}

private fun DrawScope.drawWeatherMarkers(w: Float, h: Float, textMeasurer: TextMeasurer) {
    // Storm Cloud marker over West Kameng
    val stormX = w * 0.36f
    val stormY = h * 0.30f
    drawCircle(
        color = Color(0xFF38BDF8).copy(alpha = 0.3f),
        radius = 16f,
        center = Offset(stormX, stormY)
    )
    val stormText = textMeasurer.measure(
        text = "🌧 65 mm/h Rain",
        style = TextStyle(color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
    )
    drawText(stormText, topLeft = Offset(stormX - 28f, stormY + 8f))

    // Flood rain marker near Dhemaji
    val floodX = w * 0.74f
    val floodY = h * 0.38f
    val floodText = textMeasurer.measure(
        text = "🌊 Flood Level Alert",
        style = TextStyle(color = Color(0xFFF59E0B), fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
    )
    drawText(floodText, topLeft = Offset(floodX - 35f, floodY - 18f))
}

private fun DrawScope.drawIncidentMarkers(
    w: Float,
    h: Float,
    filters: MapFilterState,
    isRoadBlocked: Boolean,
    textMeasurer: TextMeasurer
) {
    // Road Blockage on Bomdila Pass Km 142
    if (filters.showRoadBlocks && isRoadBlocked) {
        val blockX = w * 0.36f
        val blockY = h * 0.38f

        // Red flashing circle
        drawCircle(
            color = Color(0xFFEF4444).copy(alpha = 0.4f),
            radius = 14f,
            center = Offset(blockX, blockY)
        )
        drawCircle(
            color = Color(0xFFEF4444),
            radius = 7f,
            center = Offset(blockX, blockY)
        )
        // Red 'X' icon
        drawLine(
            color = Color.White,
            start = Offset(blockX - 4f, blockY - 4f),
            end = Offset(blockX + 4f, blockY + 4f),
            strokeWidth = 2.5f
        )
        drawLine(
            color = Color.White,
            start = Offset(blockX - 4f, blockY + 4f),
            end = Offset(blockX + 4f, blockY - 4f),
            strokeWidth = 2.5f
        )

        val blockText = textMeasurer.measure(
            text = "⛔ ROAD BLOCKED: Landslide Km 142",
            style = TextStyle(color = Color(0xFFF87171), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
        )
        drawText(blockText, topLeft = Offset(blockX + 12f, blockY - 6f))
    }
}

private fun DrawScope.drawFieldReportPins(
    w: Float,
    h: Float,
    reports: List<FieldReport>
) {
    reports.forEach { report ->
        // Convert lat/long approximately to map normalized bounds
        // Lat: 25.0 to 28.5 (h from 1.0 to 0.0), Long: 90.0 to 96.0 (w from 0.0 to 1.0)
        val normX = ((report.coordinates.longitude - 90.0) / 6.0).toFloat().coerceIn(0.1f, 0.9f)
        val normY = (1f - ((report.coordinates.latitude - 25.0) / 3.5).toFloat()).coerceIn(0.1f, 0.9f)

        val pinColor = when (report.severity) {
            RiskLevel.CRITICAL -> Color(0xFFEF4444)
            RiskLevel.HIGH -> Color(0xFFF97316)
            RiskLevel.MEDIUM -> Color(0xFFF59E0B)
            RiskLevel.LOW -> Color(0xFF10B981)
        }

        drawCircle(
            color = pinColor,
            radius = 5f,
            center = Offset(normX * w, normY * h)
        )
        drawCircle(
            color = Color.White,
            radius = 2f,
            center = Offset(normX * w, normY * h)
        )
    }
}

private fun DrawScope.drawVehicles(
    w: Float,
    h: Float,
    vehicles: List<Vehicle>,
    pulseRadius: Float,
    pulseAlpha: Float,
    textMeasurer: TextMeasurer
) {
    vehicles.forEach { vehicle ->
        // Normalized coordinate conversion
        val normX = ((vehicle.currentCoordinate.longitude - 90.0) / 6.0).toFloat().coerceIn(0.15f, 0.88f)
        val normY = (1f - ((vehicle.currentCoordinate.latitude - 25.0) / 3.5).toFloat()).coerceIn(0.12f, 0.92f)
        val cx = normX * w
        val cy = normY * h

        val vehicleColor = when (vehicle.routeRisk) {
            RiskLevel.CRITICAL -> Color(0xFFEF4444)
            RiskLevel.HIGH -> Color(0xFFF97316)
            RiskLevel.MEDIUM -> Color(0xFFF59E0B)
            RiskLevel.LOW -> Color(0xFF38BDF8)
        }

        // Dynamic GPS Pulse
        drawCircle(
            color = vehicleColor.copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = Offset(cx, cy)
        )

        // Vehicle Center Badge
        drawCircle(
            color = Color.White,
            radius = 6.5f,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = vehicleColor,
            radius = 5f,
            center = Offset(cx, cy)
        )

        // Vehicle Callout Tag
        val tagText = textMeasurer.measure(
            text = "${vehicle.id} (${vehicle.speedKmh} km/h)",
            style = TextStyle(
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                background = Color(0xCC0B192C)
            )
        )
        drawText(
            textLayoutResult = tagText,
            topLeft = Offset(cx + 9f, cy - 14f)
        )
    }
}

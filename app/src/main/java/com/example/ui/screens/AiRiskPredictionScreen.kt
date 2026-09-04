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
import com.example.model.RiskLevel
import com.example.model.RiskPredictionResult
import com.example.ui.components.AiExplanationCard
import com.example.ui.components.AlertBadge
import com.example.ui.components.RiskMeter
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRiskPredictionScreen(
    currentSource: String,
    currentDestination: String,
    currentVehicleType: String,
    currentWeather: String,
    predictionResult: RiskPredictionResult,
    onCalculateRisk: (source: String, destination: String, vehicleType: String, weather: String) -> Unit,
    onNavigateToOptimizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var source by remember { mutableStateOf(currentSource) }
    var destination by remember { mutableStateOf(currentDestination) }
    var vehicleType by remember { mutableStateOf(currentVehicleType) }
    var weather by remember { mutableStateOf(currentWeather) }
    var selectedDateTime by remember { mutableStateOf("Today, 14:00 hrs (Monsoon Window)") }

    val sourceOptions = listOf("Guwahati Central Depot", "Siliguri Transit Hub", "Tezpur Logistics Base", "Jorhat Depot")
    val destOptions = listOf("Tawang Military Hospital", "Itanagar Secretariat", "Kohima Central Depot", "Aizawl Emergency Hub")
    val vehicleOptions = listOf("All-Terrain Freight Truck (16T)", "4x4 Emergency Supply Van", "Cold Chain Vaccine Carrier", "Heavy Multi-Axle Carrier (24T)")
    val weatherOptions = listOf("Heavy Monsoonal Rain (65 mm/h)", "Flash Flood Alert / Inundation", "Dense Alpine Fog (<20m visibility)", "Clear / Intermittent Sun")

    var sourceExpanded by remember { mutableStateOf(false) }
    var destExpanded by remember { mutableStateOf(false) }
    var vehicleExpanded by remember { mutableStateOf(false) }
    var weatherExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Title & Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PurpleAi,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "AI ROUTE RISK PREDICTION",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                }
                Text(
                    text = "Predictive machine learning risk scoring based on satellite meteorology, GIS terrain contour & historical mass wasting data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Input Parameters Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ROUTE PARAMETERS & ENVIRONMENTAL CONDITIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Source Dropdown
                    ExposedDropdownMenuBox(
                        expanded = sourceExpanded,
                        onExpandedChange = { sourceExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = source,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Starting Location (Origin)") },
                            leadingIcon = { Icon(Icons.Default.TripOrigin, contentDescription = null, tint = CyanHighlight) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = sourceExpanded,
                            onDismissRequest = { sourceExpanded = false }
                        ) {
                            sourceOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        source = opt
                                        sourceExpanded = false
                                        onCalculateRisk(source, destination, vehicleType, weather)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Destination Dropdown
                    ExposedDropdownMenuBox(
                        expanded = destExpanded,
                        onExpandedChange = { destExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = destination,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Destination (Remote Terminal)") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = RiskCriticalRed) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = destExpanded,
                            onDismissRequest = { destExpanded = false }
                        ) {
                            destOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        destination = opt
                                        destExpanded = false
                                        onCalculateRisk(source, destination, vehicleType, weather)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Vehicle Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = vehicleExpanded,
                        onExpandedChange = { vehicleExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = vehicleType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Vehicle Type") },
                            leadingIcon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = BlueAccent) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = vehicleExpanded,
                            onDismissRequest = { vehicleExpanded = false }
                        ) {
                            vehicleOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        vehicleType = opt
                                        vehicleExpanded = false
                                        onCalculateRisk(source, destination, vehicleType, weather)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Weather Condition Dropdown
                    ExposedDropdownMenuBox(
                        expanded = weatherExpanded,
                        onExpandedChange = { weatherExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = weather,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Active Weather Condition") },
                            leadingIcon = { Icon(Icons.Default.CloudQueue, contentDescription = null, tint = RiskHighOrange) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = weatherExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = weatherExpanded,
                            onDismissRequest = { weatherExpanded = false }
                        ) {
                            weatherOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        weather = opt
                                        weatherExpanded = false
                                        onCalculateRisk(source, destination, vehicleType, weather)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Run Risk Evaluation Button
                    Button(
                        onClick = { onCalculateRisk(source, destination, vehicleType, weather) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyMedium),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("RECALCULATE AI RISK SCORE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Prediction Result Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RiskMeter(
                        score = predictionResult.overallScore,
                        level = predictionResult.riskLevel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(Modifier.height(14.dp))

                    // Factor Summary Grid (Rainfall, Landslide, Road Condition, Flood, Connectivity)
                    Text(
                        text = "INDIVIDUAL ROUTE RISK FACTORS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FactorPill("Rainfall Risk", RiskLevel.HIGH)
                        FactorPill("Landslide Risk", RiskLevel.HIGH)
                        FactorPill("Road Condition", RiskLevel.MEDIUM)
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FactorPill("Flood Risk", RiskLevel.MEDIUM)
                        FactorPill("Connectivity", RiskLevel.LOW)
                        FactorPill("Slope Stability", RiskLevel.CRITICAL)
                    }
                }
            }
        }

        // Detailed AI Explainability Card (Mandatory as per user prompt section 14)
        item {
            AiExplanationCard(
                factors = predictionResult.factors,
                explanation = predictionResult.aiExplanation,
                recommendation = predictionResult.recommendation,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Route Optimization Action Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Disruption Predicted on Primary Corridor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "AI has identified 1 safe alternate corridor with 70% lower mass-wasting probability.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onNavigateToOptimizer,
                        colors = ButtonDefaults.buttonColors(containerColor = RiskLowGreen)
                    ) {
                        Text("VIEW SAFE ROUTE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FactorPill(name: String, level: RiskLevel) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(0.8.dp, SlateBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$name: ",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            AlertBadge(level = level)
        }
    }
}

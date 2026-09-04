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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FieldReport
import com.example.model.ReportType
import com.example.model.RiskLevel
import com.example.ui.components.AlertBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldReportingScreen(
    fieldReports: List<FieldReport>,
    isOffline: Boolean,
    pendingOfflineReports: List<FieldReport>,
    isSyncing: Boolean,
    syncSuccessMessage: String?,
    onToggleOffline: () -> Unit,
    onSubmitReport: (
        type: ReportType,
        locationName: String,
        latitude: Double,
        longitude: Double,
        description: String,
        severity: RiskLevel,
        hasPhoto: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(ReportType.LANDSLIDE) }
    var locationName by remember { mutableStateOf("NH-13 Bomdila Sector Km 144") }
    var latitude by remember { mutableStateOf("27.264") }
    var longitude by remember { mutableStateOf("92.421") }
    var description by remember { mutableStateOf("Fresh mudflow and tree collapse obstructing one lane. PWD clearing team on site.") }
    var selectedSeverity by remember { mutableStateOf(RiskLevel.HIGH) }
    var hasPhotoAttached by remember { mutableStateOf(true) }

    var typeDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Offline Toggle Control
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = CyanHighlight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "FIELD REPORTING MODULE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }
                    Text(
                        text = "Mobile-friendly rapid incident logger for ground teams & PWD engineers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Offline Mode Simulation Switch
                Button(
                    onClick = onToggleOffline,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOffline) RiskCriticalRed else NavyMedium
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isOffline) "SIMULATE ONLINE" else "SIMULATE OFFLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Offline / Sync Status Indicator Card (Section 13 requirement)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOffline) Color(0xFFFEF2F2) else if (isSyncing) Color(0xFFEFF6FF) else Color(0xFFF0FDF4)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isOffline) Color(0xFFF87171) else if (isSyncing) Color(0xFF93C5FD) else Color(0xFF86EFAC)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.5.dp,
                                color = BlueAccent
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isOffline) RiskCriticalRed else RiskLowGreen)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = if (isOffline)
                                "Offline Mode Active (Remote NER Valley Simulation)"
                            else if (isSyncing)
                                "Syncing ${pendingOfflineReports.size} pending reports with Central GIS..."
                            else
                                "Network Connected — Cloud Sync Operational",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isOffline) RiskCriticalRed else if (isSyncing) BlueAccent else Color(0xFF15803D)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = if (isOffline)
                            "\"Offline — Report saved locally in SQLite queue. Will automatically batch-sync when mobile signal or satellite link restores.\""
                        else if (isSyncing)
                            "Uploading photos, timestamps, and GPS coordinates to Central Server..."
                        else
                            syncSuccessMessage ?: "\"All reports synchronized successfully.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // New Incident Report Form
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SUBMIT NEW DISRUPTION / INCIDENT REPORT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Report Type Selector
                    ExposedDropdownMenuBox(
                        expanded = typeDropdownExpanded,
                        onExpandedChange = { typeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedType.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Incident / Hazard Type") },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = RiskHighOrange) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            ReportType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.label) },
                                    onClick = {
                                        selectedType = type
                                        typeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Location Name Field
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Location Description / Landmark") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = CyanHighlight) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // GPS Coordinates Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = latitude,
                            onValueChange = { latitude = it },
                            label = { Text("Latitude") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = longitude,
                            onValueChange = { longitude = it },
                            label = { Text("Longitude") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Severity Selector
                    Text(
                        text = "SEVERITY LEVEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RiskLevel.values().forEach { sev ->
                            FilterChip(
                                selected = selectedSeverity == sev,
                                onClick = { selectedSeverity = sev },
                                label = { Text(sev.label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Description Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Incident Description & Road Condition") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // Photo Attachment Simulator
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, SlateBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = CyanHighlight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (hasPhotoAttached) "Incident_Photo_Bomdila_01.jpg" else "No Photo Attached",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (hasPhotoAttached) "Geotagged • 2.4 MB • Exif GPS included" else "Attach damage proof",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            TextButton(onClick = { hasPhotoAttached = !hasPhotoAttached }) {
                                Text(if (hasPhotoAttached) "Remove" else "Attach Photo", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Primary Submit Button (Mandatory as per prompt)
                    Button(
                        onClick = {
                            val lat = latitude.toDoubleOrNull() ?: 27.26
                            val lng = longitude.toDoubleOrNull() ?: 92.42
                            onSubmitReport(
                                selectedType,
                                locationName,
                                lat,
                                lng,
                                description,
                                selectedSeverity,
                                hasPhotoAttached
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyMedium),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "SUBMIT FIELD REPORT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Recent Ground Field Reports List
        item {
            Text(
                text = "RECENT FIELD REPORTS FROM GROUND OFFICERS",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }

        items(fieldReports.size) { index ->
            val report = fieldReports[index]
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${report.type.label}: ${report.locationName}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        AlertBadge(level = report.severity)
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reported by: ${report.officerName} • ${report.timestamp}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (report.hasPhoto) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = CyanHighlight, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                            }
                            Icon(
                                imageVector = if (report.isSynced) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = if (report.isSynced) RiskLowGreen else RiskHighOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = if (report.isSynced) "Synced" else "Local Queue",
                                fontSize = 10.sp,
                                color = if (report.isSynced) RiskLowGreen else RiskHighOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

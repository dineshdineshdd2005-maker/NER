package com.example

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.data.MockDataProvider
import com.example.model.RiskLevel
import com.example.ui.components.MapGeoBounds
import com.example.ui.components.getSeverityColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherMarkerTest {

    @Test
    fun `getSeverityColor returns Green for Low severity`() {
        val color = getSeverityColor(RiskLevel.LOW)
        assertEquals(Color(0xFF10B981), color)
    }

    @Test
    fun `getSeverityColor returns Yellow for Medium severity`() {
        val color = getSeverityColor(RiskLevel.MEDIUM)
        assertEquals(Color(0xFFF59E0B), color)
    }

    @Test
    fun `getSeverityColor returns Red for High and Critical severity`() {
        val highColor = getSeverityColor(RiskLevel.HIGH)
        val criticalColor = getSeverityColor(RiskLevel.CRITICAL)

        assertEquals(Color(0xFFEF4444), highColor)
        assertEquals(Color(0xFFDC2626), criticalColor)
    }

    @Test
    fun `dynamic coordinate projection maps GPS within viewport`() {
        val bounds = MapGeoBounds.NER_DEFAULT
        val width = 400f
        val height = 300f

        // Bomdila/West Kameng (North-West)
        val offsetNorth = bounds.project(
            latitude = 27.50,
            longitude = 92.25,
            mapWidthPx = width,
            mapHeightPx = height
        )

        // Silchar/Barak Valley (South)
        val offsetSouth = bounds.project(
            latitude = 24.83,
            longitude = 92.78,
            mapWidthPx = width,
            mapHeightPx = height
        )

        // Northern latitude should map to smaller Y (higher on screen) than southern latitude
        assertTrue(offsetNorth.y < offsetSouth.y)

        // Both projected points should lie inside the map area
        assertTrue(offsetNorth.x in 0f..width)
        assertTrue(offsetNorth.y in 0f..height)
        assertTrue(offsetSouth.x in 0f..width)
        assertTrue(offsetSouth.y in 0f..height)
    }

    @Test
    fun `mock weatherZones have coordinate data and severity levels`() {
        val zones = MockDataProvider.weatherZones
        assertTrue(zones.isNotEmpty())

        val severities = zones.map { it.severity }
        assertTrue(severities.contains(RiskLevel.LOW))
        assertTrue(severities.contains(RiskLevel.MEDIUM))
        assertTrue(severities.contains(RiskLevel.CRITICAL))

        zones.forEach { zone ->
            assertTrue(zone.coordinates.latitude > 0.0)
            assertTrue(zone.coordinates.longitude > 0.0)
        }
    }
}

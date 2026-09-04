package com.example

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.data.MockDataProvider
import com.example.model.DeliveryItem
import com.example.model.DeliveryPriority
import com.example.model.GpsCoordinate
import com.example.model.RiskLevel
import com.example.ui.components.MapGeoBounds
import com.example.ui.components.getRouteRiskScoreColor
import com.example.ui.components.getRouteRiskScoreLabel
import com.example.ui.components.projectDeliveryRoutePoints
import org.junit.Assert.*
import org.junit.Test

class DeliveryPolylineTest {

    @Test
    fun testRouteRiskScoreColorCoding() {
        // Safe / Low Risk routes: score <= 35 -> Green
        val lowScoreColor = getRouteRiskScoreColor(20)
        assertEquals(Color(0xFF10B981), lowScoreColor)

        val boundaryLowColor = getRouteRiskScoreColor(35)
        assertEquals(Color(0xFF10B981), boundaryLowColor)

        // Moderate / Caution routes: score 36..65 -> Yellow/Amber
        val mediumScoreColor = getRouteRiskScoreColor(50)
        assertEquals(Color(0xFFF59E0B), mediumScoreColor)

        val boundaryMediumColor = getRouteRiskScoreColor(65)
        assertEquals(Color(0xFFF59E0B), boundaryMediumColor)

        // High / Critical routes: score > 65 -> Red
        val highScoreColor = getRouteRiskScoreColor(78)
        assertEquals(Color(0xFFEF4444), highScoreColor)

        val extremeScoreColor = getRouteRiskScoreColor(99)
        assertEquals(Color(0xFFEF4444), extremeScoreColor)
    }

    @Test
    fun testRouteRiskScoreLabels() {
        assertEquals("LOW RISK", getRouteRiskScoreLabel(25))
        assertEquals("MEDIUM RISK", getRouteRiskScoreLabel(55))
        assertEquals("HIGH RISK", getRouteRiskScoreLabel(80))
    }

    @Test
    fun testProjectDeliveryRoutePoints() {
        val testDelivery = DeliveryItem(
            id = "TEST-DEL-01",
            vehicleId = "VEH-01",
            origin = "Guwahati",
            destination = "Tawang",
            goodsType = "Medical Aid",
            priority = DeliveryPriority.EMERGENCY,
            eta = "2h 30m",
            status = "In Transit",
            riskLevel = RiskLevel.HIGH,
            riskScore = 78,
            startCoordinate = GpsCoordinate(26.14, 91.73, "Guwahati"),
            destinationCoordinate = GpsCoordinate(27.58, 91.86, "Tawang"),
            waypoints = listOf(
                GpsCoordinate(26.65, 92.79, "Tezpur"),
                GpsCoordinate(27.26, 92.42, "Bomdila")
            )
        )

        val bounds = MapGeoBounds.NER_DEFAULT
        val projectedPoints = projectDeliveryRoutePoints(
            delivery = testDelivery,
            bounds = bounds,
            mapWidthPx = 800f,
            mapHeightPx = 600f,
            zoomLevel = 1.0f,
            panOffset = Offset.Zero
        )

        // Should contain Start + 2 Waypoints + Destination = 4 points
        assertEquals(4, projectedPoints.size)

        // Verify points have valid screen coordinates
        for (point in projectedPoints) {
            assertTrue("Point x should be non-negative: ${point.x}", point.x >= 0f)
            assertTrue("Point y should be non-negative: ${point.y}", point.y >= 0f)
        }
    }

    @Test
    fun testMockDeliveriesHaveCoordinatesAndRiskScores() {
        val deliveries = MockDataProvider.initialDeliveries
        assertTrue("Deliveries list should not be empty", deliveries.isNotEmpty())

        val highRiskDelivery = deliveries.find { it.riskScore > 65 }
        assertNotNull("Should have at least one high risk delivery", highRiskDelivery)
        assertEquals(Color(0xFFEF4444), getRouteRiskScoreColor(highRiskDelivery!!.riskScore))

        val lowRiskDelivery = deliveries.find { it.riskScore <= 35 }
        assertNotNull("Should have at least one low risk delivery", lowRiskDelivery)
        assertEquals(Color(0xFF10B981), getRouteRiskScoreColor(lowRiskDelivery!!.riskScore))

        val mediumRiskDelivery = deliveries.find { it.riskScore in 36..65 }
        assertNotNull("Should have at least one medium risk delivery", mediumRiskDelivery)
        assertEquals(Color(0xFFF59E0B), getRouteRiskScoreColor(mediumRiskDelivery!!.riskScore))
    }
}

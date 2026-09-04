package com.example.data

import com.example.model.*

object MockDataProvider {

    val demoUsers = listOf(
        User(
            id = "USR-001",
            name = "Dr. Ananya Sarma",
            email = "admin.sarma@nerlogix.gov.in",
            role = UserRole.ADMINISTRATOR,
            department = "DoNER / MoRTH Regional Cell"
        ),
        User(
            id = "USR-002",
            name = "Rajesh Borah",
            email = "r.borah@brahmaputrafleet.com",
            role = UserRole.LOGISTICS_OPERATOR,
            department = "Regional Freight Command"
        ),
        User(
            id = "USR-003",
            name = "Tsering Dorjee",
            email = "tsering.truck104@nerfleet.in",
            role = UserRole.DRIVER,
            department = "High-Altitude Logistics Fleet"
        ),
        User(
            id = "USR-004",
            name = "Biren Gogoi",
            email = "biren.gogoi@pwd.arunachal.gov.in",
            role = UserRole.FIELD_OFFICER,
            department = "Arunachal PWD Disaster Response"
        ),
        User(
            id = "USR-005",
            name = "Capt. Moa Ao",
            email = "capt.ao@sdma.gov.in",
            role = UserRole.DISASTER_OFFICER,
            department = "State Disaster Management Authority"
        )
    )

    val initialVehicles = listOf(
        Vehicle(
            id = "NER-TRUCK-104",
            driverName = "Tsering Dorjee",
            driverPhone = "+91 94360 88219",
            type = "All-Terrain Freight Truck (16T)",
            currentLocation = "Near Tezpur (NH-15)",
            destination = "Tawang Military Hospital",
            speedKmh = 42,
            eta = "3h 20m",
            status = VehicleStatus.IN_TRANSIT,
            routeRisk = RiskLevel.MEDIUM,
            currentCoordinate = GpsCoordinate(26.65, 92.80, "Tezpur Bypass"),
            progressPercent = 0.38f,
            cargo = "Emergency Cardiac & Trauma Medicines"
        ),
        Vehicle(
            id = "NER-MED-002",
            driverName = "Debashish Roy",
            driverPhone = "+91 98640 12044",
            type = "Cold Chain Vaccine Carrier",
            currentLocation = "Orang National Corridor",
            destination = "Bomdila Civil Hospital",
            speedKmh = 48,
            eta = "2h 10m",
            status = VehicleStatus.IN_TRANSIT,
            routeRisk = RiskLevel.LOW,
            currentCoordinate = GpsCoordinate(26.72, 92.40, "Orang Sector"),
            progressPercent = 0.52f,
            cargo = "Pediatric Vaccines & Blood Plasma"
        ),
        Vehicle(
            id = "NER-HVY-305",
            driverName = "Lobsang Wangdi",
            driverPhone = "+91 94351 77301",
            type = "Heavy Multi-Axle Carrier (24T)",
            currentLocation = "Jorhat Bypass (NH-715)",
            destination = "Kohima Central Depot",
            speedKmh = 38,
            eta = "4h 45m",
            status = VehicleStatus.IN_TRANSIT,
            routeRisk = RiskLevel.HIGH,
            currentCoordinate = GpsCoordinate(26.75, 94.22, "Jorhat Outskirts"),
            progressPercent = 0.25f,
            cargo = "Food Grain Rations & Pulses (PDS)"
        ),
        Vehicle(
            id = "NER-FUEL-412",
            driverName = "Pranab Kalita",
            driverPhone = "+91 98540 33918",
            type = "Hazmat Diesel Fuel Tanker",
            currentLocation = "Nagaon Junction",
            destination = "Itanagar Power Substation",
            speedKmh = 44,
            eta = "5h 15m",
            status = VehicleStatus.IN_TRANSIT,
            routeRisk = RiskLevel.LOW,
            currentCoordinate = GpsCoordinate(26.35, 92.68, "Nagaon Crossing"),
            progressPercent = 0.30f,
            cargo = "High-Altitude Diesel (Arctic Grade)"
        ),
        Vehicle(
            id = "NER-VAN-208",
            driverName = "Malsawma Lushai",
            driverPhone = "+91 94361 55092",
            type = "4x4 Emergency Supply Van",
            currentLocation = "Shillong Peak Bypass",
            destination = "Silchar Disaster Relief Camp",
            speedKmh = 50,
            eta = "3h 05m",
            status = VehicleStatus.IN_TRANSIT,
            routeRisk = RiskLevel.MEDIUM,
            currentCoordinate = GpsCoordinate(25.57, 91.89, "East Khasi Hills"),
            progressPercent = 0.65f,
            cargo = "Water Purification Kits & Tarpaulins"
        )
    )

    val sampleRouteOptions = listOf(
        RouteOption(
            id = "RT-A",
            name = "Route A: NH-13 Direct Corridor",
            tag = "Fastest Route",
            distanceKm = 450,
            durationText = "10h 20m",
            riskLevel = RiskLevel.HIGH,
            riskScore = 78,
            isRecommended = false,
            explanation = "Direct alpine corridor via Bomdila pass. Extremely high landslide risk triggered by 65mm monsoonal rainfall on unstable phyllite slope.",
            waypoints = listOf("Guwahati", "Mangaldai", "Kharupetia", "Bhalukpong", "Bomdila Pass", "Dirang", "Sela Pass", "Tawang"),
            roadCondition = "Damaged asphalt & active rockfall zone near Km 142",
            weatherCondition = "Torrential Downpour (65mm/hr)"
        ),
        RouteOption(
            id = "RT-B",
            name = "Route B: Kalaktang-Rupa Protected Bypass",
            tag = "AI RECOMMENDED ROUTE",
            distanceKm = 475,
            durationText = "11h 05m",
            riskLevel = RiskLevel.LOW,
            riskScore = 24,
            isRecommended = true,
            explanation = "Recommended because it has lower predicted disruption risk despite slightly longer travel time. Features reinforced concrete culverts, lower slope vulnerability, and active BRO clearing teams.",
            waypoints = listOf("Guwahati", "Rowta", "Bhairabkunda", "Balemu", "Kalaktang", "Rupa Valley", "Dirang", "Sela Pass", "Tawang"),
            roadCondition = "Stable engineered highway with geogrid retaining walls",
            weatherCondition = "Light Mist / Intermittent Drizzle (12mm/hr)"
        ),
        RouteOption(
            id = "RT-C",
            name = "Route C: Darranga-Tashigang Transit Corridor",
            tag = "Alternative Route",
            distanceKm = 510,
            durationText = "12h 10m",
            riskLevel = RiskLevel.MEDIUM,
            riskScore = 52,
            isRecommended = false,
            explanation = "Scenic western perimeter bypass. Moderate risk due to low cellular connectivity in deep gorges and narrow curves.",
            waypoints = listOf("Guwahati", "Tamulpur", "Darranga", "Wamrong", "Trashigang", "Lumla", "Tawang"),
            roadCondition = "Single-lane asphalt with slow freight traffic",
            weatherCondition = "Overcast with Dense Mountain Fog"
        )
    )

    val initialAlerts = listOf(
        AlertItem(
            id = "ALT-901",
            severity = RiskLevel.CRITICAL,
            title = "Landslide Blockage on NH-13",
            description = "Major rockfall and debris accumulation detected near Bomdila Sector Km 142. Both lanes completely blocked.",
            location = "Bomdila Pass, West Kameng, Arunachal Pradesh",
            timeAgo = "12m ago",
            source = "PWD Field Sensor & IoT Geophone Array",
            recommendedAction = "Immediately reroute all commercial freight via Route B (Kalaktang Bypass)",
            relatedVehicleId = "NER-TRUCK-104"
        ),
        AlertItem(
            id = "ALT-902",
            severity = RiskLevel.HIGH,
            title = "Flash Flood & Inundation Warning",
            description = "Brahmaputra tributary Jiadhal river exceeding danger level by 1.8m. Low-lying highway causeway submerged.",
            location = "Dhemaji-Silapathar Corridor (NH-515), Assam",
            timeAgo = "34m ago",
            source = "Central Water Commission (CWC) Telemetry",
            recommendedAction = "Restrict light vehicles; dispatch emergency pilot convoy"
        ),
        AlertItem(
            id = "ALT-903",
            severity = RiskLevel.MEDIUM,
            title = "Dense Mountain Fog & Low Visibility (<20m)",
            description = "Heavy convective cloud ceiling lowering over Sela Pass (13,700 ft). Severe ice slicks on switchbacks.",
            location = "Sela Pass Summit, Arunachal Pradesh",
            timeAgo = "1h 15m ago",
            source = "Border Roads Organisation (BRO) High-Altitude Station",
            recommendedAction = "Enforce 20 km/h speed limit and tire chain mandates"
        ),
        AlertItem(
            id = "ALT-904",
            severity = RiskLevel.LOW,
            title = "Precautionary Culvert Maintenance",
            description = "Routine clearing of silt and timber debris from culvert spans. One-lane alternating traffic.",
            location = "Jorhat-Golaghat Connector, Assam",
            timeAgo = "2h ago",
            source = "Assam State Highway Patrol",
            recommendedAction = "Expect 15-20 min transit delay during daytime"
        )
    )

    val initialFieldReports = listOf(
        FieldReport(
            id = "REP-701",
            type = ReportType.LANDSLIDE,
            locationName = "NH-13 Bomdila Sector Km 142.4",
            coordinates = GpsCoordinate(27.26, 92.42, "Bomdila"),
            description = "Mud and boulder slip estimated 850 cubic meters. Heavy excavator deployed by Project Vartak. Clearance underway.",
            severity = RiskLevel.CRITICAL,
            officerName = "Biren Gogoi (Field Engg)",
            timestamp = "Today, 08:45 AM",
            isSynced = true,
            hasPhoto = true
        ),
        FieldReport(
            id = "REP-702",
            type = ReportType.ROAD_DAMAGE,
            locationName = "Bhalukpong Forest Checkpost",
            coordinates = GpsCoordinate(27.01, 92.64, "Bhalukpong"),
            description = "Road embankment erosion due to swelling Kameng river. Right shoulder cordoned off.",
            severity = RiskLevel.HIGH,
            officerName = "T. Tsering (Disaster Cell)",
            timestamp = "Today, 07:15 AM",
            isSynced = true,
            hasPhoto = true
        ),
        FieldReport(
            id = "REP-703",
            type = ReportType.FLOOD,
            locationName = "Gohpur Floodplain Causeway",
            coordinates = GpsCoordinate(26.88, 93.63, "Gohpur"),
            description = "Water logging 18 inches over road deck for 120 meters. High clearance trucks passable.",
            severity = RiskLevel.MEDIUM,
            officerName = "K. Hazarika (Traffic Warden)",
            timestamp = "Yesterday, 18:30 PM",
            isSynced = true,
            hasPhoto = true
        )
    )

    val initialDeliveries = listOf(
        DeliveryItem(
            id = "DEL-NER-5501",
            vehicleId = "NER-TRUCK-104",
            origin = "Guwahati Central Depot",
            destination = "Tawang Military Hospital",
            goodsType = "Medicines & Trauma Kits",
            priority = DeliveryPriority.EMERGENCY,
            eta = "11:30 AM",
            status = "In Transit",
            riskLevel = RiskLevel.HIGH,
            riskScore = 78,
            startCoordinate = GpsCoordinate(26.14, 91.73, "Guwahati Central Depot"),
            destinationCoordinate = GpsCoordinate(27.58, 91.86, "Tawang Military Hospital"),
            waypoints = listOf(
                GpsCoordinate(26.65, 92.79, "Tezpur Transit"),
                GpsCoordinate(27.01, 92.64, "Bhalukpong Gate"),
                GpsCoordinate(27.26, 92.42, "Bomdila Pass"),
                GpsCoordinate(27.35, 92.24, "Dirang Valley")
            )
        ),
        DeliveryItem(
            id = "DEL-NER-5502",
            vehicleId = "NER-MED-002",
            origin = "Tezpur Medical College",
            destination = "Bomdila Civil Hospital",
            goodsType = "Vaccines & Plasma",
            priority = DeliveryPriority.HIGH,
            eta = "01:15 PM",
            status = "In Transit",
            riskLevel = RiskLevel.LOW,
            riskScore = 22,
            startCoordinate = GpsCoordinate(26.65, 92.79, "Tezpur Medical College"),
            destinationCoordinate = GpsCoordinate(27.26, 92.42, "Bomdila Civil Hospital"),
            waypoints = listOf(
                GpsCoordinate(26.72, 92.40, "Orang Sector"),
                GpsCoordinate(27.01, 92.64, "Bhalukpong")
            )
        ),
        DeliveryItem(
            id = "DEL-NER-5503",
            vehicleId = "NER-HVY-305",
            origin = "Jorhat FCI Grain Silo",
            destination = "Kohima Central Depot",
            goodsType = "Food Rations (PDS)",
            priority = DeliveryPriority.NORMAL,
            eta = "04:45 PM",
            status = "In Transit",
            riskLevel = RiskLevel.HIGH,
            riskScore = 74,
            startCoordinate = GpsCoordinate(26.75, 94.22, "Jorhat FCI Grain Silo"),
            destinationCoordinate = GpsCoordinate(25.67, 94.11, "Kohima Central Depot"),
            waypoints = listOf(
                GpsCoordinate(26.52, 93.97, "Golaghat Junction"),
                GpsCoordinate(25.91, 93.73, "Dimapur Bypass")
            )
        ),
        DeliveryItem(
            id = "DEL-NER-5504",
            vehicleId = "NER-FUEL-412",
            origin = "Numaligarh Refinery (NRL)",
            destination = "Itanagar Power Grid",
            goodsType = "Arctic Grade Diesel Fuel",
            priority = DeliveryPriority.HIGH,
            eta = "05:15 PM",
            status = "In Transit",
            riskLevel = RiskLevel.LOW,
            riskScore = 28,
            startCoordinate = GpsCoordinate(26.59, 93.73, "Numaligarh Refinery"),
            destinationCoordinate = GpsCoordinate(27.08, 93.60, "Itanagar Power Grid"),
            waypoints = listOf(
                GpsCoordinate(26.88, 93.63, "Gohpur Causeway"),
                GpsCoordinate(27.02, 93.81, "Banderdewa Gate")
            )
        ),
        DeliveryItem(
            id = "DEL-NER-5505",
            vehicleId = "NER-VAN-208",
            origin = "Shillong Relief HQ",
            destination = "Silchar Flood Relief Camp",
            goodsType = "Emergency Supplies & Purifiers",
            priority = DeliveryPriority.EMERGENCY,
            eta = "03:00 PM",
            status = "In Transit",
            riskLevel = RiskLevel.MEDIUM,
            riskScore = 54,
            startCoordinate = GpsCoordinate(25.57, 91.89, "Shillong Relief HQ"),
            destinationCoordinate = GpsCoordinate(24.83, 92.78, "Silchar Flood Relief Camp"),
            waypoints = listOf(
                GpsCoordinate(25.45, 92.20, "Jowai Highway"),
                GpsCoordinate(25.35, 92.37, "Khliehriat Ridge"),
                GpsCoordinate(24.90, 92.60, "Badarpur Junction")
            )
        )
    )

    val weatherZones = listOf(
        WeatherZone(
            id = "WZ-01",
            regionName = "West Kameng & Tawang Alpine Corridor",
            rainfallMmPerHour = 62.4,
            temperatureCelsius = 12,
            windKmh = 45,
            floodWarning = false,
            landslideProbabilityPercent = 86,
            roadCondition = "High Rockfall & Mudslide Hazard",
            riskLevel = RiskLevel.CRITICAL,
            coordinates = GpsCoordinate(27.50, 92.25, "West Kameng - Tawang")
        ),
        WeatherZone(
            id = "WZ-02",
            regionName = "Central Brahmaputra Basin (Tezpur-Guwahati)",
            rainfallMmPerHour = 28.0,
            temperatureCelsius = 29,
            windKmh = 18,
            floodWarning = true,
            landslideProbabilityPercent = 15,
            roadCondition = "Submerged Lowland Shoulders",
            riskLevel = RiskLevel.HIGH,
            coordinates = GpsCoordinate(26.65, 92.79, "Tezpur - Guwahati Basin")
        ),
        WeatherZone(
            id = "WZ-03",
            regionName = "Meghalaya Plateau (Cherrapunji-Shillong)",
            rainfallMmPerHour = 38.5,
            temperatureCelsius = 19,
            windKmh = 24,
            floodWarning = false,
            landslideProbabilityPercent = 42,
            roadCondition = "Heavy Mist & Slippery Surface",
            riskLevel = RiskLevel.MEDIUM,
            coordinates = GpsCoordinate(25.57, 91.89, "Cherrapunji - Shillong")
        ),
        WeatherZone(
            id = "WZ-04",
            regionName = "Barak Valley & Cachar (Silchar-Badarpur)",
            rainfallMmPerHour = 14.2,
            temperatureCelsius = 31,
            windKmh = 12,
            floodWarning = false,
            landslideProbabilityPercent = 22,
            roadCondition = "Fair with Pothole Patches",
            riskLevel = RiskLevel.LOW,
            coordinates = GpsCoordinate(24.83, 92.78, "Barak Valley - Silchar")
        ),
        WeatherZone(
            id = "WZ-05",
            regionName = "Nagaland Mountain Ridge (Dimapur-Kohima)",
            rainfallMmPerHour = 22.0,
            temperatureCelsius = 21,
            windKmh = 16,
            floodWarning = false,
            landslideProbabilityPercent = 54,
            roadCondition = "Periodic Subsidence at Phesama",
            riskLevel = RiskLevel.MEDIUM,
            coordinates = GpsCoordinate(25.67, 94.11, "Kohima Ridge")
        )
    )

    val demoScenarioSteps = listOf(
        DemoScenarioStep(
            stepNumber = 1,
            title = "Dispatch & Mission Start",
            description = "A temperature-controlled medicine freight truck (NER-TRUCK-104) is dispatched from Guwahati carrying critical trauma supplies bound for Tawang.",
            vehicleLocation = "Guwahati Exit (NH-27 Junction)",
            riskScore = 28,
            riskLevel = RiskLevel.LOW,
            alertGenerated = null,
            activeRouteName = "Route A: NH-13 Direct Corridor"
        ),
        DemoScenarioStep(
            stepNumber = 2,
            title = "Weather Influx: Monsoonal Rain Detected",
            description = "Satellite meteorological radar detects convective cloud bursts (68mm/h) across the lower Himalayan foothills of West Kameng.",
            vehicleLocation = "Tezpur Outskirts (NH-15)",
            riskScore = 55,
            riskLevel = RiskLevel.MEDIUM,
            alertGenerated = "Heavy rainfall detected. Risk level increased on NH-13.",
            activeRouteName = "Route A: NH-13 Direct Corridor"
        ),
        DemoScenarioStep(
            stepNumber = 3,
            title = "AI Risk Engine Elevation",
            description = "Machine Learning model factors steep 42° terrain slope, soil saturation index, and rainfall intensity, raising landslide probability to 84%.",
            vehicleLocation = "Near Bhalukpong Gate",
            riskScore = 78,
            riskLevel = RiskLevel.HIGH,
            alertGenerated = "AI Warning: High Landslide Vulnerability along Bomdila Corridor.",
            activeRouteName = "Route A: NH-13 Direct Corridor"
        ),
        DemoScenarioStep(
            stepNumber = 4,
            title = "Disruption Event: Road Blockage Simulated",
            description = "A massive 850m³ rockfall and slope collapse occurs at NH-13 Km 142 near Bomdila Pass. Road completely impassable.",
            vehicleLocation = "Approaching Foothill Junction",
            riskScore = 94,
            riskLevel = RiskLevel.CRITICAL,
            alertGenerated = "CRITICAL: Landslide reported near Bomdila. Route temporarily blocked.",
            activeRouteName = "Route A (BLOCKED)",
            roadBlockedLocation = "Bomdila Pass (NH-13 Km 142)"
        ),
        DemoScenarioStep(
            stepNumber = 5,
            title = "Automated Critical Incident Broadcast",
            description = "IoT geophone alert triggers automated dispatch notification to Regional Logistics Center and all in-transit drivers.",
            vehicleLocation = "Holding at Bhairabkunda Junction",
            riskScore = 94,
            riskLevel = RiskLevel.CRITICAL,
            alertGenerated = "High Risk Alert broadcasted to vehicle fleet.",
            activeRouteName = "Route A (BLOCKED)"
        ),
        DemoScenarioStep(
            stepNumber = 6,
            title = "AI Route Optimization & Safer Path",
            description = "AI Optimizer evaluates 3 topological candidates and selects Route B (Kalaktang-Rupa Bypass), avoiding the landslide sector with a safe risk score of 24/100.",
            vehicleLocation = "Bhairabkunda Divergence Point",
            riskScore = 24,
            riskLevel = RiskLevel.LOW,
            alertGenerated = "AI RECOMMENDED ROUTE generated: Rerouting via Kalaktang Bypass.",
            activeRouteName = "Route B: Kalaktang-Rupa Protected Bypass (AI RECOMMENDED)"
        ),
        DemoScenarioStep(
            stepNumber = 7,
            title = "Driver Navigation System Updated",
            description = "In-cab navigation console on NER-TRUCK-104 receives dynamic reroute coordinates. Driver Tsering Dorjee accepts turn guidance.",
            vehicleLocation = "Kalaktang Access Highway",
            riskScore = 24,
            riskLevel = RiskLevel.LOW,
            alertGenerated = "Driver acknowledged dynamic rerouting.",
            activeRouteName = "Route B: Kalaktang-Rupa Protected Bypass"
        ),
        DemoScenarioStep(
            stepNumber = 8,
            title = "Real-Time GPS Tracking Across Safer Corridor",
            description = "Telemetry shows vehicle smoothly cruising at 46 km/h along the well-drained Kalaktang mountain highway.",
            vehicleLocation = "Cruising through Rupa Valley",
            riskScore = 22,
            riskLevel = RiskLevel.LOW,
            alertGenerated = null,
            activeRouteName = "Route B: Kalaktang-Rupa Protected Bypass"
        ),
        DemoScenarioStep(
            stepNumber = 9,
            title = "Field Officer Verifies Blockage via App",
            description = "Field Officer Biren Gogoi submits geotagged incident report with photo evidence of Bomdila landslide. PWD clearing status synced.",
            vehicleLocation = "Ascending towards Dirang",
            riskScore = 22,
            riskLevel = RiskLevel.LOW,
            alertGenerated = "Field Report Synced: Incident #REP-701 verified on GIS Map.",
            activeRouteName = "Route B: Kalaktang-Rupa Protected Bypass"
        ),
        DemoScenarioStep(
            stepNumber = 10,
            title = "System-Wide GIS & Dashboard Convergence",
            description = "Central command dashboard updates route blockage polygons, rerouted delivery ETA (+45 mins), and fleet status automatically.",
            vehicleLocation = "Crossing Sela Pass Tunnel Sector",
            riskScore = 20,
            riskLevel = RiskLevel.LOW,
            alertGenerated = "Central GIS Map synchronized with active detour.",
            activeRouteName = "Route B: Kalaktang-Rupa Protected Bypass"
        ),
        DemoScenarioStep(
            stepNumber = 11,
            title = "Mission Success: Safe Delivery at Tawang",
            description = "NER-TRUCK-104 safely arrives at Tawang Military Hospital without incident, preserving vital cold-chain medicines despite severe regional disruption.",
            vehicleLocation = "Tawang Destination Reached (Arrived)",
            riskScore = 15,
            riskLevel = RiskLevel.LOW,
            alertGenerated = "Delivery Completed Successfully: Emergency Medicines Delivered.",
            activeRouteName = "Route B Completed Safely"
        )
    )
}

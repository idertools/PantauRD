package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.PairedDevice
import com.example.data.model.SafeZone
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen

@Composable
fun MapRadarView(
    device: PairedDevice?,
    safeZones: List<SafeZone>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // "MAP" (Peta Jalan OpenStreetMap) or "RADAR" (Radar Vektor HUD)
    var mapType by remember { mutableStateOf("MAP") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val lat = device?.latitude ?: -6.2088
    val lon = device?.longitude ?: 106.8456
    val deviceName = device?.name ?: "Perangkat Sasaran"
    val isEmergency = device?.emergencyStatus == true

    // Pulse animation for radar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    // Update real-time map marker position when coordinates change
    LaunchedEffect(lat, lon, isEmergency, mapType) {
        if (mapType == "MAP") {
            webViewRef?.evaluateJavascript(
                "if (typeof updateTargetPosition === 'function') { updateTargetPosition($lat, $lon, $isEmergency); }",
                null
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(GuardNavyDark)
            .border(1.dp, Color(0xFF1E2E44), RoundedCornerShape(20.dp))
            .testTag("map_radar_container")
    ) {
        if (mapType == "MAP") {
            // Real-time interactive OpenStreetMap (Leaflet with CartoDB Voyager tiles)
            RealtimeOpenStreetMap(
                latitude = lat,
                longitude = lon,
                deviceName = deviceName,
                isEmergency = isEmergency,
                safeZones = safeZones,
                onWebViewCreated = { webViewRef = it }
            )
        } else {
            // Vector Tactical Radar Canvas
            TacticalRadarCanvas(
                device = device,
                safeZones = safeZones,
                pulseProgress = pulseProgress
            )
        }

        // Top Status HUD & Map Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Status Tag
            Surface(
                color = Color(0xEE09111E),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF223A5B))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isEmergency) GuardEmergencyRed
                                else if (device?.isMonitoringActive == false) Color(0xFFF59E0B)
                                else GuardSafeGreen
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEmergency) "SOS DARURAT"
                        else if (device?.isMonitoringActive == false) "HEMAT DAYA"
                        else if (mapType == "MAP") "PETA JALAN REAL-TIME"
                        else "RADAR TAKTIS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Map Control Actions
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Toggle Map Style (Peta Jalan vs Radar)
                Surface(
                    color = Color(0xEE09111E),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF223A5B))
                ) {
                    IconButton(
                        onClick = {
                            mapType = if (mapType == "MAP") "RADAR" else "MAP"
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_map_view_btn")
                    ) {
                        Icon(
                            imageVector = if (mapType == "MAP") Icons.Default.Radar else Icons.Default.Map,
                            contentDescription = if (mapType == "MAP") "Ganti ke Radar" else "Ganti ke Peta Real-Time",
                            tint = GuardPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Re-center on target location
                if (mapType == "MAP") {
                    Surface(
                        color = Color(0xEE09111E),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF223A5B))
                    ) {
                        IconButton(
                            onClick = {
                                webViewRef?.evaluateJavascript(
                                    "if (typeof centerOnTarget === 'function') { centerOnTarget(); }",
                                    null
                                )
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("center_target_map_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = "Fokus Lokasi Target",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Open in Google Maps app for direct turn-by-turn navigation
                Surface(
                    color = Color(0xEE09111E),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF223A5B))
                ) {
                    IconButton(
                        onClick = {
                            openGoogleMaps(context, lat, lon, deviceName)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("open_gmaps_external_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "Buka Navigasi Google Maps",
                            tint = GuardSafeGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom Telemetry Overlay
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp),
            color = Color(0xF00D1B2A),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFF223A5B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device?.addressName ?: "Mendeteksi alamat target...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "%.5f, %.5f • Kecepatan: %.1f km/h".format(
                            lat,
                            lon,
                            device?.speedKmh ?: 0f
                        ),
                        color = Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = Color(0x2200D1C1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = GuardSafeGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = device?.safeZoneStatus ?: "Zona Aman",
                            color = GuardSafeGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RealtimeOpenStreetMap(
    latitude: Double,
    longitude: Double,
    deviceName: String,
    isEmergency: Boolean,
    safeZones: List<SafeZone>,
    onWebViewCreated: (WebView) -> Unit
) {
    val escapedName = deviceName.replace("'", "\\'")
    val safeZonesJson = buildString {
        append("[")
        safeZones.forEachIndexed { i, z ->
            if (i > 0) append(",")
            append("{\"name\":\"${z.name.replace("'", "\\'")}\",\"lat\":${z.latitude},\"lon\":${z.longitude},\"radius\":${z.radiusMeters}}")
        }
        append("]")
    }

    val htmlContent = remember(latitude, longitude, isEmergency, safeZonesJson) {
        generateMapHtml(
            lat = latitude,
            lon = longitude,
            deviceName = escapedName,
            isEmergency = isEmergency,
            safeZonesJson = safeZonesJson
        )
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }
                webViewClient = WebViewClient()
                setBackgroundColor(0xFF09111E.toInt())
                loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
                onWebViewCreated(this)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
        }
    )
}

@Composable
private fun TacticalRadarCanvas(
    device: PairedDevice?,
    safeZones: List<SafeZone>,
    pulseProgress: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = (size.minDimension / 2f) * 0.9f

        // Map grid lines
        val gridStep = 36.dp.toPx()
        val gridColor = Color(0x1800D1C1)
        var x = 0f
        while (x <= size.width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += gridStep
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += gridStep
        }

        // Radar circular rings
        val ringCount = 4
        for (i in 1..ringCount) {
            val r = (maxRadius / ringCount) * i
            drawCircle(
                color = Color(0x2800D1C1),
                radius = r,
                center = center,
                style = Stroke(
                    width = 1.2f,
                    pathEffect = if (i % 2 == 0) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
                )
            )
        }

        // Animated sweep or ripple
        val currentPulseRadius = maxRadius * pulseProgress
        val pulseColor = if (device?.emergencyStatus == true)
            GuardEmergencyRed.copy(alpha = (1f - pulseProgress) * 0.6f)
        else
            GuardPrimaryCyan.copy(alpha = (1f - pulseProgress) * 0.45f)

        drawCircle(
            color = pulseColor,
            radius = currentPulseRadius,
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw Safe Zone representations
        safeZones.forEachIndexed { index, zone ->
            val angle = Math.toRadians((index * 110.0) + 40.0)
            val distFactor = (maxRadius * 0.6f)
            val zoneCenter = Offset(
                center.x + (Math.cos(angle) * distFactor).toFloat(),
                center.y + (Math.sin(angle) * distFactor).toFloat()
            )

            drawCircle(
                color = GuardSafeGreen.copy(alpha = 0.15f),
                radius = 35.dp.toPx(),
                center = zoneCenter
            )
            drawCircle(
                color = GuardSafeGreen.copy(alpha = 0.6f),
                radius = 35.dp.toPx(),
                center = zoneCenter,
                style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
            )
        }

        // Device Pin marker in center
        val pinColor = if (device?.emergencyStatus == true) GuardEmergencyRed else GuardPrimaryCyan
        drawCircle(
            brush = Brush.radialGradient(
                listOf(pinColor.copy(alpha = 0.9f), pinColor.copy(alpha = 0.1f)),
                center = center,
                radius = 24.dp.toPx()
            ),
            radius = 24.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = 7.dp.toPx(),
            center = center
        )
        drawCircle(
            color = pinColor,
            radius = 5.dp.toPx(),
            center = center
        )
    }
}

private fun openGoogleMaps(context: Context, lat: Double, lon: Double, label: String) {
    try {
        val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to web Google Maps
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    } catch (_: Exception) {
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}

private fun generateMapHtml(
    lat: Double,
    lon: Double,
    deviceName: String,
    isEmergency: Boolean,
    safeZonesJson: String
): String {
    val pinColor = if (isEmergency) "#EF4444" else "#00D1C1"
    val pinPulse = if (isEmergency) "rgba(239, 68, 68, 0.4)" else "rgba(0, 209, 193, 0.4)"

    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        html, body, #map {
            margin: 0; padding: 0; width: 100%; height: 100%;
            background-color: #0B1523; font-family: -apple-system, Roboto, sans-serif;
        }
        .leaflet-control-attribution, .leaflet-control-zoom {
            display: none !important;
        }
        .beacon-wrapper {
            position: relative;
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .beacon-core {
            width: 14px;
            height: 14px;
            background-color: $pinColor;
            border: 2.5px solid #FFFFFF;
            border-radius: 50%;
            box-shadow: 0 0 10px $pinColor;
            z-index: 2;
        }
        .beacon-wave {
            position: absolute;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: $pinPulse;
            animation: pulse-ring 2s infinite ease-out;
            z-index: 1;
        }
        @keyframes pulse-ring {
            0% { transform: scale(0.4); opacity: 1; }
            100% { transform: scale(1.6); opacity: 0; }
        }
        .leaflet-popup-content-wrapper {
            background-color: #0E1A2B !important;
            color: #FFFFFF !important;
            border: 1px solid #1E334D !important;
            border-radius: 10px !important;
            box-shadow: 0 4px 12px rgba(0,0,0,0.5) !important;
        }
        .leaflet-popup-tip {
            background-color: #0E1A2B !important;
        }
        .popup-title {
            font-size: 12px;
            font-weight: bold;
            color: $pinColor;
            margin-bottom: 2px;
        }
        .popup-desc {
            font-size: 10px;
            color: #CAD5E2;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var curLat = $lat;
        var curLon = $lon;
        var map = L.map('map', {
            zoomControl: false,
            attributionControl: false
        }).setView([curLat, curLon], 15);

        // Free OpenStreetMap Tiles via CartoDB Voyager (clean, clear road labels, lightweight)
        L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
            maxZoom: 19,
            subdomains: 'abcd'
        }).addTo(map);

        // Safe Zones
        var safeZones = $safeZonesJson;
        safeZones.forEach(function(sz) {
            L.circle([sz.lat, sz.lon], {
                color: '#10B981',
                fillColor: '#10B981',
                fillOpacity: 0.15,
                weight: 1.5,
                dashArray: '4, 4',
                radius: sz.radius
            }).addTo(map).bindPopup("<div class='popup-title'>Zona: " + sz.name + "</div><div class='popup-desc'>Radius: " + sz.radius + "m</div>");
        });

        // Pulsing Marker Icon
        var customIcon = L.divIcon({
            className: 'beacon-wrapper',
            html: '<div class="beacon-wave"></div><div class="beacon-core"></div>',
            iconSize: [32, 32],
            iconAnchor: [16, 16],
            popupAnchor: [0, -14]
        });

        var targetMarker = L.marker([curLat, curLon], { icon: customIcon }).addTo(map);
        targetMarker.bindPopup("<div class='popup-title'>$deviceName</div><div class='popup-desc'>Koordinat: " + curLat.toFixed(5) + ", " + curLon.toFixed(5) + "</div>").openPopup();

        function updateTargetPosition(newLat, newLon, isSos) {
            curLat = newLat;
            curLon = newLon;
            targetMarker.setLatLng([newLat, newLon]);
            map.panTo([newLat, newLon]);
        }

        function centerOnTarget() {
            map.flyTo([curLat, curLon], 16, { animate: true, duration: 0.8 });
            targetMarker.openPopup();
        }
    </script>
</body>
</html>
    """.trimIndent()
}

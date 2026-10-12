package com.example.infobahaya.presentation.home.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.presentation.home.MapLayerType
import org.json.JSONArray
import org.json.JSONObject

class MapBridge(
    private val onSelectReport: (HazardReport?) -> Unit,
    private val reportsProvider: () -> List<HazardReport>
) {
    @JavascriptInterface
    fun onMarkerClicked(reportId: String) {
        val report = reportsProvider().find { it.id == reportId }
        onSelectReport(report)
    }

    @JavascriptInterface
    fun onMapClicked() {
        onSelectReport(null)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HazardMapCanvas(
    reports: List<HazardReport>,
    selectedReport: HazardReport?,
    onSelectReport: (HazardReport?) -> Unit,
    userLatitude: Double,
    userLongitude: Double,
    layerType: MapLayerType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val bridge = remember(onSelectReport) {
        MapBridge(onSelectReport) { reports }
    }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webViewClient = WebViewClient()
            addJavascriptInterface(bridge, "AndroidBridge")

            val htmlData = generateLeafletHtml(
                reports = reports,
                selectedReportId = selectedReport?.id,
                userLat = userLatitude,
                userLng = userLongitude,
                layerType = layerType
            )
            loadDataWithBaseURL("https://leafletjs.com", htmlData, "text/html", "UTF-8", null)
        }
    }

    // Dynamic Updates to Leaflet Map without re-creating WebView
    LaunchedEffect(reports, selectedReport, layerType, userLatitude, userLongitude) {
        val reportsJson = generateReportsJson(reports)
        val selectedId = selectedReport?.id ?: ""
        val jsCall = "if (window.updateMap) { window.updateMap($reportsJson, '$selectedId', '${layerType.name}', $userLatitude, $userLongitude); }"
        webView.evaluateJavascript(jsCall, null)
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun generateReportsJson(reports: List<HazardReport>): String {
    val jsonArray = JSONArray()
    for (r in reports) {
        val obj = JSONObject().apply {
            put("id", r.id)
            put("title", r.title)
            put("category", r.category.displayName)
            put("severity", r.severity.name)
            put("color", getSeverityHexColor(r.severity))
            put("lat", r.latitude)
            put("lng", r.longitude)
            put("address", r.address)
        }
        jsonArray.put(obj)
    }
    return jsonArray.toString()
}

private fun getSeverityHexColor(severity: HazardSeverity): String {
    return when (severity) {
        HazardSeverity.DARURAT -> "#EF4444"
        HazardSeverity.TINGGI -> "#F97316"
        HazardSeverity.SEDANG -> "#F59E0B"
        HazardSeverity.RENDAH -> "#10B981"
    }
}

private fun generateLeafletHtml(
    reports: List<HazardReport>,
    selectedReportId: String?,
    userLat: Double,
    userLng: Double,
    layerType: MapLayerType
): String {
    val reportsJson = generateReportsJson(reports)
    val selId = selectedReportId ?: ""

    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; background: #e2e8f0; }
        .leaflet-container { background: #e2e8f0; }
        .hazard-pin {
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            border: 2px solid #FFFFFF;
            box-shadow: 0 4px 10px rgba(0,0,0,0.35);
            cursor: pointer;
            transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
        }
        .hazard-pin.selected {
            transform: scale(1.35);
            border: 3px solid #0284C7;
            box-shadow: 0 0 16px rgba(2,132,199,0.9);
            z-index: 1000 !important;
        }
        .pin-inner {
            width: 8px;
            height: 8px;
            background-color: #FFFFFF;
            border-radius: 50%;
        }
        .user-marker {
            width: 18px;
            height: 18px;
            background-color: #0284C7;
            border: 3px solid #FFFFFF;
            border-radius: 50%;
            box-shadow: 0 0 12px rgba(2,132,199,0.8);
            position: relative;
        }
        .user-pulse {
            position: absolute;
            top: -11px;
            left: -11px;
            width: 34px;
            height: 34px;
            border-radius: 50%;
            background: rgba(2, 132, 199, 0.35);
            animation: pulse-ring 1.8s infinite ease-out;
            pointer-events: none;
        }
        @keyframes pulse-ring {
            0% { transform: scale(0.6); opacity: 0.9; }
            100% { transform: scale(2.2); opacity: 0; }
        }
        .popup-card {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            padding: 2px;
            max-width: 200px;
        }
        .popup-title {
            font-weight: 700;
            font-size: 13px;
            color: #0F172A;
            margin-bottom: 4px;
            line-height: 1.2;
        }
        .popup-category {
            font-size: 11px;
            font-weight: 600;
            margin-bottom: 6px;
        }
        .popup-address {
            font-size: 11px;
            color: #64748B;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', { zoomControl: false, attributionControl: false }).setView([$userLat, $userLng], 14);
        var currentTileLayer = null;
        var hazardMarkers = {};
        var userMarkerObj = null;

        function setTileLayer(type) {
            if (currentTileLayer) map.removeLayer(currentTileLayer);
            var tileUrl = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
            if (type === 'SATELLITE') {
                tileUrl = 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}';
            } else if (type === 'HEATMAP') {
                tileUrl = 'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png';
            }
            currentTileLayer = L.tileLayer(tileUrl, { maxZoom: 19 }).addTo(map);
        }

        setTileLayer('$layerType');

        map.on('click', function(e) {
            if (window.AndroidBridge && window.AndroidBridge.onMapClicked) {
                window.AndroidBridge.onMapClicked();
            }
        });

        function renderUserMarker(lat, lng) {
            if (userMarkerObj) map.removeLayer(userMarkerObj);
            var userIcon = L.divIcon({
                className: '',
                html: '<div class="user-pulse"></div><div class="user-marker"></div>',
                iconSize: [18, 18],
                iconAnchor: [9, 9]
            });
            userMarkerObj = L.marker([lat, lng], { icon: userIcon, zIndexOffset: 2000 }).addTo(map);
        }

        function renderHazardMarkers(reports, selectedId) {
            Object.keys(hazardMarkers).forEach(function(id) {
                map.removeLayer(hazardMarkers[id]);
            });
            hazardMarkers = {};

            reports.forEach(function(r) {
                var isSelected = (r.id === selectedId);
                var pinClass = isSelected ? 'hazard-pin selected' : 'hazard-pin';
                var size = isSelected ? 30 : 24;

                var customIcon = L.divIcon({
                    className: '',
                    html: '<div class="' + pinClass + '" style="background-color: ' + r.color + '; width: ' + size + 'px; height: ' + size + 'px;"><div class="pin-inner"></div></div>',
                    iconSize: [size, size],
                    iconAnchor: [size/2, size/2]
                });

                var m = L.marker([r.lat, r.lng], { icon: customIcon }).addTo(map);

                var popupContent = '<div class="popup-card">' +
                    '<div class="popup-title">' + r.title + '</div>' +
                    '<div class="popup-category" style="color: ' + r.color + '">' + r.category + ' (' + r.severity + ')</div>' +
                    '<div class="popup-address">' + r.address + '</div>' +
                    '</div>';

                m.bindPopup(popupContent);

                m.on('click', function(e) {
                    L.DomEvent.stopPropagation(e);
                    if (window.AndroidBridge && window.AndroidBridge.onMarkerClicked) {
                        window.AndroidBridge.onMarkerClicked(r.id);
                    }
                });

                hazardMarkers[r.id] = m;

                if (isSelected) {
                    map.panTo([r.lat, r.lng], { animate: true, duration: 0.5 });
                }
            });
        }

        renderUserMarker($userLat, $userLng);
        renderHazardMarkers($reportsJson, '$selId');

        window.updateMap = function(reports, selectedId, layerType, userLat, userLng) {
            setTileLayer(layerType);
            renderUserMarker(userLat, userLng);
            renderHazardMarkers(reports, selectedId);
        };
    </script>
</body>
</html>
    """.trimIndent()
}

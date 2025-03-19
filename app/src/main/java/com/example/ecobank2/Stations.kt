package com.example.ecobank2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class Stations : Fragment() {
    private var mapViewStations: MapView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var userMarker: Marker? = null
    private var firstLocationUpdate = true
    private lateinit var searchViewStations: AutoCompleteTextView
    private lateinit var distanceTextView: TextView
    private var lastSelectedStation: Station? = null
    private val routePoints = mutableListOf<GeoPoint>()




    private val ecoOilStations = listOf(
        Station("EcoOil - Sta. Barbara", 15.986001550108089, 120.47914042108087),
        Station("EcoOil - Bugallon", 15.970964, 120.217721),
        Station("EcoOil - EDSA Mandaluyong", 14.59950947, 121.0594116),
        Station("EcoOil - Cainta", 14.58617262637261, 121.11467083383992),
        Station("EcoOil - EDSA Pasay", 14.5386317, 121.0116554),
        Station("EcoOil - PAG-ASA", 12.33642488, 121.0760464),
        Station("EcoOil - Magsaysay", 12.30936364, 121.1498375),
        Station("EcoOil - Mamburao", 13.22510233, 120.5896923),
        Station("EcoOil - Malinta", 12.31294857, 123.5589562),
        Station("EcoOil - Baleno", 12.44066833, 123.5140002),
        Station("EcoOil - Balud", 12.03807066, 123.1952845),
        Station("EcoOil - Placer", 11.86879068, 123.90554),
        Station("EcoOil - Esperanza", 11.74553028, 124.0381113),
        Station("EcoOil - Odiongan", 12.42050864, 121.9891061),
        Station("EcoOil - Sibuyan", 12.37057086, 122.6862472),
        Station("EcoOil - Calaca", 13.95529561, 120.8210623),
        Station("EcoOil - Cuenca", 13.9003441, 121.059112),
        Station("EcoOil - San Jose", 13.86321816, 121.1086799),
        Station("EcoOil - Lipa", 13.95060845, 121.1660282),
        Station("EcoOil - Darasa", 14.06159487, 121.1528962),
        Station("EcoOil - Padre Pio", 14.11914841, 121.1720174),
        Station("EcoOil - Alabat", 14.1008542498042, 122.014720556498),
        Station("EcoOil - Quezon", 14.0022271472917, 122.172499690095),
        Station("EcoOil - Del Gallego", 13.92514301, 122.5982487),
        Station("EcoOil - Bangui", 18.52348869, 120.7457271),
        Station("EcoOil - Burgos", 18.53227522, 120.6152504),
        Station("EcoOil - Pasuquin", 18.46343895, 120.5795799),
        Station("EcoOil - Sulbec", 18.34852409, 120.6320222),
        Station("EcoOil - Bacarra", 18.26895031, 120.6063492),
        Station("EcoOil - Vintar", 18.31445503, 120.7807852),
        Station("EcoOil - Sta. Joaquina", 18.2057445, 120.5854389),
        Station("EcoOil - Brgy. 8", 18.20263221, 120.5942503),
        Station("EcoOil - Buttong", 18.20263221, 120.5942503),
        Station("EcoOil - San Nicolas", 18.16231425, 120.5894281),
        Station("EcoOil - Piddig", 18.16334063, 120.7159117),
        Station("EcoOil - Sarrat", 18.17129218, 120.6339919),
        Station("EcoOil - Batac", 18.05577389, 120.557265),
        Station("EcoOil - Currimao", 17.97613772, 120.4800386),
        Station("EcoOil - Nueva Era", 17.91616684, 120.6623922),
        Station("EcoOil - Narvacan", 17.41838745, 120.4782118),
        Station("EcoOil - Tagudin", 16.95811139, 120.4433675),
        Station("EcoOil - Lal-lo", 17.58138409, 121.7645046),
        Station("EcoOil - Gonzaga", 18.28258215, 122.0112954),
        Station("EcoOil - Magsaysay Bacolod", 10.65428351, 122.9388124),
        Station("EcoOil - Burgos Bacolod", 10.66140783, 122.974727),
        Station("EcoOil - Alijis", 10.64235412, 122.9447184),
        Station("EcoOil - Taculing", 10.65498093, 122.9655669),
        Station("EcoOil - Brgy. 10", 10.67499999, 122.9482515),
        Station("EcoOil - Circumferential East", 10.66269423, 122.9670724),
        Station("EcoOil - Montevista", 10.6736506, 122.9719817),
        Station("EcoOil - Sibulan", 9.335455681, 123.2926836),
        Station("EcoOil - Sta Catalina 1", 9.336681674, 122.8608281),
        Station("EcoOil - Sta Catalina 2", 9.33629568, 122.8606044),
        Station("EcoOil - Palinpinon", 9.310723006, 123.2811395),
        Station("EcoOil - Bio-os", 9.46869631, 123.1858163),
        Station("EcoOil - Kalumboyan", 9.507179089, 122.8101488),
        Station("EcoOil - Malabugas", 9.510125339, 122.8101031),
        Station("EcoOil - Baclayon", 9.626445716, 123.8957085),
        Station("EcoOil - Batuan", 9.783034772, 124.1486824),
        Station("EcoOil - Totolan", 9.636568905, 123.8461644),
        Station("EcoOil - Pilar", 9.854037572, 124.3465926),
        Station("EcoOil - San Miguel", 10.76457141, 122.4933942),
        Station("EcoOil - San Agustin", 10.10167971, 124.3170258),
        Station("EcoOil - Bagacay", 10.1557328, 124.2349812),
        Station("EcoOil - Digos", 6.9041126, 125.3215117),
        Station("EcoOil - Tagum", 7.4578376, 125.7947532),
        Station("EcoOil - Matina", 7.0757713, 125.5707263),
        Station("EcoOil - Gensan", 6.069639, 125.1362246),
        Station("EcoOil - Plaridel", 9.378333, 118.501667),
        Station("EcoOil - Cauayan", 9.972000, 122.624000),
        Station("EcoOil - Balatong", 18.15765, 120.55333)

    )

    data class Station(val title: String, val lat: Double, val lon: Double)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stations, container, false)
        mapViewStations = view.findViewById(R.id.mapViewStations)
        searchViewStations = view.findViewById(R.id.searchView)
        distanceTextView = view.findViewById(R.id.distanceTextView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val fabRefresh = view.findViewById<FloatingActionButton>(R.id.refresh)
        fabRefresh.setOnClickListener { refreshUserLocation() }

        setupMap()
        setupSearch()
        addEcoOilStations()
        requestLocationUpdates()
        clearRoutes() // Clear previous routes before drawing a new one
        drawPolyline(routePoints)


        return view
    }

    private fun setupMap() {
        mapViewStations?.setTileSource(TileSourceFactory.MAPNIK)
        mapViewStations?.setMultiTouchControls(true)
        mapViewStations?.controller?.setZoom(15.0)

        userMarker = Marker(mapViewStations).apply {
            title = "Your Location"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapViewStations?.overlays?.add(userMarker)
    }

    private fun setupSearch() {
        val stationNames = ecoOilStations.map { it.title }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            stationNames
        )
        searchViewStations.setAdapter(adapter)

        searchViewStations.setOnItemClickListener { _, _, position, _ ->
            val selectedStation = ecoOilStations.find { it.title == adapter.getItem(position) }
            selectedStation?.let {
                lastSelectedStation = it  // Store the selected station
                moveCameraToStation(it.lat, it.lon)

                // Clear previous routes before drawing a new one
                clearRoutes()

                // If user location is available, draw the route immediately
                val userLocation = userMarker?.position
                if (userLocation != null) {
                    drawRoute(userLocation.latitude, userLocation.longitude, it.lat, it.lon)
                }
            }
        }




    }

    private val drawnRoutes = mutableListOf<Polyline>() // Store drawn routes

    private fun drawRoute(startLat: Double, startLon: Double, endLat: Double, endLon: Double) {
        val baseUrl = "https://routing.openstreetmap.de/routed-car/route/v1/driving/"
        val routeUrl = "$baseUrl$startLon,$startLat;$endLon,$endLat?overview=full&geometries=geojson"

        val request = JsonObjectRequest(Request.Method.GET, routeUrl, null,
            { response ->
                val routesArray = response.getJSONArray("routes")
                if (routesArray.length() > 0) {
                    val route = routesArray.getJSONObject(0)
                    val geometry = route.getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")

                    val distanceMeters = route.getDouble("distance")
                    val distanceText = if (distanceMeters >= 1000) {
                        String.format("%.2f km", distanceMeters / 1000)
                    } else {
                        String.format("%.0f meters", distanceMeters)
                    }

                    Log.d("RouteInfo", "Distance: $distanceText")
                    distanceTextView.text = "Distance: $distanceText"

                    val routePoints = mutableListOf<GeoPoint>()
                    for (i in 0 until coordinates.length()) {
                        val point = coordinates.getJSONArray(i)
                        val lon = point.getDouble(0)
                        val lat = point.getDouble(1)
                        routePoints.add(GeoPoint(lat, lon))
                    }
                    drawPolyline(routePoints)
                }
            },
            { error ->
                Log.e("RouteError", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }




    private fun drawPolyline(points: List<GeoPoint>) {
        val polyline = Polyline().apply {
            setPoints(points)
            color = Color.BLUE
            width = 5f
        }
        drawnRoutes.add(polyline)
        mapViewStations?.overlays?.add(polyline)
        mapViewStations?.invalidate()
    }
    private fun clearRoutes() {
        drawnRoutes.forEach { mapViewStations?.overlays?.remove(it) }
        drawnRoutes.clear()
        mapViewStations?.invalidate()
    }



    private fun moveCameraToStation(lat: Double, lon: Double) {
        val geoPoint = GeoPoint(lat, lon)
        mapViewStations?.controller?.setCenter(geoPoint)
        mapViewStations?.controller?.setZoom(18.0) // Zoom in

        // Calculate distance from current location
        val userLocation = userMarker?.position
        if (userLocation != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLocation.latitude, userLocation.longitude, // Current location
                lat, lon, // Selected station
                results
            )

            val distanceInKm = results[0] / 1000 // Convert meters to kilometers
            distanceTextView.text = "Distance: %.2f km".format(distanceInKm)
            distanceTextView.visibility = View.VISIBLE
        }
    }


    @SuppressLint("MissingPermission")
    private fun refreshUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                updateUserLocation(location, firstUpdate = true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(true)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateUserLocation(location, firstUpdate = firstLocationUpdate)
                    if (firstLocationUpdate) firstLocationUpdate = false
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun updateUserLocation(location: Location, firstUpdate: Boolean) {
        val newGeoPoint = GeoPoint(location.latitude, location.longitude)
        userMarker?.position = newGeoPoint
        mapViewStations?.invalidate()

        if (firstUpdate) {
            mapViewStations?.controller?.setCenter(newGeoPoint)
        }

        // If a station is selected, update the route
        lastSelectedStation?.let {
            drawRoute(location.latitude, location.longitude, it.lat, it.lon)
        }
    }


    private fun addMarker(lat: Double, lon: Double, title: String) {
        val marker = Marker(mapViewStations).apply {
            position = GeoPoint(lat, lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title
        }
        mapViewStations?.overlays?.add(marker)
    }

    private fun addEcoOilStations() {
        ecoOilStations.forEach { addMarker(it.lat, it.lon, it.title) }
    }

    override fun onResume() {
        super.onResume()
        mapViewStations?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewStations?.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewStations?.onDetach()
    }
}

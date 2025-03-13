package com.example.ecobank2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class Stations : Fragment() {
    private var mapViewStations: MapView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var userMarker: Marker? = null
    private var firstLocationUpdate = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stations, container, false)
        mapViewStations = view.findViewById(R.id.mapViewStations)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val fabRefresh = view.findViewById<FloatingActionButton>(R.id.refresh)
        fabRefresh.setOnClickListener {
            refreshUserLocation()
        }

        setupMap()
        addEcoOilStations()
        requestLocationUpdates()

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
        }.addOnFailureListener {
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
            .setWaitForAccurateLocation(true)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateUserLocation(location,firstUpdate = firstLocationUpdate)
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
    }


    private fun addMarker(lat: Double, lon: Double, title: String) {
        val marker = Marker(mapViewStations)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        mapViewStations?.overlays?.add(marker)
    }

    private fun addEcoOilStations() {
        addMarker(15.986001550108089, 120.47914042108087, "EcoOil - Sta. Barbara")
        addMarker(15.970964, 120.217721, "EcoOil - Bugallon")
        addMarker(14.59950947, 121.0594116, "EcoOil - EDSA Mandaluyong")
        addMarker(14.58617262637261, 121.11467083383992, "EcoOil - Cainta")
        addMarker(14.5386317, 121.0116554, "EcoOil - EDSA Pasay")
        addMarker(12.33642488, 121.0760464, "EcoOil - PAG-ASA")
        addMarker(12.30936364, 121.1498375, "EcoOil - Magsaysay")
        addMarker(13.22510233, 120.5896923, "EcoOil - Mamburao")
        addMarker(12.31294857, 123.5589562, "EcoOil - Malinta")
        addMarker(12.44066833, 123.5140002, "EcoOil - Baleno")
        addMarker(12.03807066, 123.1952845, "EcoOil - Balud")
        addMarker(11.86879068, 123.90554, "EcoOil - Placer")
        addMarker(11.74553028, 124.0381113, "EcoOil - Esperanza")
        addMarker(12.42050864, 121.9891061, "EcoOil - Odiongan")
        addMarker(12.37057086, 122.6862472, "EcoOil - Sibuyan")
        addMarker(13.95529561, 120.8210623, "EcoOil - Calaca")
        addMarker(13.9003441, 121.059112, "EcoOil - Cuenca")
        addMarker(13.86321816, 121.1086799, "EcoOil - San Jose")
        addMarker(13.95060845, 121.1660282, "EcoOil - Lipa")
        addMarker(14.06159487, 121.1528962, "EcoOil - Darasa")
        addMarker(14.11914841, 121.1720174, "EcoOil - Padre Pio")
        addMarker(14.1008542498042, 122.014720556498, "EcoOil - Alabat")
        addMarker(14.0022271472917, 122.172499690095, "EcoOil - Quezon")
        addMarker(13.92514301, 122.5982487, "EcoOil - Del Gallego")
        addMarker(18.52348869, 120.7457271, "EcoOil - Bangui")
        addMarker(18.53227522, 120.6152504, "EcoOil - Burgos")
        addMarker(18.46343895, 120.5795799, "EcoOil - Pasuquin")
        addMarker(18.34852409, 120.6320222, "EcoOil - Sulbec")
        addMarker(18.26895031, 120.6063492, "EcoOil - Bacarra")
        addMarker(18.31445503, 120.7807852, "EcoOil - Vintar")
        addMarker(18.2057445, 120.5854389, "EcoOil - Sta. Joaquina")
        addMarker(18.20263221, 120.5942503, "EcoOil - Brgy. 8")
        addMarker(18.20263221, 120.5942503, "EcoOil - Buttong")
        addMarker(18.16231425, 120.5894281, "EcoOil - San Nicolas")
        addMarker(18.16334063, 120.7159117, "EcoOil - Piddig")
        addMarker(18.17129218, 120.6339919, "EcoOil - Sarrat")
        addMarker(18.05577389, 120.557265, "EcoOil - Batac")
        addMarker(17.97613772, 120.4800386, "EcoOil - Currimao")
        addMarker(17.91616684, 120.6623922, "EcoOil - Nueva Era")
        addMarker(17.41838745, 120.4782118, "EcoOil - Narvacan")
        addMarker(16.95811139, 120.4433675, "EcoOil - Tagudin")
        addMarker(17.58138409, 121.7645046, "EcoOil - Lal-lo")
        addMarker(18.28258215, 122.0112954, "EcoOil - Gonzaga")
        addMarker(10.65428351, 122.9388124, "EcoOil - Magsaysay Bacolod")
        addMarker(10.66140783, 122.974727, "EcoOil - Burgos Bacolod")
        addMarker(10.64235412, 122.9447184, "EcoOil - Alijis")
        addMarker(10.65498093, 122.9655669, "EcoOil - Taculing")
        addMarker(10.67499999, 122.9482515, "EcoOil - Brgy. 10")
        addMarker(10.66269423, 122.9670724, "EcoOil - Circumferential East")
        addMarker(10.6736506, 122.9719817, "EcoOil - Montevista")
        addMarker(9.335455681, 123.2926836, "EcoOil - Sibulan")
        addMarker(9.336681674, 122.8608281, "EcoOil - Sta Catalina 1")
        addMarker(9.33629568, 122.8606044, "EcoOil - Sta Catalina 2")
        addMarker(9.310723006, 123.2811395, "EcoOil - Palinpinon")
        addMarker(9.46869631, 123.1858163, "EcoOil - Bio-os")
        addMarker(9.507179089, 122.8101488, "EcoOil - Kalumboyan")
        addMarker(9.510125339, 122.8101031, "EcoOil - Malabugas")
        addMarker(9.626445716, 123.8957085, "EcoOil - Baclayon")
        addMarker(9.783034772, 124.1486824, "EcoOil - Batuan")
        addMarker(9.636568905, 123.8461644, "EcoOil - Totolan")
        addMarker(9.854037572, 124.3465926, "EcoOil - Pilar")
        addMarker(10.76457141, 122.4933942, "EcoOil - San Miguel")
        addMarker(10.10167971, 124.3170258, "EcoOil - San Agustin")
        addMarker(10.1557328, 124.2349812, "EcoOil - Bagacay")
        addMarker(6.9041126, 125.3215117, "EcoOil - Digos")
        addMarker(7.4578376, 125.7947532, "EcoOil - Tagum")
        addMarker(7.0757713, 125.5707263, "EcoOil - Matina")
        addMarker(6.069639, 125.1362246, "EcoOil - Gensan")
        addMarker(9.378333, 118.501667, "EcoOil - Plaridel")
        addMarker(9.972000, 122.624000, "EcoOil - Cauayan")
        addMarker(18.15765, 120.55333, "EcoOil - Balatong")



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

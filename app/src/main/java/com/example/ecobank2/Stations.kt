package com.example.ecobank2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class Stations : Fragment() {

    private lateinit var mapViewStations: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_stations, container, false)
        mapViewStations = view.findViewById(R.id.mapViewStations)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupMap()
        requestLocationPermission()

        return view
    }

    private fun setupMap() {
        mapViewStations.setTileSource(TileSourceFactory.MAPNIK)
        mapViewStations.setMultiTouchControls(true)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLocation = GeoPoint(location.latitude, location.longitude)
                mapViewStations.controller.setZoom(15.0)
                mapViewStations.controller.setCenter(userLocation)

                addMarker(location.latitude, location.longitude, "You are here")

                addMarker(15.977905, 120.481728, "Eco oil")
                addMarker(16.000000, 120.500000, "Another Station")
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(lat: Double, lon: Double, title: String) {
        val marker = Marker(mapViewStations)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        mapViewStations.overlays.add(marker)
        mapViewStations.invalidate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewStations.onDetach()
    }
}

package com.example.ecobank2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

import org.osmdroid.views.overlay.Marker

class Map : Fragment() {

    private lateinit var mapView: org.osmdroid.views.MapView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        setupMap()
        return view
    }

    private fun setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(12.0)
        val startPoint = GeoPoint(37.7749, -122.4194)
        mapController.setCenter(startPoint)

        val marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "San Francisco"
        mapView.overlays.add(marker)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }
}

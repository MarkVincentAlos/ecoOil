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
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class Stations : Fragment() {
    private var mapViewStations: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stations, container, false)
        mapViewStations = view.findViewById(R.id.mapViewStations)

        setupMap()
        addEcoOilStations()

        return view
    }

    private fun setupMap() {
        mapViewStations?.setTileSource(TileSourceFactory.MAPNIK)
        mapViewStations?.setMultiTouchControls(true)

        val mapController = mapViewStations?.controller
        val startPoint = GeoPoint(12.8797, 121.7740)
        mapController?.setZoom(8.0)
        mapController?.setCenter(startPoint)
    }

    private fun addMarker(lat: Double, lon: Double, title: String) {
        if (mapViewStations != null) {
            val marker = Marker(mapViewStations)
            marker.position = GeoPoint(lat, lon)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = title
            mapViewStations!!.overlays.add(marker)
            mapViewStations!!.invalidate()
        }
    }

    private fun addEcoOilStations() {
        addMarker(15.986001550108089, 120.47914042108087, "EcoOil - Sta. Barbara")
        addMarker(15.970964, 120.217721, "EcoOil - Bugallon")
        addMarker(14.59950947,121.0594116, "EcoOil - EDSA Mandaluyong")
        addMarker(14.58617262637261,121.11467083383992,"EcoOil - Cainta")
        addMarker(14.5386317,121.0116554,"EcoOil - EDSA Pasay ")
        addMarker(12.33642488,121.0760464,"EcoOil - PAG-ASA ")
        addMarker(12.30936364,121.1498375,"EcoOil - Magsaysay ")
        addMarker(13.22510233,120.5896923,"EcoOil - Mamburao ")
        addMarker(12.31294857,123.5589562,"EcoOil - Malinta")
        addMarker(12.44066833,123.5140002,"EcoOil - Baleno ")
        addMarker(12.03807066,123.1952845,"EcoOil - Balud ")
        addMarker(11.86879068,123.90554,"EcoOil - Placer ")
        addMarker(11.74553028,124.0381113,"EcoOil - Esperanza ")
        addMarker(12.42050864,121.9891061,"EcoOil - Odiongan ")
        addMarker(12.37057086,122.6862472,"EcoOil - Sibuyan ")
        addMarker(13.95529561,120.8210623,"EcoOil - Calaca ")
        addMarker(13.9003441,121.059112,"EcoOil - Cuenca ")
        addMarker(13.86321816,121.1086799,"EcoOil - San Jose ")
        addMarker(13.95060845,121.1660282,"EcoOil - Lipa ")
        addMarker(14.06159487,121.1528962,"EcoOil - Darasa ")
        addMarker(14.11914841,121.1720174,"EcoOil - Padre Pio ")
        addMarker(14.1008542498042,122.014720556498,"EcoOil - Alabat")
        addMarker(14.0022271472917,122.172499690095,"EcoOil - Quezon")
        addMarker(13.92514301,122.5982487,"EcoOil - Del Gallego")
        addMarker(18.52348869,120.7457271,"EcoOil - Bangui")
        addMarker(18.53227522,120.6152504,"EcoOil - Burgos")
        addMarker(18.46343895,120.5795799,"EcoOil - Pasuquin")
        addMarker(18.34852409,120.6320222,"EcoOil - Sulbec")
        addMarker(18.26895031,120.6063492,"EcoOil - Bacarra")
        addMarker(18.31445503,120.7807852,"EcoOil - Vintar")
        addMarker(18.2057445,120.5854389,"EcoOil - Sta. Joaquina")
        addMarker(18.20263221,120.5942503,"EcoOil - Brgy. 8")
        addMarker(18.20263221,120.5942503,"EcoOil - Buttong")
        addMarker(18.16231425,120.5894281,"EcoOil - San Nicolas")
        addMarker(18.16334063,120.7159117,"EcoOil - Piddig")
        addMarker(18.17129218,120.6339919,"EcoOil - Sarrat")
        addMarker(18.05577389,120.557265,"EcoOil - Batac")
        addMarker(17.97613772,120.4800386,"EcoOil - Currimao")
        addMarker(17.91616684,120.6623922,"EcoOil - Nueva Era")
        addMarker(17.41838745,120.4782118,"EcoOil - Narvacan")
        addMarker(16.95811139,120.4433675,"EcoOil - Tagudin")
        addMarker(17.58138409,121.7645046,"EcoOil - Lal-lo")
        addMarker(18.28258215,122.0112954,"EcoOil - Gonzaga")
        //addMarker(,"EcoOil - Magsaysay Bacolod")
        //addMarker(,"EcoOil - Burgos Bacolod")
        //addMarker(,"EcoOil - Alijis")
        //addMarker(,"EcoOil - Taculing")
        //addMarker(,"EcoOil - Brgy. 10")
        //addMarker(,"EcoOil - Circumferential East")
        //addMarker(,"EcoOil - Montevista")
        //addMarker(,"EcoOil - Bago")
        //addMarker(,"EcoOil - Isabela")
        //addMarker(,"EcoOil - Sibulan")
        //addMarker(,"EcoOil - Sta Catalina 1")
        //addMarker(,"EcoOil - Sta Catalina 2")
        //addMarker(,"EcoOil - Palinpinon")
        //addMarker(,"EcoOil - Bio-os")
        //addMarker(,"EcoOil - Kalumboyan")
        //addMarker(,"EcoOil - Malabugas")
        //addMarker(,"EcoOil - Baclayon")
        //addMarker(,"EcoOil - Batuan")
        //addMarker(,"EcoOil - Totolan")
        //addMarker(,"EcoOil - Pilar")
        //addMarker(,"EcoOil - San Miguel")
        //addMarker(,"EcoOil - San Agustin")
        //addMarker(,"EcoOil - Bagacay")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")
        //addMarker(, "EcoOil - ")


    }

    override fun onResume() {
        super.onResume()
        mapViewStations?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewStations?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewStations?.onDetach()
    }
}

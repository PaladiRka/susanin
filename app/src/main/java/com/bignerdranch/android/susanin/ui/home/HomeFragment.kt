package com.bignerdranch.android.susanin.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bignerdranch.android.susanin.BuildConfig
import com.bignerdranch.android.susanin.R
import com.bignerdranch.android.susanin.SusaninLocationManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


//class HomeFragment : Fragment() {
//
//    private lateinit var homeViewModel: HomeViewModel
//
//    private val handler = Handler()
//
//    var locationManager: SusaninLocationManager? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//
//        locationManager = context?.let { SusaninLocationManager(it, textView) }
//
//        val fab: FloatingActionButton = root.findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            locationManager?.updateLocation()
//
//        }
//
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                locationManager?.updateLocation()
//
//                handler.postDelayed(this, 100)
//            }
//        }, 100)
//
//        val mapView: MapView = root.findViewById(R.id.map_view) as MapView
//        mapView.setBuiltInZoomControls(true)
//        mapView.setMultiTouchControls(true)
//
//        val mapController: IMapController = mapView.controller
//        mapController.setZoom(2)
//
//        return root
//    }
//}
//


class HomeFragment : Fragment() {

    private lateinit var locationManager: SusaninLocationManager
    private lateinit var map: MapView
    private lateinit var myLocationMarker: Marker
    private var geoPoint: GeoPoint? = null

    private val lastTouchDown = PointF()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val ctx = requireActivity().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID;
        map = view.findViewById(R.id.map_view)
        map.setUseDataConnection(true)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController: IMapController = map.controller

        mapController.zoomTo(14, 1)

        val mGpsMyLocationProvider = GpsMyLocationProvider(activity)
        val locationOverlay = MyLocationNewOverlay(mGpsMyLocationProvider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        val icon = getBitmap(R.drawable.ic_baseline_navigation_24)
        locationOverlay.setPersonIcon(icon)
        locationOverlay.setDirectionArrow(icon, icon)
        locationOverlay.setPersonHotspot(40f, 40f)
        map.overlays.add(locationOverlay)

        myLocationMarker = Marker(map)

        locationOverlay.runOnFirstFix {
            val myLocation: GeoPoint? = locationOverlay.myLocation
            if (myLocation != null) {
                requireActivity().runOnUiThread { map.controller.animateTo(myLocation) }
            }
        }

        locationManager = SusaninLocationManager(requireContext())

        locationManager.updateLocation()
        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener { it ->
            locationManager.updateLocation()
            updateLoc()

        }

        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                return true
            }

            override fun longPressHelper(point: GeoPoint): Boolean {
                Log.e(
                    "TAG",
                    "Long press " +
                            "latitude ${point.latitude} " +
                            "longitude ${point.longitude}"
                )

                val startMarker = Marker(map)
                startMarker.position = point
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(startMarker)

                geoPoint = point
                return false
            }
        }
        val overlayEvents = MapEventsOverlay(requireContext(), mReceive)
        map.overlays.add(overlayEvents)
        return view
    }

    private fun getBitmap(@DrawableRes resId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(requireActivity(), resId);
        val canvas = Canvas()
        val bitmap: Bitmap = drawable?.let {
            Bitmap.createBitmap(
                it.intrinsicWidth,
                it.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        } ?: return null
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    private fun updateLoc() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location =
            locationManager.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        try {
            val locGeoPoint = GeoPoint(location.latitude, location.longitude)
            map.controller.setCenter(locGeoPoint)
            myLocationMarker.position = locGeoPoint

        } catch (e: NullPointerException) {
            Toast.makeText(
                requireContext(),
                "Не удалось загрузить местоположение, проверьте соединение",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

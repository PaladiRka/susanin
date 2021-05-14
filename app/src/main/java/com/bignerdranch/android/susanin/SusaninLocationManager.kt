package com.bignerdranch.android.susanin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*


class SusaninLocationManager(private val context: Context) {
    companion object {
        const val TAG = "SusaninLocMan"
    }
    val locationManager = context.getSystemService(LocationManager::class.java)

    private val locationListener: LocationListener = MyLocationListener(context)

    fun updateLocation(){

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 500, 10f, locationListener)
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 500, 10f, locationListener)
    }

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener(val context: Context) : LocationListener {
        override fun onLocationChanged(loc: Location) {

            val longitude = "Longitude: " + loc.longitude
            Log.v(TAG, longitude)
            val latitude = "Latitude: " + loc.latitude
            Log.v(TAG, latitude)

            /*------- To get city name from coordinates -------- */
            var cityName: String? = null
            val gcd = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>
            try {
                addresses = gcd.getFromLocation(
                    loc.latitude,
                    loc.longitude, 1
                )
                if (addresses.isNotEmpty()) {
                    println(addresses[0].locality)
                    cityName = addresses[0].locality
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

}
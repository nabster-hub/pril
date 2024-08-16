package com.example.tateknew.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class LocationHandler(private val context: Context) {

    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        // Проверяем доступность провайдеров
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

        if (!isGpsEnabled && !isNetworkEnabled) {
            Log.d("LocationHandler", "No location provider enabled")
            onLocationReceived(null)
            return
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                currentLocation = location
                onLocationReceived(location)
                locationManager?.removeUpdates(this) // Останавливаем обновления после получения данных
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (isGpsEnabled) {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } else if (isNetworkEnabled) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        }

        // Пытаемся получить последнее известное местоположение
        val lastKnownLocation = if (isGpsEnabled) {
            locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else {
            locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        lastKnownLocation?.let {
            currentLocation = it
            onLocationReceived(it)
        }
    }

    // Метод для остановки обновления местоположения, если необходимо
    fun stopLocationUpdates() {
        locationManager?.removeUpdates { /* no-op */ }
    }


}

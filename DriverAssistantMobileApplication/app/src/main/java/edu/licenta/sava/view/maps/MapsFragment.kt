package edu.licenta.sava.view.maps

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import edu.licenta.sava.R
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.view.activity.DrivingSessionDetailedMapActivity

class MapsFragment : Fragment() {

    private lateinit var drivingSession: DrivingSession

    private val callback = OnMapReadyCallback { googleMap ->

        val warningEventsList = drivingSession.warningEventsList
        val sensorDataList = drivingSession.sensorDataList

        if (sensorDataList.isNotEmpty()) {
            val start = LatLng(
                sensorDataList.first().latitude,
                sensorDataList.first().longitude
            )
            val stop = LatLng(
                sensorDataList.last().latitude,
                sensorDataList.last().longitude
            )

            val latLngList = ArrayList<LatLng>()
            latLngList.add(start)
            googleMap.addMarker(
                MarkerOptions()
                    .position(start)
                    .title("START")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )?.tag = 0

            for (warningEvent in warningEventsList) {
                val latitude = warningEvent.sensorData.latitude
                val longitude = warningEvent.sensorData.longitude
                val message = "Speed: " + warningEvent.sensorData.speed.toString() +
                        "KM/H, Limit: " + warningEvent.sensorData.speedLimit + "KM/H"
                val latLng = LatLng(latitude, longitude)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(message)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                )?.tag = 1
            }

            for (i in 1 until sensorDataList.size) {
                if (!(sensorDataList[i].speed < 5 && sensorDataList[i - 1].speed < 5)) {
                    val latLng = LatLng(sensorDataList[i].latitude, sensorDataList[i].longitude)
                    latLngList.add(latLng)
                }
            }

            latLngList.add(stop)
            googleMap.addMarker(
                MarkerOptions()
                    .position(stop)
                    .title("STOP")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )?.tag = 2

            googleMap.addPolyline(
                PolylineOptions()
                    .color(Color.DKGRAY)
                    .clickable(true)
                    .addAll(latLngList)
                    .width(10F)
            ).tag = 3

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(start))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drivingSessionActivity = activity as DrivingSessionDetailedMapActivity
        drivingSession = drivingSessionActivity.getCurrentDrivingSession()
        Log.d("Fragment:", "I acquired the following driving session $drivingSession")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}
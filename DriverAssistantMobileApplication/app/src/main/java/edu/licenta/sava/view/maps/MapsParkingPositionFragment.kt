package edu.licenta.sava.view.maps

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
import edu.licenta.sava.R
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.view.activity.ParkingPositionActivity

class MapsParkingPositionFragment : Fragment() {

    private lateinit var drivingSession: DrivingSession

    private val callback = OnMapReadyCallback { googleMap ->

        val lastLocation = LatLng(
            drivingSession.sensorDataList.last().latitude,
            drivingSession.sensorDataList.last().longitude
        )

        googleMap.addMarker(
            MarkerOptions()
                .position(lastLocation)
                .title("PARKING POSITION")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        )?.tag = 0

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f))
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

        val parkingPositionActivity = activity as ParkingPositionActivity
        drivingSession = parkingPositionActivity.getCurrentDrivingSession()
        Log.d("Fragment:", "I acquired the following driving session $drivingSession")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}
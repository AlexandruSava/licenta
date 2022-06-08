package edu.licenta.sava.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionBinding
import edu.licenta.sava.model.Notification
import edu.licenta.sava.model.SensorData
import edu.licenta.sava.service.DrivingSessionService
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.random.Random

class DrivingSessionActivity : AppCompatActivity() {
    private val database =
        Firebase
            .database("https://licenta-driver-assistant-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("driving_sessions")

    private val drivingSessionService = DrivingSessionService()

    private lateinit var binding: ActivityDrivingSessionBinding

    private lateinit var userId: String
    private lateinit var email: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var i = 10
    private var speed = 0
    private var speedLimit = 30
    private var temperature = 0
    private var lastScore = 100f
    private lateinit var currentLocation: Location

    private var sessionStarted = false

    private lateinit var sensorData: SensorData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()
        initializeButtons()
        initializeLocation()
        startDrivingSession(userId, email)
    }

    private fun setBinding() {
        binding = ActivityDrivingSessionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setUserAndEmail() {
        val userIdString = intent.getStringExtra("userId")
        val emailString = intent.getStringExtra("email")
        if (!userIdString.isNullOrEmpty() && !emailString.isNullOrEmpty()) {
            userId = userIdString
            email = emailString
        }
    }

    private fun initializeButtons() {
        val longMessage = "Do you want to end this session?"
        val positiveText = "End Session"
        val negativeText = "Cancel"

        binding.finishBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setMessage(longMessage)
                .setPositiveButton(positiveText) { _, _ -> finishAction() }
                .setNegativeButton(negativeText, null)
                .show()
        }
    }

    private fun finishAction() {
        sessionStarted = false

        Log.d("SESSION", "SESSION HAS STOPPED")

        stopLocationUpdates()

        val endTime = System.currentTimeMillis()

        drivingSessionService.stopDrivingSession(endTime)

        val drivingSession = drivingSessionService.getDrivingSession()

        DatabaseController()
            .writeDrivingSessionsDataInLocalStorage(
                this,
                database,
                userId = userId,
                drivingSession = drivingSession
            )

        val intent = Intent(this, DrivingSessionDetailedActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        intent.putExtra("endTime", endTime)
        intent.putExtra("version", 1)
        startActivity(intent)
        finish()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )
    }

    private fun initializeLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            @SuppressLint("SimpleDateFormat")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Get current sensor data
                currentLocation = locationResult.lastLocation
                val latitude = currentLocation.latitude
                val longitude = currentLocation.longitude
                speed = ((currentLocation.speed) * 3.6).toInt()
                temperature = Random.nextInt(30)
                val speedLimit = getSpeedLimit(latitude, longitude)

                // Create and add to the list the current SensorData
                sensorData = SensorData(speed, temperature, latitude, longitude, speedLimit)
                drivingSessionService.addSensorData(sensorData)

                // Process data
                val score: Float = drivingSessionService.analyzeDrivingSession()

                val simpleDateFormat = SimpleDateFormat("mm:ss")
                val durationFormatted: String =
                    simpleDateFormat.format(drivingSessionService.getDuration())

                val decimalFormat = DecimalFormat("#.##")
                decimalFormat.roundingMode = RoundingMode.DOWN
                val averageSpeed = decimalFormat.format(drivingSessionService.getAverageSpeed())

                // Show data to user
                lastScore = score
                binding.score.text = lastScore.toInt().toString()
                binding.speed.text = speed.toString()
                binding.averageSpeed.text = averageSpeed.toString()
                binding.timeElapsed.text = durationFormatted

                setScoreTextViewColor(score.toInt())
                setWarningEventTextViews()
            }

            private fun getSpeedLimit(latitude: Double, longitude: Double): Int {
                val speedLimits = arrayOf(30, 40, 50, 60, 60)

                if (i % 5 == 0) {
                    val random = Random.nextInt(5)
                    speedLimit = speedLimits.elementAt(random)
                }

                i++
                Log.d("Location:", "Current position is $latitude, $longitude")

                return speedLimit
            }

            private fun setScoreTextViewColor(score: Int) {
                when (score) {
                    in 85..100 -> binding.score.setTextColor(Color.parseColor("#FF4BC100"))
                    in 75..84 -> binding.score.setTextColor(Color.parseColor("#FF64DD17"))
                    in 60..74 -> binding.score.setTextColor(Color.parseColor("#FFE1BC00"))
                    in 50..59 -> binding.score.setTextColor(Color.parseColor("#FFE14F00"))
                    in 0..49 -> binding.score.setTextColor(Color.parseColor("#E10000"))
                }
            }

            private fun setWarningEventTextViews() {
                val warningEvent = drivingSessionService.getLastWarningEvent()
                var message = "Error"
                when (warningEvent.type) {
                    Notification.GOOD_DRIVING.name.lowercase() -> {
                        message = Notification.GOOD_DRIVING.message
                    }
                    Notification.SPEEDING.name.lowercase() -> {
                        message = Notification.SPEEDING.message
                    }
                }
                binding.warning.text = message
            }
        }
    }

    private fun startDrivingSession(userId: String, email: String) {
        sessionStarted = true

        Log.d("SESSION", "SESSION HAS STARTED")

        listenLocationUpdates()

        drivingSessionService.startDrivingSession(userId, email)
    }

    private fun listenLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}
package edu.licenta.sava.view.activity

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.controller.FirebaseController
import edu.licenta.sava.databinding.ActivityDashboardBinding
import edu.licenta.sava.service.DashboardService
import java.math.RoundingMode
import java.text.DecimalFormat

open class DashboardActivity : DrawerLayoutActivity() {

    private val screenId: Int = 1

    private val dashboardService = DashboardService()
    private val databaseController = DatabaseController()
    private val firebaseController = FirebaseController()

    private lateinit var binding: ActivityDashboardBinding

    private val database =
        Firebase
            .database("https://licenta-driver-assistant-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("driving_sessions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        getDataFromFirebase()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        initializeButtons()
        requestLocationPermissions()
    }

    private fun setBinding() {
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getDataFromFirebase() {
        val reference = database.child(userId)
        val context = this

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseController.getFirebaseDataAndWriteDrivingSessionsDataInLocalStorage(
                    snapshot,
                    userId,
                    context
                )
                getStorageData()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database Error!")
            }
        })
    }

    private fun getStorageData() {
        val initialized = databaseController.verifyPresenceOfALocalFile(this, userId)
        if (initialized) {
            val drivingSessionsList =
                databaseController.getDrivingSessionsDataFromLocalStorage(this, userId)
            if (drivingSessionsList.isNotEmpty()) {
                val decimalFormat = DecimalFormat("#.##")
                decimalFormat.roundingMode = RoundingMode.DOWN
                var distance = decimalFormat.format(
                    dashboardService.calculateTotalDistance(drivingSessionsList)
                )
                distance += " km"

                var calculateAverageSpeed =
                    dashboardService.calculateAverageSpeed(drivingSessionsList).toString()
                calculateAverageSpeed += " km/h"

                val userScore = dashboardService.calculateUserScore(drivingSessionsList)

                // Display data to user
                binding.score.text = userScore.toString()
                binding.averageSpeed.text = calculateAverageSpeed
                binding.distance.text = distance

                if (drivingSessionsList.size >= 10) {
                    binding.improvement.text =
                        dashboardService.calculateImprovement(drivingSessionsList)
                } else {
                    binding.improvement.text = "-"
                }

                // Set proper color to score
                setScoreTextViewColor(userScore, binding.score)
            }
        }
    }

    private fun initializeButtons() {
        binding.startSessionBtn.setOnClickListener {
            startSessionAction()
        }
    }

    private fun requestLocationPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
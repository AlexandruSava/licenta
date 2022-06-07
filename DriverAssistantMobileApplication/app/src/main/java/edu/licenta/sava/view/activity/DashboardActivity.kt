package edu.licenta.sava.view.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.R
import edu.licenta.sava.service.DashboardService
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.controller.FirebaseController
import edu.licenta.sava.databinding.ActivityDashboardBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class DashboardActivity : AppCompatActivity() {

    private val dashboardController = DashboardService()
    private val databaseController = DatabaseController()
    private val firebaseController = FirebaseController()

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawer: DrawerLayout

    private lateinit var userId: String
    private lateinit var email: String

    private val database =
        Firebase
            .database("https://licenta-driver-assistant-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("driving_sessions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()
        getDataFromFirebase()
        initializeToolbarAndMenu()
        initializeButtons()
        requestLocationPermissions()
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
                    dashboardController.calculateTotalDistance(drivingSessionsList)
                )
                distance += " km"

                var calculateAverageSpeed =
                    dashboardController.calculateAverageSpeed(drivingSessionsList).toString()
                calculateAverageSpeed += " km/h"

                val userScore = dashboardController.calculateUserScore(drivingSessionsList)

                // Display data to user
                binding.score.text = userScore.toString()
                binding.averageSpeed.text = calculateAverageSpeed
                binding.distance.text = distance

                if (drivingSessionsList.size >= 10) {
                    binding.improvement.text =
                        dashboardController.calculateImprovement(drivingSessionsList)
                } else {
                    binding.improvement.text = "-"
                }

                // Set proper color to score
                setScoreTextViewColor(userScore)

            }
        }
    }

    private fun requestLocationPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Toast.makeText(applicationContext, "Access fine", Toast.LENGTH_LONG).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Toast.makeText(applicationContext, "Access coarse", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Toast.makeText(applicationContext, "Access denied", Toast.LENGTH_LONG).show()
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun initializeButtons() {
        binding.startSessionBtn.setOnClickListener {
            startSessionAction()
        }
    }

    private fun initializeToolbarAndMenu() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawer = binding.drawer
        drawer.addDrawerListener(
            ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.show_navigation,
                R.string.hide_navigation
            )
        )

        val navigation = binding.navigationView
        navigation.setNavigationItemSelectedListener { menuItem ->
            onOptionsItemSelected(menuItem)
            navigation.setCheckedItem(menuItem)
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        binding.navigationView
            .getHeaderView(0)
            .findViewById<TextView>(R.id.email_menu)
            .text = email

        navigation.setCheckedItem(R.id.dashboard_item)
    }

    private fun setBinding() {
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun logoutAction() {
        val intent = Intent(this, LoginActivity::class.java)
        Firebase.auth.signOut()
        startActivity(intent)
        finish()
    }

    private fun historyAction() {
        val intent = Intent(this, DrivingSessionsHistoryActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    private fun startSessionAction() {
        val intent = Intent(this, DrivingSessionActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    private fun learningAction() {
        val intent = Intent(this, LearningActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    private fun parkingAction() {
        val intent = Intent(this, ParkingPositionActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_item) {
            logoutAction()
            return true
        }
        if (item.itemId == R.id.history_item) {
            historyAction()
            return true
        }
        if (item.itemId == R.id.start_session_item) {
            startSessionAction()
            return true
        }
        if (item.itemId == R.id.learning_item) {
            learningAction()
            return true
        }
        if (item.itemId == R.id.parking_item) {
            parkingAction()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUserAndEmail() {
        val userIdString = intent.getStringExtra("userId")
        val emailString = intent.getStringExtra("email")
        if (!userIdString.isNullOrEmpty() && !emailString.isNullOrEmpty()) {
            userId = userIdString
            email = emailString
        }
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
}
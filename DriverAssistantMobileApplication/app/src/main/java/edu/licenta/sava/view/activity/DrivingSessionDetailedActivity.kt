package edu.licenta.sava.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.R
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionDetailedBinding
import edu.licenta.sava.model.DrivingSession
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DrivingSessionDetailedActivity : AppCompatActivity() {

    private val databaseController = DatabaseController()

    private lateinit var binding: ActivityDrivingSessionDetailedBinding
    private lateinit var drawer: DrawerLayout

    private lateinit var userId: String
    private lateinit var email: String
    private var endTime: Long = 0L

    private lateinit var currentDrivingSession: DrivingSession

    private var version: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()
        initializeButtons()
        initializeToolbarAndMenu()
        getStorageDataAndSetListDataAndTextViews()
    }

    private fun initializeButtons() {
        binding.backBtn.setOnClickListener {
            historyAction()
        }

        binding.seeDetailedMapBtn.setOnClickListener {
            seeDetailedMapAction()
        }
    }

    private fun setBinding() {
        binding = ActivityDrivingSessionDetailedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setUserAndEmail() {
        val userIdString = intent.getStringExtra("userId")
        val emailString = intent.getStringExtra("email")
        val endTimeLong = intent.getLongExtra("endTime", 0L)
        val versionInt = intent.getIntExtra("version", 0)
        if (!userIdString.isNullOrEmpty() && !emailString.isNullOrEmpty()) {
            userId = userIdString
            email = emailString
            endTime = endTimeLong
            version = versionInt
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getStorageDataAndSetListDataAndTextViews() {
        val initialized = databaseController.verifyPresenceOfALocalFile(this, userId)
        if (initialized) {
            currentDrivingSession =
                databaseController
                    .getDrivingSessionBySessionEndTime(
                        this,
                        userId,
                        endTime
                    )

            // Formatters
            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode = RoundingMode.DOWN

            val formatterDate = SimpleDateFormat("dd MMMM yyyy HH:mm")

            val formatterDuration = SimpleDateFormat("HH:mm")
            formatterDuration.timeZone = TimeZone.getTimeZone("GMT")

            // Get data to display
            val averageSpeed = decimalFormat.format(currentDrivingSession.averageSpeed) + " km/h"
            val date = formatterDate.format(currentDrivingSession.endTime)
            val duration = formatterDuration.format(currentDrivingSession.duration)
            val distance = decimalFormat.format(currentDrivingSession.distanceTraveled) + " km"
            val score = decimalFormat.format(currentDrivingSession.finalScore)

            // Display data to UI
            binding.averageSpeed.text = averageSpeed
            binding.dateEnd.text = date
            binding.timeElapsed.text = duration
            binding.distance.text = distance
            binding.score.text = score

            // Color the score
            setScoreTextViewColor(currentDrivingSession.finalScore.toInt(), binding.score)
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

        navigation.setCheckedItem(R.id.history_item)
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
        if (item.itemId == R.id.dashboard_item) {
            dashboardAction()
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

    private fun parkingAction() {
        val intent = Intent(this, ParkingPositionActivity::class.java)
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

    private fun logoutAction() {
        val intent = Intent(this, LoginActivity::class.java)
        Firebase.auth.signOut()
        startActivity(intent)
        finish()
    }

    private fun historyAction() {
        // If user is redirected here by a finished driving session
        if (version == 1) {
            val intent = Intent(this, DrivingSessionsHistoryActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("email", email)
            startActivity(intent)
        } else {
            onBackPressed()
        }
        finish()
    }

    private fun dashboardAction() {
        val intent = Intent(this, DashboardActivity::class.java)
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

    private fun seeDetailedMapAction() {
        val intent = Intent(this, DrivingSessionDetailedMapActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        intent.putExtra("endTime", endTime)
        startActivity(intent)
    }

    private fun setScoreTextViewColor(score: Int, textView: TextView) {
        when (score) {
            in 85..100 -> textView.setTextColor(Color.parseColor("#FF4BC100"))
            in 75..84 -> textView.setTextColor(Color.parseColor("#FF64DD17"))
            in 60..74 -> textView.setTextColor(Color.parseColor("#FFE1BC00"))
            in 50..59 -> textView.setTextColor(Color.parseColor("#FFE14F00"))
            in 0..49 -> textView.setTextColor(Color.parseColor("#E10000"))
        }
    }
}
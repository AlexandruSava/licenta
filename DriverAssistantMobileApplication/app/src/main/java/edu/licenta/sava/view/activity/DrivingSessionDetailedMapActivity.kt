package edu.licenta.sava.view.activity

import android.content.Intent
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
import edu.licenta.sava.database.DatabaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionDetailedMapBinding
import edu.licenta.sava.model.DrivingSession

class DrivingSessionDetailedMapActivity : AppCompatActivity() {

    private val databaseController = DatabaseController()

    private lateinit var binding: ActivityDrivingSessionDetailedMapBinding
    private lateinit var drawer: DrawerLayout

    private lateinit var userId: String
    private lateinit var email: String
    private var endTime: Long = 0L

    private lateinit var currentDrivingSession: DrivingSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()
        initializeToolbarAndMenu()
        initializeButtons()
        getStorageData()
    }

    private fun initializeButtons() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun setBinding() {
        binding = ActivityDrivingSessionDetailedMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setUserAndEmail() {
        val userIdString = intent.getStringExtra("userId")
        val emailString = intent.getStringExtra("email")
        val endTimeLong = intent.getLongExtra("endTime", 0L)
        if (!userIdString.isNullOrEmpty() && !emailString.isNullOrEmpty()) {
            userId = userIdString
            email = emailString
            endTime = endTimeLong
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

    private fun learningAction() {
        val intent = Intent(this, LearningActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    private fun getStorageData() {
        val initialized = databaseController.verifyPresenceOfALocalFile(this, userId)
        if (initialized) {
            currentDrivingSession =
                databaseController.getDrivingSessionBySessionEndTime(
                    this,
                    userId,
                    endTime
                )
        }
    }

    fun getCurrentDrivingSession(): DrivingSession {
        return currentDrivingSession
    }
}
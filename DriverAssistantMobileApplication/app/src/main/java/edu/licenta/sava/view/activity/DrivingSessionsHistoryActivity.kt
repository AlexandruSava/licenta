package edu.licenta.sava.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.R
import edu.licenta.sava.database.DatabaseController
import edu.licenta.sava.database.FirebaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionsHistoryBinding
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.view.adapter.DrivingSessionsHistoryAdapter

class DrivingSessionsHistoryActivity : AppCompatActivity() {

    private val databaseController = DatabaseController()
    private val firebaseController = FirebaseController()

    private lateinit var binding: ActivityDrivingSessionsHistoryBinding
    private lateinit var listAdapterDrivingSessions: DrivingSessionsHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawer: DrawerLayout

    private lateinit var userId: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()
        initializeToolbarAndMenu()

        getStorageDataAndInitList()
    }

    private fun getStorageDataAndInitList() {
        val initialized = databaseController.verifyPresenceOfALocalFile(this, userId)
        if (initialized) {
            val drivingSessionsList =
                databaseController.getDrivingSessionsDataFromLocalStorage(this, userId)
            drivingSessionsList.sortWith(compareBy { it.endTime })
            initList(drivingSessionsList)
        }
    }

    private fun initList(drivingSessionsList: ArrayList<DrivingSession>) {
        val model: MutableList<DrivingSession> = drivingSessionsList.reversed().toMutableList()

        listAdapterDrivingSessions =
            DrivingSessionsHistoryAdapter(
                model,
                this::moreDetailsAction,
                this::deleteDrivingSession
            )

        recyclerView = binding.historyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = listAdapterDrivingSessions
    }

    private fun deleteDrivingSession(drivingSession: DrivingSession) {
        firebaseController.deleteDrivingSession(drivingSession)
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

    private fun setBinding() {
        binding = ActivityDrivingSessionsHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun moreDetailsAction(drivingSession: DrivingSession) {
        val intent = Intent(this, DrivingSessionDetailedActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        intent.putExtra("endTime", drivingSession.endTime)
        intent.putExtra("version", 0)
        startActivity(intent)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_item) {
            logoutAction()
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

    private fun setUserAndEmail() {
        val userIdString = intent.getStringExtra("userId")
        val emailString = intent.getStringExtra("email")
        if (!userIdString.isNullOrEmpty() && !emailString.isNullOrEmpty()) {
            userId = userIdString
            email = emailString
        }
    }
}
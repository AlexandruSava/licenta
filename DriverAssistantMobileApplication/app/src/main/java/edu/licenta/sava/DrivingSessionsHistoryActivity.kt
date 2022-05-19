package edu.licenta.sava

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
import edu.licenta.sava.databinding.ActivityDrivingSessionsHistoryBinding

class DrivingSessionsHistoryActivity : AppCompatActivity() {

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

        initList()
    }

    private fun initList() {
        val sensorData = SensorData(12,12,12.2, 12.2, 12)
        val warningEvent = WarningEvent("Hello", 123L, sensorData)
        val warningEvents = ArrayList<WarningEvent>()
        warningEvents.add(warningEvent)
        val drivingSession1 = DrivingSession(1, "abc", "abc", 123L, 123L, 123L, 100.0f, 100.0f, 123f, warningEvents )
        val drivingSession2 = DrivingSession(1, "abc", "abc", 123L, 123L, 123L, 100.0f, 100.0f, 123f, warningEvents )
        val drivingSession3 = DrivingSession(1, "abc", "abc", 123L, 123L, 123L, 100.0f, 100.0f, 123f, warningEvents )
        val drivingSessionsList = ArrayList<DrivingSession>()
        drivingSessionsList.add(drivingSession1)
        drivingSessionsList.add(drivingSession2)
        drivingSessionsList.add(drivingSession3)

        val model: MutableList<DrivingSession> = drivingSessionsList.reversed().toMutableList()

        listAdapterDrivingSessions = DrivingSessionsHistoryAdapter(model) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("email", email)
            intent.putExtra("index", it.index)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = listAdapterDrivingSessions

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
package edu.licenta.sava.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.R

open class DrawerLayoutActivity : AppCompatActivity() {

    protected lateinit var userId: String
    protected lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUserAndEmail()
    }

    private fun setUserAndEmail() {
        val userIdString = intent.getStringExtra("userId")
        val emailString = intent.getStringExtra("email")
        if (!userIdString.isNullOrEmpty() && !emailString.isNullOrEmpty()) {
            userId = userIdString
            email = emailString
        }
    }

    protected fun initializeToolbarAndMenu(
        toolbar: Toolbar?,
        drawer: DrawerLayout,
        navigation: NavigationView,
        screen: Int
    ) {
        setSupportActionBar(toolbar)

        drawer.addDrawerListener(
            ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.show_navigation,
                R.string.hide_navigation
            )
        )

        navigation.setNavigationItemSelectedListener { menuItem ->
            onOptionsItemSelected(menuItem)
            navigation.setCheckedItem(menuItem)
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        navigation.getHeaderView(0)
            .findViewById<TextView>(R.id.email_menu)
            .text = email

        when (screen) {
            1 -> navigation.setCheckedItem(R.id.dashboard_item)
            2 -> navigation.setCheckedItem(R.id.history_item)
            3 -> navigation.setCheckedItem(R.id.learning_item)
            4 -> navigation.setCheckedItem(R.id.parking_item)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.dashboard_item) {
            dashboardAction()
            return true
        }
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

    private fun dashboardAction() {
        val intent = Intent(this, DashboardActivity::class.java)
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

    fun startSessionAction() {
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

    fun setScoreTextViewColor(score: Int, textView: TextView) {
        when (score) {
            in 85..100 -> textView.setTextColor(Color.parseColor("#FF4BC100"))
            in 75..84 -> textView.setTextColor(Color.parseColor("#FF64DD17"))
            in 60..74 -> textView.setTextColor(Color.parseColor("#FFE1BC00"))
            in 50..59 -> textView.setTextColor(Color.parseColor("#FFE14F00"))
            in 0..49 -> textView.setTextColor(Color.parseColor("#E10000"))
        }
    }
}
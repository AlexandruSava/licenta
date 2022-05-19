package edu.licenta.sava

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
import edu.licenta.sava.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawer: DrawerLayout

    private lateinit var userId: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()
        initializeToolbarAndMenu()
        initializeButtons()
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
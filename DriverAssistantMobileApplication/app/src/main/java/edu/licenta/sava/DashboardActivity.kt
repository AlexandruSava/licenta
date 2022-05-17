package edu.licenta.sava

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()

        initializeButtons()
    }

    private fun setBinding() {
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun initializeButtons() {
        logoutAction()
    }

    private fun logoutAction() {
        binding.logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            Firebase.auth.signOut()
            startActivity(intent)
            finish()
        }
    }
}
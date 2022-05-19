package edu.licenta.sava

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.licenta.sava.databinding.ActivityDrivingSessionBinding

class DrivingSessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrivingSessionBinding

    private lateinit var userId: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserAndEmail()

        initializeButtons()
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

    private fun setBinding() {
        binding = ActivityDrivingSessionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun finishAction() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
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
package edu.licenta.sava.view.activity

import android.os.Bundle
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionDetailedMapBinding
import edu.licenta.sava.model.DrivingSession

class DrivingSessionDetailedMapActivity : DrawerLayoutActivity() {

    private val screenId = 2

    private val databaseController = DatabaseController()

    private lateinit var binding: ActivityDrivingSessionDetailedMapBinding

    private var endTime: Long = 0L

    private lateinit var currentDrivingSession: DrivingSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setEndTime()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        initializeButtons()
        getStorageData()
    }

    private fun setBinding() {
        binding = ActivityDrivingSessionDetailedMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setEndTime() {
        val endTimeLong = intent.getLongExtra("endTime", 0L)
        endTime = endTimeLong
    }

    private fun initializeButtons() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
            finish()
        }
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
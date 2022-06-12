package edu.licenta.sava.view.activity

import android.os.Bundle
import edu.licenta.sava.R
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.databinding.ActivityParkingPositionBinding
import edu.licenta.sava.model.DrivingSession

class ParkingPositionActivity : DrawerLayoutActivity() {

    private val screenId = 4

    private val databaseController = DatabaseController()

    private lateinit var binding: ActivityParkingPositionBinding

    private lateinit var currentDrivingSession: DrivingSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        getStorageData()
        setTextView()
    }

    private fun setBinding() {
        binding = ActivityParkingPositionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getStorageData() {
        val initialized = databaseController.verifyPresenceOfALocalFile(this, userId)
        if (initialized) {
            currentDrivingSession =
                databaseController.getLastDrivingSession(
                    this,
                    userId
                )
        }
    }

    private fun setTextView() {
        if (currentDrivingSession.sensorDataList.isEmpty()) {
            binding.parkingText.text = getString(R.string.no_information_available)
        } else if (currentDrivingSession.sensorDataList.last().latitude.toInt() == 0) {
            binding.parkingText.text = getString(R.string.no_information_available)
        }
    }

    fun getCurrentDrivingSession(): DrivingSession {
        return currentDrivingSession
    }
}
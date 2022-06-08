package edu.licenta.sava.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionDetailedBinding
import edu.licenta.sava.model.DrivingSession
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DrivingSessionDetailedActivity : DrawerLayoutActivity() {

    private val screenId: Int = 2

    private val databaseController = DatabaseController()

    private lateinit var binding: ActivityDrivingSessionDetailedBinding

    private var endTime: Long = 0L
    private var version: Int = 0

    private lateinit var currentDrivingSession: DrivingSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setEndTimeAndVersion()
        initializeButtons()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        getStorageDataAndSetListDataAndTextViews()
    }

    private fun setBinding() {
        binding = ActivityDrivingSessionDetailedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setEndTimeAndVersion() {
        val endTimeLong = intent.getLongExtra("endTime", 0L)
        val versionInt = intent.getIntExtra("version", 0)

        endTime = endTimeLong
        version = versionInt
    }

    private fun initializeButtons() {
        binding.backBtn.setOnClickListener {
            historyAction()
        }

        binding.seeDetailedMapBtn.setOnClickListener {
            seeDetailedMapAction()
        }
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

    private fun seeDetailedMapAction() {
        val intent = Intent(this, DrivingSessionDetailedMapActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        intent.putExtra("endTime", endTime)
        startActivity(intent)
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
}
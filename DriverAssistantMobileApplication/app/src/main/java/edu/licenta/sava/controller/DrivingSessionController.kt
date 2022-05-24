package edu.licenta.sava.controller

import android.util.Log
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.model.Notification
import edu.licenta.sava.model.SensorData
import edu.licenta.sava.model.WarningEvent
import kotlin.math.pow

class DrivingSessionController {

    private lateinit var drivingSession: DrivingSession

    private lateinit var userId: String
    private lateinit var email: String
    private var startTime: Long = 0
    private var duration: Long = 0
    private var drivingSessionScore: Float = 100f
    private var maxDrivingSessionScore: Float = 100f
    private var warningEventsList = ArrayList<WarningEvent>()

    private var speedingTimes: Int = 0

    private var sensorDataList = ArrayList<SensorData>()
    private lateinit var currentSensorData: SensorData

    private val basicScoreReduction: Float = 0.2f
    private val basicScoreGain: Float = 0.15f

    private val basicPower: Float = 2f

    fun startDrivingSession(userId: String, email: String) {
        this.userId = userId
        this.email = email
        startTime = System.currentTimeMillis()
        drivingSessionScore = 100f
        maxDrivingSessionScore = 100f
    }

    fun stopDrivingSession() {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        val averageSpeed = getAverageSpeed()
        val distanceTraveled: Float = averageSpeed * duration / 1000 / 60 / 60
        drivingSession = DrivingSession(
            1,
            userId,
            email,
            startTime,
            endTime,
            duration,
            averageSpeed,
            drivingSessionScore,
            maxDrivingSessionScore,
            distanceTraveled,
            sensorDataList,
            warningEventsList
        )
    }

    fun getAverageSpeed(): Float {
        var sum = 0f
        for (sensorData in sensorDataList) {
            sum += sensorData.speed
        }
        if (sensorDataList.size.toFloat() == 0f)
            return 0f
        return sum / sensorDataList.size.toFloat()
    }

    fun addSensorData(sensorData: SensorData) {
        if (sensorDataList.last().speed < 5 && sensorData.speed < 5) {
            Log.d("Car", "Car standing.")
        } else {
            sensorDataList.add(sensorData)
        }
        currentSensorData = sensorData
        duration = System.currentTimeMillis() - startTime
    }

    fun analyzeDrivingSession(): Float {
        if (currentSensorData.speed > 15) {
            val mistakeRatio = if (currentSensorData.outsideTemp < 3) 2f else 1f

            val speedRatio =
                (currentSensorData.speed / (currentSensorData.speedLimit + 10).toDouble()).toFloat()

            if (speedRatio > 1) {
                speedingTimes++
                reduceDrivingScore(mistakeRatio, speedRatio)
                issueSpeedWarningEvent()
            } else {
                increaseDrivingScore()
            }

            Log.d(
                "SensorData:", "index: ${sensorDataList.size}, speedRatio: $speedRatio, " +
                        "drivingSessionScore: $drivingSessionScore, maxDrivingSessionScore: " +
                        "$maxDrivingSessionScore"
            )
        }

        return drivingSessionScore
    }

    private fun issueSpeedWarningEvent() {
        val warningEvent = WarningEvent(
            Notification.SPEEDING.name.lowercase(),
            System.currentTimeMillis(),
            currentSensorData
        )
        warningEventsList.add(warningEvent)
        println(warningEvent)
    }

    private fun increaseDrivingScore() {
        if (currentSensorData.speed >= 15) {
            if (drivingSessionScore + basicScoreGain < maxDrivingSessionScore) {
                drivingSessionScore += basicScoreGain
            } else {
                drivingSessionScore = maxDrivingSessionScore
            }
        }
    }

    private fun reduceDrivingScore(mistakeRatio: Float, speedRatio: Float) {
        val mistakeScoreReduction = basicPower.pow(basicScoreReduction * mistakeRatio * speedRatio)

        if (mistakeScoreReduction * 0.3f > maxDrivingSessionScore) {
            maxDrivingSessionScore = 0f
        } else {
            maxDrivingSessionScore -= mistakeScoreReduction * 0.3f
        }

        if (mistakeScoreReduction > drivingSessionScore) {
            drivingSessionScore = 0f
        } else {
            drivingSessionScore -= mistakeScoreReduction
        }
    }

    fun getLastWarningEvent(): WarningEvent {
        return if (speedingTimes > 0) {
            warningEventsList.last()
        } else {
            WarningEvent(
                Notification.GOOD_DRIVING.name.lowercase(),
                System.currentTimeMillis(),
                currentSensorData
            )
        }
    }

    fun getDrivingSession(): DrivingSession {
        return drivingSession
    }

    fun getDuration(): Long {
        return duration
    }

}
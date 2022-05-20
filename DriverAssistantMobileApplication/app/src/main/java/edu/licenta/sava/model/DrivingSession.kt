package edu.licenta.sava.model

import java.io.Serializable

data class DrivingSession(
    var index: Int,
    val userId: String,
    val email: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val averageSpeed: Float,
    val finalScore: Float,
    val finalMaximumScore: Float,
    val distanceTraveled: Float,
    val sensorDataList: ArrayList<SensorData>,
    val warningEventsList: ArrayList<WarningEvent>,
) : Serializable

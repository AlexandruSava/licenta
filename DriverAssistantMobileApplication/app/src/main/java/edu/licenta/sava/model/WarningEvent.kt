package edu.licenta.sava.model

data class WarningEvent(
    val type: String,
    val timestamp: Long,
    val sensorData: SensorData
)
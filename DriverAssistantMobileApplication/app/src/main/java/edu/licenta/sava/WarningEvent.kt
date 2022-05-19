package edu.licenta.sava

data class WarningEvent(
    val type: String,
    val timestamp: Long,
    val sensorData: SensorData
)
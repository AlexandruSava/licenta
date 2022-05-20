package edu.licenta.sava.model

data class SensorData(
    val speed: Int,
    val outsideTemp: Int,
    val latitude: Double,
    val longitude: Double,
    val speedLimit: Int
)
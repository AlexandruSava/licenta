package edu.licenta.sava.service

import edu.licenta.sava.model.DrivingSession

class DashboardService {

    fun calculateUserScore(drivingSessionList: ArrayList<DrivingSession>): Int {
        val totalDistance = calculateTotalDistance(drivingSessionList)

        var ponderedAverage = 0f
        for (drivingSession in drivingSessionList) {
            val weight = (drivingSession.distanceTraveled / totalDistance)
            ponderedAverage += weight * drivingSession.finalScore
        }
        return ponderedAverage.toInt()
    }

    fun calculateAverageSpeed(drivingSessionsList: ArrayList<DrivingSession>): Int {
        var sumAverageSpeed = 0f
        for (drivingSession in drivingSessionsList) {
            sumAverageSpeed += drivingSession.averageSpeed
        }
        return (sumAverageSpeed / drivingSessionsList.size.toFloat()).toInt()
    }

    fun calculateTotalDistance(drivingSessionsList: ArrayList<DrivingSession>): Float {
        var distance = 0f
        for (drivingSession in drivingSessionsList) {
            distance += drivingSession.distanceTraveled
        }

        return distance
    }

    fun calculateImprovement(drivingSessionsList: ArrayList<DrivingSession>): String {
        var scoreSumOld = 0f
        var scoreSumNew = 0f

        for (i in drivingSessionsList.size - 6 until drivingSessionsList.size - 1) {
            scoreSumNew += drivingSessionsList[i].finalScore
        }

        for (i in drivingSessionsList.size - 10 until drivingSessionsList.size - 6) {
            scoreSumOld += drivingSessionsList[i].finalScore
        }

        return if (scoreSumNew > scoreSumOld) {
            var improvement = "+ "
            improvement += ((scoreSumNew / scoreSumOld - 1) * 100).toInt().toString()
            improvement += " %"
            improvement
        } else {
            var improvement = "- "
            improvement += ((scoreSumOld / scoreSumNew - 1) * 100).toInt().toString()
            improvement += " %"
            improvement
        }
    }
}
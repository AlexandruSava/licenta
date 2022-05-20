package edu.licenta.sava.database

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.model.SensorData
import edu.licenta.sava.model.WarningEvent

class FirebaseController : Application() {

    private val databaseController = DatabaseController()

    override fun onCreate() {
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)
    }

    fun getFirebaseDataAndWriteDrivingSessionsDataInLocalStorage(
        snapshot: DataSnapshot,
        userId: String,
        context: Context
    ) {
        Log.d("FIREBASE", "Trying to get data from Firebase.")
        val drivingSessionsData = snapshot.children

        var sensorDataBuilder: SensorData
        var warningEventBuilder: WarningEvent
        var warningEventListBuilder: ArrayList<WarningEvent>
        var drivingSessionBuilder: DrivingSession
        val drivingSessionsListBuilder = ArrayList<DrivingSession>()

        for (drivingSession in drivingSessionsData) {

            val warningEventsList = drivingSession.child("warningEventsList").children

            warningEventListBuilder = ArrayList()

            for (warningEvent in warningEventsList) {
                val sensorDataData = warningEvent.child("sensorData")
                sensorDataBuilder = SensorData(
                    sensorDataData.child("speed").value.toString().toInt(),
                    sensorDataData.child("outsideTemp").value.toString().toInt(),
                    sensorDataData.child("latitude").value.toString().toDouble(),
                    sensorDataData.child("longitude").value.toString().toDouble(),
                    sensorDataData.child("speedLimit").value.toString().toInt()
                )

                warningEventBuilder = WarningEvent(
                    warningEvent.child("type").value.toString(),
                    warningEvent.child("timestamp").value.toString().toLong(),
                    sensorDataBuilder
                )

                warningEventListBuilder.add(warningEventBuilder)
            }

            drivingSessionBuilder = DrivingSession(
                drivingSession.child("index").value.toString().toInt(),
                drivingSession.child("userId").value.toString(),
                drivingSession.child("email").value.toString(),
                drivingSession.child("startTime").value.toString().toLong(),
                drivingSession.child("endTime").value.toString().toLong(),
                drivingSession.child("duration").value.toString().toLong(),
                drivingSession.child("averageSpeed").value.toString().toFloat(),
                drivingSession.child("finalScore").value.toString().toFloat(),
                drivingSession.child("finalMaximumScore").value.toString().toFloat(),
                warningEventListBuilder
            )

            drivingSessionsListBuilder.add(drivingSessionBuilder)
        }

        Log.d("FIREBASE", "Successfully retrieved data from Firebase.")

        databaseController.writeDrivingSessionsDataInLocalStorage(
            context,
            userId,
            drivingSessionsListBuilder
        )
    }
}
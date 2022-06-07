package edu.licenta.sava.controller

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.database.DatabaseReference
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.model.SensorData
import edu.licenta.sava.model.WarningEvent

class DatabaseController {

    fun getDrivingSessionsDataFromLocalStorage(
        context: Context,
        userId: String
    ): ArrayList<DrivingSession> {
        Log.d("STORAGE", "Attempting to read data from local storage for user $userId")
        val data = readDrivingSessionsDataFromLocalStorage(context, userId)
        Log.d("STORAGE", "Successfully read data: $data")
        return data
    }

    private fun readDrivingSessionsDataFromLocalStorage(
        context: Context,
        userId: String
    ): ArrayList<DrivingSession> {
        val initializedDatabase = verifyPresenceOfALocalFile(context, userId)
        if (initializedDatabase) {
            val objectMapper = jacksonObjectMapper()
            var drivingSessionList: ArrayList<DrivingSession>

            context.openFileInput(userId)
                .use { stream ->
                    val data = stream
                        .bufferedReader()
                        .use { it.readText() }
                    drivingSessionList =
                        objectMapper.readValue(data)
                }
            return drivingSessionList
        }
        return getFakeDrivingSession()
    }

    private fun getFakeDrivingSession(): ArrayList<DrivingSession> {
        val fakeSensorData = SensorData(
            0,
            0,
            0.toDouble(),
            0.toDouble(),
            0
        )
        val fakeSensorDataList = ArrayList<SensorData>()
        fakeSensorDataList.add(fakeSensorData)
        val fakeWarningEventsList = arrayListOf(
            WarningEvent(
                "",
                0,
                fakeSensorData
            )
        )
        val fakeDrivingSession = DrivingSession(
            0,
            "",
            "",
            0L,
            0L,
            0L,
            0f,
            0f,
            0f,
            0f,
            fakeSensorDataList,
            fakeWarningEventsList
        )
        val fakeData = ArrayList<DrivingSession>()
        fakeData.add(fakeDrivingSession)
        return fakeData
    }

    fun writeDrivingSessionsDataInLocalStorage(
        context: Context,
        userId: String,
        drivingSessionsList: ArrayList<DrivingSession>
    ) {
        Log.d("STORAGE", "Writing data to DB $drivingSessionsList")

        val objectMapper = jacksonObjectMapper()
        val data: ByteArray = objectMapper.writeValueAsBytes(drivingSessionsList)
        context.openFileOutput(
            userId,
            Context.MODE_PRIVATE
        ).use {
            it.write(data)
        }
    }

    fun verifyPresenceOfALocalFile(context: Context, userId: String): Boolean {
        val files: Array<String> = context.fileList()
        if (userId in files) {
            return true
        }
        return false
    }

    fun getDrivingSessionBySessionEndTime(
        context: Context,
        userId: String,
        endTime: Long
    ): DrivingSession {
        val initializedDatabase = verifyPresenceOfALocalFile(context, userId)
        if (initializedDatabase) {
            val drivingSessionsList = readDrivingSessionsDataFromLocalStorage(context, userId)
            for (drivingSession in drivingSessionsList) {
                if (drivingSession.endTime == endTime) {
                    return drivingSession
                }
            }
        }
        return getFakeDrivingSession().first()
    }

    fun writeDrivingSessionsDataInLocalStorage(
        context: Context,
        database: DatabaseReference,
        userId: String,
        drivingSession: DrivingSession
    ) {
        val initializedDatabase = verifyPresenceOfALocalFile(context, userId)
        var drivingSessionsList = ArrayList<DrivingSession>()
        if (initializedDatabase) {
            drivingSessionsList = readDrivingSessionsDataFromLocalStorage(context, userId)
            drivingSession.index = drivingSessionsList.size + 1
        } else {
            drivingSession.index = 1
        }
        drivingSessionsList.add(drivingSession)

        Log.d("STORAGE", "Writing data to DB $drivingSessionsList")

        val objectMapper = jacksonObjectMapper()
        val data: ByteArray = objectMapper.writeValueAsBytes(drivingSessionsList)
        context.openFileOutput(
            userId,
            Context.MODE_PRIVATE
        ).use {
            it.write(data)
        }

        database.child(userId).setValue(drivingSessionsList)
    }

    fun getLastDrivingSession(
        context: Context,
        userId: String
    ): DrivingSession {
        val initializedDatabase = verifyPresenceOfALocalFile(context, userId)
        if (initializedDatabase) {
            val drivingSessionsList = readDrivingSessionsDataFromLocalStorage(context, userId)
            return drivingSessionsList.last()
        }
        return getFakeDrivingSession().first()
    }
}
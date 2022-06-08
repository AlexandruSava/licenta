package edu.licenta.sava.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.licenta.sava.controller.DatabaseController
import edu.licenta.sava.controller.FirebaseController
import edu.licenta.sava.databinding.ActivityDrivingSessionsHistoryBinding
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.view.adapter.DrivingSessionsHistoryAdapter

class DrivingSessionsHistoryActivity : DrawerLayoutActivity() {

    private val screenId: Int = 2

    private val databaseController = DatabaseController()
    private val firebaseController = FirebaseController()

    private lateinit var binding: ActivityDrivingSessionsHistoryBinding
    private lateinit var listAdapterDrivingSessions: DrivingSessionsHistoryAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        getStorageDataAndInitList()
    }

    private fun setBinding() {
        binding = ActivityDrivingSessionsHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getStorageDataAndInitList() {
        val initialized = databaseController.verifyPresenceOfALocalFile(this, userId)
        if (initialized) {
            val drivingSessionsList =
                databaseController.getDrivingSessionsDataFromLocalStorage(this, userId)
            drivingSessionsList.sortWith(compareBy { it.endTime })
            initList(drivingSessionsList)
        }
    }

    private fun initList(drivingSessionsList: ArrayList<DrivingSession>) {
        val model: MutableList<DrivingSession> = drivingSessionsList.reversed().toMutableList()

        listAdapterDrivingSessions =
            DrivingSessionsHistoryAdapter(
                model,
                this::moreDetailsAction,
                this::deleteDrivingSession
            )

        recyclerView = binding.historyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = listAdapterDrivingSessions
    }

    private fun moreDetailsAction(drivingSession: DrivingSession) {
        val intent = Intent(this, DrivingSessionDetailedActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("email", email)
        intent.putExtra("endTime", drivingSession.endTime)
        intent.putExtra("version", 0)
        startActivity(intent)
    }

    private fun deleteDrivingSession(drivingSession: DrivingSession) {
        firebaseController.deleteDrivingSession(drivingSession)
    }
}
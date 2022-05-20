package edu.licenta.sava.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.licenta.sava.model.DrivingSession
import edu.licenta.sava.R
import java.text.SimpleDateFormat
import java.util.*

class DrivingSessionsHistoryAdapter(
    private val drivingSessionsList: MutableList<DrivingSession>,
    private val onClick: (DrivingSession) -> (Unit)
) : RecyclerView.Adapter<DrivingSessionsHistoryAdapter.HistoryViewHolder>() {

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        holder.bind(drivingSessionsList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_driving_session, parent, false)

        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return drivingSessionsList.size
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onClick(drivingSessionsList[adapterPosition])
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun bind(drivingSession: DrivingSession) {
            val stringIndex = "#${drivingSession.index} Driving Session"
            val score = drivingSession.finalScore.toInt()
            val startTime = drivingSession.startTime
            val date = Date(startTime)
            val formatter = SimpleDateFormat("dd MMMM yyyy hh:mm")
            formatter.timeZone = SimpleTimeZone(0, "GMT")
            val formattedDate = formatter.format(date)
        }

    }

    private fun setScoreTextViewColor(score: Int, textView: TextView) {
        when (score) {
            in 85..100 -> textView.setTextColor(Color.parseColor("#FF4BC100"))
            in 75..84 -> textView.setTextColor(Color.parseColor("#FF64DD17"))
            in 60..74 -> textView.setTextColor(Color.parseColor("#FFE1BC00"))
            in 50..59 -> textView.setTextColor(Color.parseColor("#FFE14F00"))
            in 0..49 -> textView.setTextColor(Color.parseColor("#E10000"))
        }
    }
}
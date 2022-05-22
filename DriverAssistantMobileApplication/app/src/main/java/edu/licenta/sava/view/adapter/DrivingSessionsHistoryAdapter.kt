package edu.licenta.sava.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.licenta.sava.R
import edu.licenta.sava.model.DrivingSession
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DrivingSessionsHistoryAdapter(
    private val drivingSessionsList: MutableList<DrivingSession>,
    private val onMoreDetailsClick: (DrivingSession) -> (Unit),
    private val onDeleteClick: (DrivingSession) -> (Unit)
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
            itemView.findViewById<MaterialButton>(R.id.delete_btn).setOnClickListener {
                val longMessage = "Are you sure you want to delete this driving session?"
                val positiveText = "Delete Driving Session"
                val negativeText = "Cancel"

                MaterialAlertDialogBuilder(itemView.context)
                    .setMessage(longMessage)
                    .setPositiveButton(positiveText) { _, _ ->
                        run {
                            onDeleteClick(drivingSessionsList[adapterPosition])
                            drivingSessionsList.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        }
                    }
                    .setNegativeButton(negativeText, null)
                    .show()
            }

            itemView.findViewById<MaterialButton>(R.id.more_details_btn).setOnClickListener {
                onMoreDetailsClick(drivingSessionsList[adapterPosition])
            }

        }

        @SuppressLint("SimpleDateFormat")
        fun bind(drivingSession: DrivingSession) {

            // Formatters
            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode = RoundingMode.DOWN

            val formatterDate = SimpleDateFormat("dd MMMM yyyy HH:mm")

            val formatterDuration = SimpleDateFormat("HH:mm")
            formatterDuration.timeZone = TimeZone.getTimeZone("GMT")

            // Get data to display
            val averageSpeed = decimalFormat.format(drivingSession.averageSpeed) + " km/h"
            val date = formatterDate.format(drivingSession.endTime)
            val duration = formatterDuration.format(drivingSession.duration)
            val distance = decimalFormat.format(drivingSession.distanceTraveled) + " km"
            val score = decimalFormat.format(drivingSession.finalScore)
            val warnings = drivingSession.warningEventsList.size.toString() + " Warnings"

            // Display data
            itemView.findViewById<TextView>(R.id.average_speed).text = averageSpeed
            itemView.findViewById<TextView>(R.id.date).text = date
            itemView.findViewById<TextView>(R.id.distance).text = distance
            itemView.findViewById<TextView>(R.id.duration).text = duration
            itemView.findViewById<TextView>(R.id.score).text = score
            itemView.findViewById<TextView>(R.id.warnings).text = warnings

            // Set color text to score
            setScoreTextViewColor(
                drivingSession.finalScore.toInt(),
                itemView.findViewById(R.id.score)
            )
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
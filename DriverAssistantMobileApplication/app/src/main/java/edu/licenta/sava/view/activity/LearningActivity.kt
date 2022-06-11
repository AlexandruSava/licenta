package edu.licenta.sava.view.activity

import android.content.Intent
import android.os.Bundle
import edu.licenta.sava.databinding.ActivityLearningBinding

class LearningActivity : DrawerLayoutActivity() {

    private val screenId = 3

    private lateinit var binding: ActivityLearningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        initializeButtons()
    }

    private fun setBinding() {
        binding = ActivityLearningBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun initializeButtons() {
        binding.seatbeltLearnBtn.setOnClickListener {
            val intent = Intent(this, LearningArticleActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("email", email)
            intent.putExtra("article", "seatbelt")
            startActivity(intent)
        }

        binding.understeeringBtn.setOnClickListener {
            val intent = Intent(this, LearningArticleActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("email", email)
            intent.putExtra("article", "understeering")
            startActivity(intent)
        }
    }
}
package edu.licenta.sava.view.activity

import android.os.Bundle
import edu.licenta.sava.databinding.ActivityLearningArticleBinding

class LearningArticleActivity : DrawerLayoutActivity() {

    private val screenId = 3

    private lateinit var binding: ActivityLearningArticleBinding

    private lateinit var article: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setArticleString()
        initializeToolbarAndMenu(
            binding.toolbar,
            binding.drawer,
            binding.navigationView,
            screenId
        )
        initializeButtons()
    }

    private fun setBinding() {
        binding = ActivityLearningArticleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setArticleString() {
        val articleString = intent.getStringExtra("article")
        if (!articleString.isNullOrEmpty()) {
            article = articleString
        }
    }

    private fun initializeButtons() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}
package edu.licenta.sava.view.activity

import android.os.Bundle
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import edu.licenta.sava.R
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
        setTextViewsAndYoutubeVideo()
    }

    private fun setTextViewsAndYoutubeVideo() {
        var videoId = ""
        when (article) {
            "seatbelt" -> {
                videoId = "y3InF19dzlM"
                setYoutubeVideo(videoId)
                binding.articleTitle.text = getString(R.string.seatbelt)
                binding.articleFirstParagraph.text = getString(R.string.seatbelt_first_paragraph)
                binding.articleSecondParagraph.text = getString(R.string.seatbelt_second_paragraph)
            }
            "understeering" -> {
                videoId = "EwmDdMzzDjY"
                setYoutubeVideo(videoId)
                binding.articleTitle.text = getString(R.string.understeering)
                binding.articleFirstParagraph.text =
                    getString(R.string.understeering_first_paragraph)
                binding.articleSecondParagraph.text =
                    getString(R.string.understeering_second_paragraph)
            }
        }

        setYoutubeVideo(videoId)
    }

    private fun setYoutubeVideo(videoId: String) {
        val youTubePlayerView = binding.youtubePlayer
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
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
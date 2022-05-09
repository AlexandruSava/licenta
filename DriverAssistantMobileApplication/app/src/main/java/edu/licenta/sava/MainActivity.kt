package edu.licenta.sava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.licenta.sava.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeButtons()
    }

    fun initializeButtons() {
        cancelAction()

        nextAction()
    }

    private fun nextAction() {
        binding.nextButton.setOnClickListener {

        }
    }

    private fun cancelAction() {
        binding.cancelButton.setOnClickListener {

        }
    }
}
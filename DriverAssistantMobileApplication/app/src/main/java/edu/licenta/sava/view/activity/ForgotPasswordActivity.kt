package edu.licenta.sava.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
    }

    private fun setBinding() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun initializeButtons() {
        binding.createBtn.setOnClickListener {
            createAccountAction()
        }

        binding.loginBtn.setOnClickListener {
            alreadyHaveAnAccountAction()
        }

        binding.nextButton.setOnClickListener {
            submitForgotPasswordAction()
        }
    }

    private fun createAccountAction() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun alreadyHaveAnAccountAction() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun submitForgotPasswordAction() {
        when {
            TextUtils.isEmpty(binding.emailInput.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val email: String = binding.emailInput.text.toString().trim { it <= ' ' }

                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Please check your email and reset your password.",
                                Toast.LENGTH_LONG
                            ).show()

                            alreadyHaveAnAccountAction()
                        } else {
                            Toast.makeText(
                                this,
                                "No internet connection. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}
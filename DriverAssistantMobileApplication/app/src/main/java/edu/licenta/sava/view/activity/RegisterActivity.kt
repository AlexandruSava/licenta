package edu.licenta.sava.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.licenta.sava.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()

        initializeButtons()
    }

    private fun initializeButtons() {
        alreadyHaveAnAccountAction()
        registerAction()
    }

    private fun registerAction() {
        binding.nextButton.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.emailInput.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.passwordInput.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val email: String = binding.emailInput.text.toString().trim { it <= ' ' }
                    val password: String = binding.passwordInput.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                startApplication(firebaseUser, email)
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun alreadyHaveAnAccountAction() {
        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setBinding() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun startApplication(
        firebaseUser: FirebaseUser,
        email: String
    ) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("userId", firebaseUser.uid)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
}
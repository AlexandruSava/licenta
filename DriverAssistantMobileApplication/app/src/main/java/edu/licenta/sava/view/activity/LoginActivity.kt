package edu.licenta.sava.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.licenta.sava.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()

        verifyLoggedInUser()
        initializeButtons()
    }

    private fun verifyLoggedInUser() {
        val user = Firebase.auth.currentUser

        if (user != null) {
            user.email?.let { startApplication(user, it) }
        }
    }

    private fun setBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun initializeButtons() {
        binding.nextButton.setOnClickListener {
            loginAction()
        }

        binding.createBtn.setOnClickListener {
            createAccountAction()
        }

        binding.forgotPasswordBtn.setOnClickListener {
            forgotPasswordAction()
        }
    }

    private fun createAccountAction() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun forgotPasswordAction() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginAction() {
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
                    .signInWithEmailAndPassword(email, password)
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
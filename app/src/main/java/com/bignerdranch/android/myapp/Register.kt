package com.bignerdranch.android.myapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.register.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class Register : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            registerUser()
        }

        btnGoToLogin.setOnClickListener {
            Intent(this@Register, Login::class.java).also {
                startActivity(it)
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        myAppRed_Register.setOnClickListener{
            myAppRed_Register.toggleVisibility()
            myAppBlue_Register.toggleVisibility()
        }

        myAppBlue_Register.setOnClickListener{
            myAppBlue_Register.toggleVisibility()
            myAppRed_Register.toggleVisibility()
        }
    }

    private fun View.toggleVisibility() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }
    }

    private fun registerUser() {
        val email = etEmailRegister.text.toString()
        val password = etPasswordRegister.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        val toast = Toast.makeText(this@Register, "Registered Successfully!", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        Intent(this@Register, Login::class.java).also {
                            startActivity(it)
                            finish()
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Register, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}
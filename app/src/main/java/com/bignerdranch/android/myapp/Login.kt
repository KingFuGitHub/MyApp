package com.bignerdranch.android.myapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = FirebaseAuth.getInstance()

        btnGoToRegister.setOnClickListener {
            Intent(this, Register::class.java).also {
                startActivity(it)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        btnLogin.setOnClickListener {
            loginUser()
        }

        myAppRed_Login.setOnClickListener{
            myAppRed_Login.toggleVisibility()
            myAppBlue_Login.toggleVisibility()
        }

        myAppBlue_Login.setOnClickListener{
            myAppBlue_Login.toggleVisibility()
            myAppRed_Login.toggleVisibility()
        }

        ibGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(this, options)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }
    }

    private fun View.toggleVisibility() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@Login, "Successfully Logged in", Toast.LENGTH_LONG).show()
                    Intent(this@Login, Home::class.java).also {
                        startActivity(it)
                        finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }

            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@Login, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN){
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }


    private fun loginUser() {
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if(email.isEmpty() && password.isEmpty()){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Login, "Please enter email and password", Toast.LENGTH_LONG).show()
                        }
                    }
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        val toast = Toast.makeText(this@Login, "Login Successfully!", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        Intent(this@Login, Home::class.java).also {
                            startActivity(it)
                            finish()
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Login, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

    }

}
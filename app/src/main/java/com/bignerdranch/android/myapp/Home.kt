package com.bignerdranch.android.myapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_left_header.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


const val IMAGE_REQUEST_CODE = 0

open class Home : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        toggle = ActionBarDrawerToggle(this, drawerHome, R.string.open, R.string.close)

        drawerHome.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        downloadImage("profilePicture")

        navViewLeftProfile.setNavigationItemSelectedListener {

            val toast = Toast.makeText(this, "You're currently in Home", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 0)

            when (it.itemId) {
                R.id.miHome ->  toast.show()

                R.id.miChat -> Intent(this@Home, Chat::class.java).also {
                    startActivity(it)
                    finish()
                }
                R.id.miGames -> Intent(this@Home, Game::class.java).also {
                    startActivity(it)
                    finish()
                }
                R.id.miMusic -> Intent(this@Home, Music::class.java).also {
                    startActivity(it)
                    finish()
                }

                R.id.miProfile -> Intent(this@Home, Profile::class.java).also {
                    startActivity(it)
                    finish()
                }

                R.id.miLogout -> Intent(this@Home, Login::class.java).also {
                    startActivity(it)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }
            true
        }

        listFiles()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun chooseImage(view: View) {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {

            data?.data?.let {
                curFile = it
                ivProfilePicture.setImageURI(it)
                uploadImageToStorage("profilePicture") // to upload profile picture to Firebase Cloud Storage
            }
        }
    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                imageRef.child("images/$filename").putFile(it).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Home, "Successfully uploaded image", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Home, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun downloadImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val maxDownloadSize = 5L * 1024 * 1024
            val bytes = imageRef.child("images/$filename").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                ivProfilePicture.setImageBitmap(bmp)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Home, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to close this activity?")
            .setPositiveButton(
                "Yes"
            ) { dialog, which -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try{
            val images = imageRef.child("pictures/").listAll().await()
            val imageUrls = mutableListOf<String>()
            for(image in images.items){
                val url = image.downloadUrl.await()
                imageUrls.add(url.toString())
            }
            withContext(Dispatchers.Main){
                val imageAdapter = ImageAdapter(imageUrls)
                rvImages.apply{
                    adapter = imageAdapter
                    layoutManager = LinearLayoutManager(this@Home)
                }
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@Home, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}

package com.bignerdranch.android.myapp


import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.chat.*
import kotlinx.android.synthetic.main.nav_left_header.*
import kotlinx.android.synthetic.main.profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Profile : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference
    private var mp: MediaPlayer? = null
    private var personCollectionRef = Firebase.firestore.collection("persons")
    private var firstName = ""
    private var lastName = ""
    private var age = -1
    private var refreshAppCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        toggle = ActionBarDrawerToggle(this, drawerProfile, R.string.open, R.string.close)

        drawerProfile.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        downloadImage("profilePicture")

        scSwipeRefresh.setOnCheckedChangeListener { _, isChecked ->
            refreshAppCheck = isChecked
            swipeToRefresh.isEnabled = refreshAppCheck
        }


        refreshApp()

        navViewLeftProfile.setNavigationItemSelectedListener {

            val toast = Toast.makeText(this, "You're currently in settings", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 0)

            when (it.itemId) {
                R.id.miHome -> Intent(this, Home::class.java).also {
                    startActivity(it)
                    finish()
                }

                R.id.miChat -> Intent(this, Chat::class.java).also {
                    startActivity(it)
                    finish()
                }

                R.id.miGames -> Intent(this, Game::class.java).also {
                    startActivity(it)
                    finish()
                }

                R.id.miMusic -> Intent(this, Music::class.java).also {
                    startActivity(it)
                    finish()
                }

                R.id.miProfile ->  toast.show()

                R.id.miLogout -> Intent(this, Login::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            true
        }

        btnSaveToDataBase.setOnClickListener {
            val person = getOldPerson()
            savePerson(person)
        }

        btnUpdateChanges.setOnClickListener {
            val oldPerson = getOldPerson()
            val newPerson = getNewPersonMap()
            updatePerson(oldPerson,newPerson)
        }

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
            startActivityForResult(it, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 0) {

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
                    Toast.makeText(this@Profile, "Successfully uploaded image", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Profile, e.message, Toast.LENGTH_LONG).show()
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
                ivProfilePicture2.setImageBitmap(bmp)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Profile, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try{
            personCollectionRef.add(person).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@Profile, "Successfully saved data!", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@Profile, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getNewPersonMap():Map<String,Any>{
        val firstName = etNewFirstName.text.toString()
        val lastName = etNewLastName.text.toString()
        val age = etNewAge.text.toString()
        val map = mutableMapOf<String, Any>()
        if(firstName.isNotEmpty()){
            map["firstName"] = firstName
        }
        if(lastName.isNotEmpty()){
            map["lastName"] = lastName
        }
        if(age.isNotEmpty()){
            map["age"] = age.toInt()
        }
        return map
    }

    private fun getOldPerson() : Person{
         firstName = etFirstName.text.toString()
         lastName = etLastName.text.toString()
         age = etAge.text.toString().toInt()
        return Person(firstName, lastName, age)
    }


    private fun updatePerson( person: Person, newPersonMap: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        val personQuery = personCollectionRef
            .whereEqualTo("firstName", firstName)
            .whereEqualTo("lastName", lastName)
            .whereEqualTo("age", age)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty()){
            for(document in personQuery){
                try{
                    personCollectionRef.document(document.id).update("firstName", firstName)
                    personCollectionRef.document(document.id).set(
                        newPersonMap,
                        SetOptions.merge()
                    ).await()
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@Profile, "No person matched the query", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            withContext(Dispatchers.Main){
                Toast.makeText(this@Profile, "No person matched the query", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshApp() {
        if(refreshAppCheck) {
            swipeToRefresh.setOnRefreshListener {

                downloadImage("profilePicture")

                tvFirstName.text = "First: " + etFirstName.text.toString()
                tvLastName.text = "Last: " + etLastName.text.toString()
                tvAge.text = "Age: " + etAge.text.toString()

                Handler(Looper.getMainLooper()).postDelayed({
                    swipeToRefresh.isRefreshing = false
                }, 1000)
            }
        }
    }

}
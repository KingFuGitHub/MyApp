package com.bignerdranch.android.myapp

import android.app.*
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.games.*
import kotlinx.android.synthetic.main.music.*
import kotlinx.android.synthetic.main.music_popup_window.*
import kotlinx.android.synthetic.main.nav_left_header.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Runnable




class Music : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private var curFile: Uri? = null
    private val imageRef = Firebase.storage.reference
    private val songRef = Firebase.storage.reference

    var runnable: Runnable? = null
    var handler = Handler(Looper.getMainLooper())
    private var autoPlay = true
//    private val CHANNEL_ID = "channel_id"
//    private val notificationId = 1

    companion object {
        var mp: MediaPlayer? = null
    }

    var mp1: MediaPlayer? = null
    var marqueeText = true
    lateinit var currentPicture: ImageView
    lateinit var currentTitle: TextView
    var addSongCheck = false
    var currentSong = arrayListOf(
        R.raw.congratulations,
        R.raw.in_the_end,
        R.raw.love_me_like_you_do,
        R.raw.dont_turn_back,
        R.raw.young_and_beautiful,
        R.raw.my_heart_will_go_on,
        R.raw.stay,
        R.raw.holiday
    )
    var picture = arrayListOf<ImageView>()
    var title = arrayListOf<TextView>()
    var title2 = arrayListOf<String>()
    var totalSong = currentSong.size
    var currentSongIndex = 0
    var stopUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.music)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        window.decorView.toggleVisibility()
//        createNotificationChannel()
        val constraintLayout = findViewById<ConstraintLayout>(R.id.MusicBackground)
        val animationDrawable: AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)

        picture.add(0, ivCongratulations)
        picture.add(1, ivInTheEnd)
        picture.add(2, ivLoveMeLikeYouDo)
        picture.add(3, ivDontTurnBack)
        picture.add(4, ivYoungAndBeautiful)
        picture.add(5, ivMyHeartWillGoOn)
        picture.add(6, ivStay)
        picture.add(7, ivHoliday)
        currentPicture = picture[0]

        title.add(0, tvCongratulations)
        title.add(1, tvInTheEnd)
        title.add(2, tvLoveMeLikeYouDo)
        title.add(3, tvDontTurnBack)
        title.add(4, tvYoungAndBeautiful)
        title.add(5, tvMyHeartWillGoOn)
        title.add(6, tvStay)
        title.add(7, tvHoliday)
        currentTitle = title[0]

        title2.add(0, "Congratulations by PewDiePie")
        title2.add(1, "In The End by Linkin Park")
        title2.add(2, "Love Me Like You Do by Ellie Groulding")
        title2.add(3, "Don't Turn Back by Silent Partner")
        title2.add(4, "Young and Beautiful by Lana Del Rey")
        title2.add(5, "My Heart Will Go On by Celine Dion")
        title2.add(6, "Stay by The Kid LAROI")
        title2.add(7, "Holiday by KSI")

        val actvSearchSong = findViewById<AutoCompleteTextView>(R.id.actvSearchSong)
        val songName = resources.getStringArray(R.array.song_name)
        val adapter =
            ArrayAdapter(this, R.layout.auto_complete_search_music, R.id.tvCustom, songName)
        actvSearchSong.setAdapter(adapter)

        actvSearchSong.setOnItemClickListener { _, _, position, _ ->

            val inputMethodManager2 =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager2.hideSoftInputFromWindow(actvSearchSong.windowToken, 0)

            val value = adapter.getItem(position) ?: String
            if (value == "Congratulations") {
                selectSong(0)
                animationDrawable.start()

            } else if (value == "In The End") {
                selectSong(1)
                animationDrawable.start()

            } else if (value == "Love Me Like You Do") {
                selectSong(2)
                animationDrawable.start()

            } else if (value == "Dont Turn Back") {
                selectSong(3)
                animationDrawable.start()

            } else if (value == "Young and Beautiful") {
                selectSong(4)
                animationDrawable.start()

            } else if (value == "My Heart Will Go On") {
                selectSong(5)
                animationDrawable.start()

            } else if (value == "Stay") {
                selectSong(6)
                animationDrawable.start()

            } else if (value == "Holiday") {
                selectSong(7)
                animationDrawable.start()
            }
            actvSearchSong.setText("")
            actvSearchSong.clearListSelection()
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                actvSearchSong.windowToken,
                0
            ) // to hide soft keyboard
            actvSearchSong.isCursorVisible = false

        }

        ivPlay.setOnClickListener {

            if (mp == null) {
                mp = MediaPlayer.create(this, currentSong[currentSongIndex])
                initialiseSeekBar()
            }

            if (mp?.isPlaying == true) {
                mp?.pause()
                animationDrawable.stop()
                if (marqueeText) {
                    currentTitle.isSelected = false
                } else if (!marqueeText) {
                    currentTitle.isSelected = false
                }
                ivPlay.setImageResource(R.drawable.ic_play_circle)
            } else if (mp?.isPlaying == false) {
                if (mp?.currentPosition == mp?.duration) {
                    currentPicture.toggleVisibility()
                    currentTitle.toggleVisibility()

                    currentSongIndex = (currentSongIndex + 1) % totalSong

                    togglePictureAndTitle()

                    mp?.stop()
                    mp?.reset()
                    mp?.release()
                    mp = MediaPlayer.create(this, currentSong[currentSongIndex])
                    initialiseSeekBar()
                    mp?.start()
                    animationDrawable.start()


                    //MusicBackground.setBackgroundResource(R.drawable.gradient_background_music)
                    val broadcastIntent3 = Intent(this, NotificationReceiver3::class.java)
                    val pendingIntent3 = PendingIntent.getBroadcast(
                        this, 0, broadcastIntent3, 0
                    ).send()
                } else {
                    mp?.start()
                    animationDrawable.start()
                    if (marqueeText) {
                        currentTitle.isSelected = true
                    } else if (!marqueeText) {
                        currentTitle.isSelected = false
                    }
                    ivPlay.setImageResource(R.drawable.ic_pause_circle)
                }
            }
            startService()
//            val broadcastIntent2 = Intent(this, NotificationReceiver2::class.java)
//            val pendingIntent2 = PendingIntent.getBroadcast(
//                this, 0, broadcastIntent2, 0
//            ).send()

//            sendNotification()
        }


        ivPrevious.setOnClickListener {
            YoYo.with(Techniques.Landing).playOn(ivPrevious)

            currentPicture.toggleVisibility()
            currentTitle.toggleVisibility()

            currentSongIndex = (currentSongIndex + (currentSong.size - 1)) % totalSong

            togglePictureAndTitle()

            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = MediaPlayer.create(this, currentSong[currentSongIndex])
            initialiseSeekBar()
            mp?.start()
            animationDrawable.start()

            startService()
            val broadcastIntent3 = Intent(this, NotificationReceiver::class.java)
            val pendingIntent3 = PendingIntent.getBroadcast(
                this, 0, broadcastIntent3, 0
            ).send()
//            sendNotification()

        }

        ivSkip.setOnClickListener {
            YoYo.with(Techniques.Landing).playOn(ivSkip)
            currentPicture.toggleVisibility()
            currentTitle.toggleVisibility()

            currentSongIndex = (currentSongIndex + 1) % totalSong

            togglePictureAndTitle()

            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = MediaPlayer.create(this, currentSong[currentSongIndex])
            initialiseSeekBar()
            mp?.start()
            animationDrawable.start()


            //MusicBackground.setBackgroundResource(R.drawable.gradient_background_music)
            startService()
            val broadcastIntent3 = Intent(this, NotificationReceiver3::class.java)
            val pendingIntent3 = PendingIntent.getBroadcast(
                this, 0, broadcastIntent3, 0
            ).send()


//            val intent = Intent()
//            intent.setClass(this, NotificationReceiver3::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0).send()

        }




        seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, change: Boolean) {
                if(change) {
                    mp1?.seekTo(progress)
                    progressBarMusic1.progress = seekBarMusic.progress
                    progressBarMusic2.progress = seekBarMusic.progress
                    calCulateCurrentDuration(seekBarMusic.progress)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopUpdating = true
//                mp?.setVolume(0f, 0f)

                cvProgressBarMusic1.toggleVisibility()
                cvProgressBarMusic2.toggleVisibility()

                if (mp == null) {
                    mp = MediaPlayer.create(this@Music, currentSong[currentSongIndex])
                    initialiseSeekBar()
                    progressBarMusic1.progress = mp?.currentPosition!!
                    progressBarMusic2.progress = mp?.currentPosition!!
                }

                autoPlay = false

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                stopUpdating = false
                mp?.seekTo(seekBarMusic.progress)
//                mp?.setVolume(1f, 1f)
                cvProgressBarMusic1.toggleVisibility()
                cvProgressBarMusic2.toggleVisibility()
                startService()
                autoPlay = true
//                sendNotification()
//                if (mp?.isPlaying == true && seekBar?.progress == mp1?.duration) {
//                    currentPicture.toggleVisibility()
//                    currentTitle.toggleVisibility()
//
//                    currentSongIndex = (currentSongIndex + 1) % totalSong
//
//                    togglePictureAndTitle()
//
//                    mp?.stop()
//                    mp?.reset()
//                    mp?.release()
//                    mp = MediaPlayer.create(this@Music, currentSong[currentSongIndex])
//                    initialiseSeekBar()
//                    mp?.start()
//
//
//                    //MusicBackground.setBackgroundResource(R.drawable.gradient_background_music)
//                    val broadcastIntent3 = Intent(this@Music, NotificationReceiver3::class.java)
//                    val pendingIntent3 = PendingIntent.getBroadcast(
//                        this@Music, 0, broadcastIntent3, 0
//                    ).send()
//                }
            }

        })


        toggle = ActionBarDrawerToggle(this, drawerMusic, R.string.open, R.string.close)

        drawerMusic.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        downloadImage("profilePicture")

        navViewLeftMusic.setNavigationItemSelectedListener { MenuItem ->

            val toast = Toast.makeText(this, "You're currently in music", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 0)

            when (MenuItem.itemId) {
                R.id.miHome -> Intent(this, Home::class.java).also { Intent ->
                    startActivity(Intent)
                    finish()
                    mp?.stop()
                    runnable?.let { it -> handler.removeCallbacks(it) }
                    animationDrawable.stop()
                }

                R.id.miChat -> Intent(this, Chat::class.java).also { Intent ->
                    startActivity(Intent)
                    finish()
                    mp?.stop()
                    runnable?.let { it -> handler.removeCallbacks(it) }
                    animationDrawable.stop()
                }

                R.id.miGames -> Intent(this, Game::class.java).also { Intent ->
                    startActivity(Intent)
                    finish()
                    mp?.stop()
                    runnable?.let { it -> handler.removeCallbacks(it) }
                    animationDrawable.stop()
                }

                R.id.miMusic -> toast.show()

                R.id.miProfile -> Intent(this, Profile::class.java).also { Intent ->
                    startActivity(Intent)
                    finish()
                    mp?.stop()
                    runnable?.let { it -> handler.removeCallbacks(it) }
                    animationDrawable.stop()
                }

                R.id.miLogout -> Intent(this, Login::class.java).also { Intent ->
                    startActivity(Intent)
                    finish()
                    mp?.stop()
                    runnable?.let { it -> handler.removeCallbacks(it) }
                    animationDrawable.stop()
                }

            }
            true
        }


        navViewRightMusic.setNavigationItemSelectedListener { MenuItem ->

            when (MenuItem.itemId) {
                R.id.miCongratulation -> selectSong(0)

                R.id.miInTheEnd -> selectSong(1)

                R.id.miLoveMeLikeYouDo -> selectSong(2)

                R.id.miDontTurnBack -> selectSong(3)

                R.id.miYoungAndBeautiful -> selectSong(4)

                R.id.miMyHeartWillGoOn -> selectSong(5)

                R.id.miStay -> selectSong(6)

                R.id.miHoliday -> selectSong(7)



                R.id.miPhotograph -> {
                    try {
                        mp = MediaPlayer()
                        mp?.setDataSource("https://firebasestorage.googleapis.com/v0/b/myapp-7a885.appspot.com/o/songs%2FSong%201?alt=media&token=a1a8351a-2629-4164-ba1b-7f10e46f3f2d")
                        mp?.prepare()
                        mp?.start()
                        Toast.makeText(
                            this,
                            "Photograph by Ed Sheeran is playing",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@Music, e.message, Toast.LENGTH_LONG).show()
                    }

                }

                R.id.miAddSong ->
                    if (!addSongCheck) {
                        Intent(Intent.ACTION_GET_CONTENT).also {
                            it.type = "audio/*"
                            startActivityForResult(it, 1)
                        }
                    } else {
                        try {
                            mp = MediaPlayer()
                            mp?.setDataSource("https://firebasestorage.googleapis.com/v0/b/myapp-7a885.appspot.com/o/songs%2FSong2?alt=media")
                            mp?.prepare()
                            mp?.start()
                            Toast.makeText(this, "Song 2 is playing", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@Music, e.message, Toast.LENGTH_LONG).show()
                        }
                    }

            }
            animationDrawable.start()
            true
        }

        var count = 1
        ibEmptyHeart.setOnClickListener {
            if (count % 2 == 1) {
                ibRedHeart.toggleVisibility()
                YoYo.with(Techniques.Bounce).playOn(ibRedHeart)
                YoYo.with(Techniques.Bounce).playOn(ibEmptyHeart)
                ibEmptyHeart.scaleX = 1.3f
                ibEmptyHeart.scaleY = 1.3f
                val toast1 = Toast.makeText(this, "You liked the song", Toast.LENGTH_SHORT)
                toast1.setGravity(Gravity.BOTTOM, 0, 0)
                toast1.show()
            } else {
                YoYo.with(Techniques.FadeOutDown).duration(1000).playOn(ibRedHeart)
                YoYo.with(Techniques.Shake).duration(1000).playOn(ibEmptyHeart)
                ibEmptyHeart.scaleX = 1.3f
                ibEmptyHeart.scaleY = 1.3f
                val toast2 = Toast.makeText(this, "You unliked the song", Toast.LENGTH_SHORT)
                toast2.setGravity(Gravity.BOTTOM, 0, 0)
                toast2.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    ibRedHeart.toggleVisibility()
                }, 1000)
            }
            count++
        }

        ibMoreMusic.setOnClickListener {
            currentTitle.isSelected = marqueeText
            val popup = PopupMenu(this, ibMoreMusic)
            popup.menuInflater.inflate(R.menu.pop_up_music, popup.menu)
            popup.setOnMenuItemClickListener {
                currentTitle.isSelected = marqueeText
                when (it.itemId) {
                    R.id.miMusicSettings -> {
                        val view =
                            LayoutInflater.from(this).inflate(R.layout.music_popup_window, null)
                        val switchCompat1 =
                            view.findViewById<SwitchCompat>(R.id.scAutoPlayMusic)
                        val switchCompat2 = view.findViewById<SwitchCompat>(R.id.scMarqeeText)

                        switchCompat1.isChecked = autoPlay
                        switchCompat1.setOnCheckedChangeListener { _, isChecked ->
                            autoPlay = isChecked
                        }

                        currentTitle.isSelected = marqueeText
                        switchCompat2.isChecked = marqueeText
                        switchCompat2.setOnCheckedChangeListener { _, isChecked ->
                            marqueeText = isChecked
                        }

                        AlertDialog.Builder(this)
                            .setView(view)
                            .show()
                    }
                }
                true
            }
            popup.show()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (ForegroundService.pictureID == R.drawable.congratulations) {
            if (mp != null) {
                selectSong(0)
            }
        } else if (ForegroundService.pictureID == R.drawable.in_the_end) {
            selectSong(1)
        } else if (ForegroundService.pictureID == R.drawable.love_me_like_you_do) {
            selectSong(2)
        } else if (ForegroundService.pictureID == R.drawable.dont_turn_back) {
            selectSong(3)
        } else if (ForegroundService.pictureID == R.drawable.young_and_beautiful) {
            selectSong(4)
        } else if (ForegroundService.pictureID == R.drawable.my_heart_will_go_on) {
            selectSong(5)
        } else if (ForegroundService.pictureID == R.drawable.stay_image) {
            selectSong(6)
        } else if (ForegroundService.pictureID == R.drawable.holiday_image) {
            selectSong(7)
        }
    }

    override fun recreate() {
        super.recreate()
        if (ForegroundService.pictureID == R.drawable.congratulations) {

            selectSong(0)

        } else if (ForegroundService.pictureID == R.drawable.in_the_end) {
            selectSong(1)

        } else if (ForegroundService.pictureID == R.drawable.love_me_like_you_do) {
            selectSong(2)

        } else if (ForegroundService.pictureID == R.drawable.dont_turn_back) {
            selectSong(3)

        } else if (ForegroundService.pictureID == R.drawable.young_and_beautiful) {
            selectSong(4)

        } else if (ForegroundService.pictureID == R.drawable.my_heart_will_go_on) {
            selectSong(5)

        } else if (ForegroundService.pictureID == R.drawable.stay_image) {
            selectSong(6)

        } else if (ForegroundService.pictureID == R.drawable.holiday_image) {
            selectSong(7)
        }
    }

    override fun onPause() {
        super.onPause()
        if (currentTitle.isSelected) {
            currentTitle.isSelected = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentTitle.isSelected) {
            currentTitle.isSelected = true
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
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            data?.data?.let {
                curFile = it
                ivProfilePicture.setImageURI(it)
                uploadImageToStorage("profilePicture") // to upload profile picture to Firebase Cloud Storage
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            data?.data?.let {
                curFile = it
                ivPhotograph.setImageURI(curFile)
                uploadSongToStorage("Song2") // to upload song to Firebase Cloud Storage
            }
        }
    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                imageRef.child("images/$filename").putFile(it).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Music, "Successfully uploaded image", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Music, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadSongToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                songRef.child("songs/$filename").putFile(it).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Music, "Successfully uploaded song", Toast.LENGTH_LONG)
                        .show()
                    val menu: Menu = navViewRightMusic.menu
                    val addSong = menu.findItem(R.id.miAddSong)
                    addSong.title = "Song2"
                    addSong.setIcon(R.drawable.ic_note)
                    addSongCheck = true
                    totalSong++
                    title.add(3, tvSong1)
                    picture.add(3, ivPhotograph)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Music, e.message, Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@Music, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun initialiseSeekBar() {
        seekBarMusic.max = mp?.duration!!
        seekBarMusicFake.max = seekBarMusic.max
        progressBarMusic1.max = mp?.duration!!
        progressBarMusic2.max = mp?.duration!!
        calculatMaxDuration(mp?.duration!!)
            runnable = Runnable {
                if(!stopUpdating) {
                    seekBarMusic.progress = mp?.currentPosition!!
                    progressBarMusic1.progress = mp?.currentPosition!!
                    progressBarMusic2.progress = mp?.currentPosition!!
                    calCulateCurrentDuration(mp?.currentPosition!!)
                }
                runnable?.let { handler.postDelayed(it, 0) }
                if (mp?.isPlaying == true) {
                    ivPlay.setImageResource(R.drawable.ic_pause_circle)
                    currentTitle.isSelected = marqueeText
                } else {
                    ivPlay.setImageResource(R.drawable.ic_play_circle)
                }
            }
            handler.postDelayed(runnable!!, 0)

        mp?.setOnCompletionListener {
            if (autoPlay) {
                YoYo.with(Techniques.Landing).playOn(ivSkip)

                currentPicture.toggleVisibility()
                currentTitle.toggleVisibility()

                currentSongIndex = (currentSongIndex + 1) % totalSong

                togglePictureAndTitle()
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = MediaPlayer.create(this, currentSong[currentSongIndex])
                initialiseSeekBar()
                mp?.start()

                startService()
                val broadcastIntent3 = Intent(this, NotificationReceiver3::class.java)
                val pendingIntent3 = PendingIntent.getBroadcast(
                    this, 0, broadcastIntent3, FLAG_CANCEL_CURRENT
                ).send()
//                sendNotification()
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

    private fun selectSong(jumpSongIndex: Int) {
//        val toast2 = Toast.makeText(this, "Song is currently selected", Toast.LENGTH_SHORT)
//        toast2.setGravity(Gravity.TOP, 0, 0)

        if (jumpSongIndex == currentSongIndex && mp != null) {
//            toast2.show()
        } else {
            currentPicture.toggleVisibility()
            currentTitle.toggleVisibility()
            currentSongIndex = jumpSongIndex
            togglePictureAndTitle()
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = MediaPlayer.create(this, currentSong[currentSongIndex])
            initialiseSeekBar()
            mp?.start()

            startService()
//            sendNotification()
        }
    }

    private fun togglePictureAndTitle() {
        picture[currentSongIndex].toggleVisibility()
        title[currentSongIndex].toggleVisibility()
        currentPicture = picture[currentSongIndex]
        currentTitle = title[currentSongIndex]
        currentTitle.isSelected = marqueeText
    }

//    override fun onBackPressed() {
//        AlertDialog.Builder(this)
//            .setIcon(android.R.drawable.ic_dialog_alert)
//            .setTitle("Closing Activity")
//            .setMessage("Are you sure you want to close this activity?")
//            .setPositiveButton(
//                "Yes"
//            ) { dialog, which ->
//                run {
//                    runnable?.let { handler.removeCallbacks(it) }
//                    finish()
//                }
//            }
//            .setNegativeButton("No", null)
//            .show()
//    }


//    private fun createNotificationChannel() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Notification Title"
//            val descriptionText = "Notification Description"
//            val importance = NotificationManager.IMPORTANCE_LOW
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

//    private fun sendNotification() {
//        val intent = Intent(this, Music::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//        val bitmapLargeIcon =
//            BitmapFactory.decodeResource(resources, R.drawable.ic_music)
//
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_note)
//            .setContentTitle(title2[currentSongIndex])
//            .setContentText("Description")
//            .setLargeIcon(bitmapLargeIcon)
//            .setContentIntent(pendingIntent)
//            .setColor(ContextCompat.getColor(this, R.color.red))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//
//        with(NotificationManagerCompat.from(this)) {
//            notify(notificationId, builder.build())
//        }
//    }


    fun hideSoftKeyboard(view: View) {
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(actvSearchSong.windowToken, 0)
    }

    private fun calculatMaxDuration(maxDuration: Int) {
        val seconds = maxDuration / 1000
        val convertion1 = seconds % 10
        val convertion2 = (maxDuration / 10000) % 6
        val convertion3 = (maxDuration / 60000) % 10

        val maxSongDuration = "$convertion3:$convertion2$convertion1"
        tvMaxDuration.text = maxSongDuration
    }

    private fun calCulateCurrentDuration(currentDuration: Int) {
        val seconds = currentDuration / 1000
        val convertion1 = seconds % 10
        val convertion2 = (currentDuration / 10000) % 6
        val convertion3 = (currentDuration / 60000) % 10

        val minDuration = "$convertion3:$convertion2$convertion1"
        tvCurrentDuration.text = minDuration
    }


    private fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }


    fun Activity.transparentStatusAndNavigation(
        systemUiScrim: Int = Color.parseColor("#40000000") // 25% black
    ) {
        var systemUiVisibility = 0
        // Use a dark scrim by default since light status is API 23+
        var statusBarColor = systemUiScrim
        //  Use a dark scrim by default since light nav bar is API 27+
        var navigationBarColor = systemUiScrim
        val winParams = window.attributes


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            navigationBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.decorView.systemUiVisibility = systemUiVisibility
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            winParams.flags = winParams.flags or
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            winParams.flags = winParams.flags and
                    (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION).inv()
            window.statusBarColor = statusBarColor
            window.navigationBarColor = navigationBarColor
        }

        window.attributes = winParams
    }
}






package com.bignerdranch.android.myapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.chat.*
import kotlinx.android.synthetic.main.games.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.music.*
import kotlinx.android.synthetic.main.nav_left_header.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*


class Game : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private var curFile: Uri? = null
    private val imageRef = Firebase.storage.reference
    private var slashAttack = 0
    private var currentMonster = 0
    private var tapDamage = 2
    private var Crit_Chance = 5
    private var Crit_Damage = 2
    private var No_crit = 0
    private var GOLD = 0
    private var obtainSkill_2 = false
    private var obtainSkill_3 = false
    private var obtainSkill_4 = false
    private var GoldenTimeCheck = false
    private var counter = 0
    private var currentStage = 1
    private var trueDamange = false
    private var armor = 0
    private var currentEnergy = 100
    private var totalEnergy = 100
    private var energyRegen = 1
    private var skill_Damage = 1
    private var bonusGold = 0
    private lateinit var backgroundMusic: MediaPlayer
    private var tapAttackLevel = 1
    private var monsterLevel = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.games)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        backgroundMusic = MediaPlayer.create(this, R.raw.demon_slayer_op_instrumental)
        backgroundMusic.isLooping = true
        backgroundMusic.start()

        toggle = ActionBarDrawerToggle(this, drawerGames, R.string.open, R.string.close)

        drawerGames.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        downloadImage("profilePicture")

        tvCurrentEnergy.text = "$currentEnergy"
        tvTotalEnergy.text = "/${totalEnergy}"

        btnTapAttack.setOnClickListener {
            attack(slashAttack)
            slashAttack++
            goldenTimeTap()

            if (currentEnergy < totalEnergy && slashAttack % 2 == 0) {
                currentEnergy = currentEnergy + energyRegen
                tvCurrentEnergy.text = "$currentEnergy"
                pbEnergyBar.incrementProgressBy(energyRegen)
                if (currentEnergy > totalEnergy) {
                    currentEnergy = totalEnergy
                    tvCurrentEnergy.text = "$currentEnergy"
                    pbEnergyBar.progress = currentEnergy
                }
            }
            if (vvZenitsu.isPlaying) {
                vvZenitsu.toggleVisibility()
                YoYo.with(Techniques.FadeOut).duration(1).playOn(vvZenitsu)
                vvZenitsu.stopPlayback()
                for (ATTACK in slashAttack..slashAttack + 15) {
                    goldenTimeTap()
                    skill_1_attack(ATTACK)
                }
            } else if (vvSukuna.isPlaying) {
                vvSukuna.toggleVisibility()
                YoYo.with(Techniques.FadeOut).duration(1).playOn(vvSukuna)
                vvSukuna.stopPlayback()
                for (ATTACK in slashAttack..slashAttack + 15) {
                    goldenTimeTap()
                    skill_1_attack(ATTACK)
                }
            } else if (vvKugisaki.isPlaying) {
                vvKugisaki.toggleVisibility()
                YoYo.with(Techniques.FadeOut).duration(1).playOn(vvKugisaki)
                vvKugisaki.stopPlayback()
                for (ATTACK in slashAttack..slashAttack + 15) {
                    goldenTimeTap()
                    skill_1_attack(ATTACK)
                }
            }
        }


        ivMusicOn.setOnClickListener {
            backgroundMusic.start()
            ivMusicOff.toggleVisibility()
            ivMusicOn.toggleVisibility()
        }

        ivMusicOff.setOnClickListener {
            backgroundMusic.pause()
            ivMusicOn.toggleVisibility()
            ivMusicOff.toggleVisibility()
        }

        seekBarHeathBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                pbhealthBar.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBarEnergy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                pbEnergyBar.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        val uriZenitsu: Uri =
            Uri.parse("android.resource://$packageName/${R.raw.skill_1_zenitsu}")
        vvZenitsu.setVideoURI(uriZenitsu)

        ivSkill_1.setOnClickListener {
            if (currentEnergy < 25) {
                Toast.makeText(this, "Not enough Energy", Toast.LENGTH_SHORT).show()
            } else {
                vvZenitsu.start()
                YoYo.with(Techniques.FadeIn).duration(100).playOn(vvZenitsu)
                cvSkill_1.toggleVisibility()
                cvSkill_1_Border.toggleVisibility()
                vvZenitsu.toggleVisibility()
                vvZenitsu.setZOrderMediaOverlay(true)
                YoYo.with(Techniques.FadeOut).delay(10900).playOn(vvZenitsu)
                pbEnergyBar.incrementProgressBy(-25)
                tvCurrentEnergy.text = "${currentEnergy - 25}"
                currentEnergy = currentEnergy - 25

                Handler(Looper.getMainLooper()).postDelayed({
                    if (vvZenitsu.isVisible == true) {
                        vvZenitsu.toggleVisibility()
                        for (ATTACK in slashAttack..slashAttack + 15) {
                            goldenTimeTap()
                            skill_1_attack(ATTACK)
                        }
                    }
                    cvSkill_1.toggleVisibility()
                    cvSkill_1_Border.toggleVisibility()
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_1_Border)
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_1)
                }, 11000)
            }
        }

        val uriSukuna: Uri =
            Uri.parse("android.resource://$packageName/${R.raw.skill_2_sukuna_domain_expansion_video}")
        vvSukuna.setVideoURI(uriSukuna)

        ivSkill_2.setOnClickListener {
            if (currentEnergy < 25) {
                Toast.makeText(this, "Not enough energy", Toast.LENGTH_SHORT).show()
            } else {
                vvSukuna.start()
                YoYo.with(Techniques.FadeIn).duration(100).playOn(vvSukuna)
                cvSkill_2.toggleVisibility()
                cvSkill_2_Border.toggleVisibility()
                vvSukuna.toggleVisibility()
                vvSukuna.setZOrderMediaOverlay(true)
                YoYo.with(Techniques.FadeOut).delay(10900).playOn(vvSukuna)
                pbEnergyBar.incrementProgressBy(-25)
                tvCurrentEnergy.text = "${currentEnergy - 25}"
                currentEnergy = currentEnergy - 25

                Handler(Looper.getMainLooper()).postDelayed({
                    if (vvSukuna.isVisible == true) {
                        vvSukuna.toggleVisibility()
                        for (ATTACK in slashAttack..slashAttack + 15) {
                            goldenTimeTap()
                            skill_1_attack(ATTACK)
                        }
                    }
                    cvSkill_2.toggleVisibility()
                    cvSkill_2_Border.toggleVisibility()
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_2_Border)
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_2)
                }, 11000)

            }
        }


        val uriGoldenTime: Uri =
            Uri.parse("android.resource://$packageName/${R.raw.skill_3_golden_time}")
        vvGoldenTime.setVideoURI(uriGoldenTime)

        ivSkill_3.setOnClickListener {
            if (currentEnergy < 25) {
                Toast.makeText(this, "Not enough energy", Toast.LENGTH_SHORT).show()
            } else {
                vvGoldenTime.start()
                YoYo.with(Techniques.FadeIn).duration(100).playOn(vvGoldenTime)
                cvSkill_3.toggleVisibility()
                cvSkill_3_Border.toggleVisibility()
                vvGoldenTime.toggleVisibility()
                pbEnergyBar.incrementProgressBy(-25)
                tvCurrentEnergy.text = "${currentEnergy - 25}"
                currentEnergy = currentEnergy - 25
                GoldenTimeCheck = true
                YoYo.with(Techniques.FadeOut).delay(12000).playOn(vvGoldenTime)
                Handler(Looper.getMainLooper()).postDelayed({
                    cvSkill_3.toggleVisibility()
                    cvSkill_3_Border.toggleVisibility()
                    vvGoldenTime.toggleVisibility()
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_3_Border)
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_3)
                    GoldenTimeCheck = false
                }, 12100)
            }
        }

        val uriKugisaki: Uri =
            Uri.parse("android.resource://$packageName/${R.raw.skill_4_piercing_nail}")
        vvKugisaki.setVideoURI(uriKugisaki)

        ivSkill_4.setOnClickListener {
            if (currentEnergy < 25) {
                Toast.makeText(this, "Not enough energy", Toast.LENGTH_SHORT).show()
            } else {
                vvKugisaki.start()
                YoYo.with(Techniques.FadeIn).duration(100).playOn(vvKugisaki)
                cvSkill_4.toggleVisibility()
                cvSkill_4_Border.toggleVisibility()
                vvKugisaki.toggleVisibility()
                vvKugisaki.setZOrderMediaOverlay(true)
                pbEnergyBar.incrementProgressBy(-25)
                tvCurrentEnergy.text = "${currentEnergy - 25}"
                currentEnergy = currentEnergy - 25
                trueDamange = true
                YoYo.with(Techniques.FadeOut).delay(4900).playOn(vvKugisaki)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (vvKugisaki.isVisible == true) {
                        vvKugisaki.toggleVisibility()
                        for (ATTACK in slashAttack..slashAttack + 15) {
                            goldenTimeTap()
                            skill_1_attack(ATTACK)
                        }
                    }
                }, 5000)
                Handler(Looper.getMainLooper()).postDelayed({
                    cvSkill_4.toggleVisibility()
                    cvSkill_4_Border.toggleVisibility()
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_4_Border)
                    YoYo.with(Techniques.FadeIn).playOn(cvSkill_4)
                    trueDamange = false
                }, 15000)
            }
        }


        navViewLeftProfile.setNavigationItemSelectedListener {

            val toast = Toast.makeText(this, "You're currently in Games", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 0)

            when (it.itemId) {
                R.id.miHome -> Intent(this, Home::class.java).also {
                    startActivity(it)
                    finish()
                    backgroundMusic.stop()
                }

                R.id.miChat -> Intent(this, Chat::class.java).also {
                    startActivity(it)
                    finish()
                    backgroundMusic.stop()
                }

                R.id.miGames -> toast.show()

                R.id.miMusic -> Intent(this, Music::class.java).also {
                    startActivity(it)
                    finish()
                    backgroundMusic.stop()
                }

                R.id.miProfile -> Intent(this, Profile::class.java).also {
                    startActivity(it)
                    finish()
                    backgroundMusic.stop()
                }

                R.id.miLogout -> Intent(this, Login::class.java).also {
                    startActivity(it)
                    finish()
                    backgroundMusic.stop()
                }
            }
            true
        }


        navViewRightGames.setNavigationItemSelectedListener {
            val toast1 = Toast.makeText(this, "Skill is owned", Toast.LENGTH_SHORT)
            toast1.setGravity(Gravity.TOP, 0, 0)
            val insufficientGold = Toast.makeText(this, "Not enough gold", Toast.LENGTH_SHORT)
            insufficientGold.setGravity(Gravity.TOP, 0, 0)
            val toast3 = Toast.makeText(this, "New skill obtained", Toast.LENGTH_SHORT)
            toast3.setGravity(Gravity.TOP, 0, 0)

            when (it.itemId) {
                R.id.miSkill_2 -> {
                    if (GOLD >= 2500 && obtainSkill_2 == false) {
                        cvSkill_2_Border.toggleVisibility()
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                        obtainSkill_2 = true
                        toast3.show()
                    } else if (GOLD < 2500 && obtainSkill_2 == false) {
                        insufficientGold.show()
                    } else {
                        toast1.show()
                    }
                }

                R.id.miSkill_3 -> {
                    if (GOLD >= 2500 && obtainSkill_3 == false) {
                        cvSkill_3_Border.toggleVisibility()
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                        obtainSkill_3 = true
                        toast3.show()
                    } else if (GOLD < 2500 && obtainSkill_3 == false) {
                        insufficientGold.show()
                    } else {
                        toast1.show()
                    }
                }

                R.id.miSkill_4 -> {
                    if (GOLD >= 2500 && obtainSkill_4 == false) {
                        cvSkill_4_Border.toggleVisibility()
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                        obtainSkill_4 = true
                        toast3.show()
                    } else if (GOLD < 2500 && obtainSkill_4 == false) {
                        insufficientGold.show()
                    } else {
                        toast1.show()
                    }
                }

                R.id.miTapAttack -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        tapDamage = tapDamage + (tapDamage * 2 - tapDamage / 2)
                        val menu: Menu = navViewRightGames.menu
                        val tap = menu.findItem(R.id.miTapAttack)
                        tapAttackLevel++
                        tap.title = "Tap Attack lv ${tapAttackLevel}"
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miTapEnergyRegen -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        energyRegen = energyRegen + 1
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miTotalEnergy -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        tvTotalEnergy.text = "/ ${totalEnergy + 20}"
                        totalEnergy = totalEnergy + 20
                        tvCurrentEnergy.text = "$totalEnergy"
                        pbEnergyBar.max = totalEnergy
                        currentEnergy = totalEnergy
                        pbEnergyBar.progress = totalEnergy
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miCritChance -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        Crit_Chance = Crit_Chance + 5
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miCritDamage -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        Crit_Damage = Crit_Damage + 5
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miSkillDamage -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        skill_Damage = skill_Damage * 2
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miBonusGold -> {
                    if (GOLD >= 2500) {
                        tvCoins.text = "${GOLD - 2500}"
                        GOLD = GOLD - 2500
                        bonusGold = bonusGold + 1000
                        YoYo.with(Techniques.Shake).playOn(tvCoins)
                    } else {
                        insufficientGold.show()
                    }
                }

                R.id.miIncreaseArmor -> {
                    armor = armor + 500
                    tvArmor.text = "$armor"
                }

                R.id.miGreedisGood -> {
                    tvCoins.text = "${GOLD + 50000}"
                    GOLD = GOLD + 50000
                    YoYo.with(Techniques.Pulse).playOn(tvCoins)
                }
            }
            true
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
    }


    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                imageRef.child("images/$filename").putFile(it).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Game, "Successfully uploaded image", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Game, e.message, Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@Game, e.message, Toast.LENGTH_LONG).show()
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

    private fun attack(ATTACK: Int) {
        if (ATTACK % 6 == 0) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage + armor)
                if (tapDamage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                if (tapDamage * Crit_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage)
            }
            slashToggle(ATTACK)
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash1)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash1)
            YoYo.with(Techniques.TakingOff).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Wobble).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 1) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage + armor)
                if (tapDamage < armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                if (tapDamage * Crit_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage)
            }
            slashToggle(ATTACK)
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash2)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash2)
            YoYo.with(Techniques.Landing).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Swing).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 2) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage + armor)
                if (tapDamage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                if (tapDamage * Crit_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage)
            }
            slashToggle(ATTACK)
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash3)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash3)
            YoYo.with(Techniques.Tada).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Wave).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 3) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage + armor)
                if (tapDamage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                if (tapDamage * Crit_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage)
            }
            slashToggle(ATTACK)
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash4)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash4)
            YoYo.with(Techniques.Shake).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.ZoomOutUp).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 4) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage + armor)
                if (tapDamage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                if (tapDamage * Crit_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage)
            }
            slashToggle(ATTACK)
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash5)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash5)
            YoYo.with(Techniques.Tada).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Wave).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 5 && trueDamange == false) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage + armor)
                if (tapDamage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                if (tapDamage * Crit_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage)
            }
            slashToggle(ATTACK)
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash6)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash6)
            YoYo.with(Techniques.Shake).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.ZoomOutUp).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }
    }

    private fun slashToggle(ATTACK: Int) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (ATTACK % 6 == 0) {
                    ivSlash1.toggleVisibility()
                } else if (ATTACK % 6 == 1) {
                    ivSlash2.toggleVisibility()
                } else if (ATTACK % 6 == 2) {
                    ivSlash3.toggleVisibility()
                } else if (ATTACK % 6 == 3) {
                    ivSlash4.toggleVisibility()
                } else if (ATTACK % 6 == 4) {
                    ivSlash5.toggleVisibility()
                } else {
                    ivSlash6.toggleVisibility()
                }
            }, 500
        )
    }

    private fun skill_1_attack(ATTACK: Int) {
        if (ATTACK % 6 == 0) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                if (tapDamage * skill_Damage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                if (tapDamage * Crit_Damage * skill_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * skill_Damage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage)
            }
            ivSlash1.toggleVisibility()
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash1)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash1)
            YoYo.with(Techniques.TakingOff).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Wobble).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 1) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                if (tapDamage * skill_Damage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                if (tapDamage * Crit_Damage * skill_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * skill_Damage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage)
            }
            ivSlash2.toggleVisibility()
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash2)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash2)
            YoYo.with(Techniques.Landing).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Swing).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 2) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                if (tapDamage * skill_Damage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                if (tapDamage * Crit_Damage * skill_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * skill_Damage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage)
            }
            ivSlash3.toggleVisibility()
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash3)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash3)
            YoYo.with(Techniques.Tada).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Wave).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 3) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //vDamageText.text = "${tapDamage * skill_Damage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                if (tapDamage * skill_Damage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                if (tapDamage * Crit_Damage * skill_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * skill_Damage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage)
            }
            ivSlash4.toggleVisibility()
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash4)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash4)
            YoYo.with(Techniques.Shake).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.ZoomOutUp).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 4) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                // pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                if (tapDamage * skill_Damage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                if (tapDamage * Crit_Damage * skill_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * skill_Damage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage)
            }
            ivSlash5.toggleVisibility()
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash5)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash5)
            YoYo.with(Techniques.Tada).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.Wave).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }

        if (ATTACK % 6 == 5) {
            No_crit = kotlin.random.Random.nextInt(1, 100)
            YoYo.with(Techniques.Shake).delay(50).playOn(cvMonsters)
            if (Crit_Chance < No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.red))
                //tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                //pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                if (tapDamage * skill_Damage <= armor) {
                    tvDamageText.text = "0"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * skill_Damage - armor}"
                    pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage + armor)
                }
            } else if (Crit_Chance >= No_crit && trueDamange == false) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                //tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                //pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                if (tapDamage * Crit_Damage * skill_Damage <= armor) {
                    tvDamageText.text = "0 crit"
                    pbhealthBar.incrementProgressBy(0)
                } else {
                    tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage - armor} crit"
                    pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage + armor)
                }
            } else if (Crit_Chance < No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.NORMAL)
                tvDamageText.textSize = 30f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * skill_Damage} true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * skill_Damage)
            } else if (Crit_Chance >= No_crit && trueDamange == true) {
                tvDamageText.setTypeface(null, Typeface.BOLD)
                tvDamageText.textSize = 35f
                tvDamageText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvDamageText.text = "${tapDamage * Crit_Damage * skill_Damage} crit true DMG"
                pbhealthBar.incrementProgressBy(-tapDamage * Crit_Damage * skill_Damage)
            }
            ivSlash6.toggleVisibility()
            YoYo.with(Techniques.FadeIn).delay(50).playOn(ivSlash6)
            YoYo.with(Techniques.FadeOut).delay(100).playOn(ivSlash6)
            YoYo.with(Techniques.Shake).duration(1000).playOn(tvDamageText)
            YoYo.with(Techniques.ZoomOutUp).delay(200).duration(2000).playOn(tvDamageText)
            YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000).playOn(tvDamageText)
            MediaPlayer.create(this, R.raw.slash).start()
            MediaPlayer.create(this, R.raw.slash).reset()
            MediaPlayer.create(this, R.raw.slash).release()
            slashToggle(ATTACK)
            killCheck()
        }
    }

    private fun killCheck() {
        if (pbhealthBar.progress == 0) {
            if (currentMonster % 3 == 0) {
                ivHandDemon.toggleVisibility()
                ivDeepSeaKing.toggleVisibility()

                tvHandDemon.toggleVisibility()
                tvDeepSeaKing.toggleVisibility()
            } else if (currentMonster % 3 == 1) {
                ivDeepSeaKing.toggleVisibility()
                ivRhino.toggleVisibility()

                tvDeepSeaKing.toggleVisibility()
                tvRhino.toggleVisibility()
            } else {
                ivRhino.toggleVisibility()
                ivHandDemon.toggleVisibility()

                tvRhino.toggleVisibility()
                tvHandDemon.toggleVisibility()
            }

            if (currentStage % 10 == 0) {
                var randomArmor = kotlin.random.Random.nextInt(1, 9)
                armor = armor + randomArmor
                tvArmor.text = "$armor"
            }

            monsterLevel++
            tvHandDemon.text = "Hand Demon Lv ${monsterLevel}"
            tvRhino.text = "Rhino Lv ${monsterLevel}"
            tvDeepSeaKing.text = "DeepSeaKing Lv ${monsterLevel}"

            currentStage++

            pbhealthBar.max = pbhealthBar.max + currentStage + 2 * 2
            pbhealthBar.progress = pbhealthBar.max
            currentMonster++
            tvStageNumber.text = "Stage ${currentMonster + 1}"

            GOLD = GOLD + currentMonster * 2 + Random().nextInt(1000) + bonusGold
            tvCoins.text = "${GOLD}"
            YoYo.with(Techniques.Landing).delay(50).playOn(ivCoins)
        }
    }

    private fun goldenTimeTap() {
        if (GoldenTimeCheck == true) {
            GOLD = GOLD + 100
            tvCoins.text = "${GOLD}"
            YoYo.with(Techniques.Shake).playOn(tvCoins)
            tvGoldenTimeCoin.text = "+ 100"
            if (counter % 6 == 0) {
                YoYo.with(Techniques.TakingOff).duration(1000).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.Wobble).delay(200).duration(200).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.SlideOutUp).delay(250).duration(100)
                    .playOn(tvGoldenTimeCoin)
            } else if (counter % 6 == 1) {
                YoYo.with(Techniques.Landing).duration(100).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.Swing).delay(200).duration(200).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.SlideOutUp).delay(250).duration(100)
                    .playOn(tvGoldenTimeCoin)
            } else if (counter % 6 == 2) {
                YoYo.with(Techniques.Tada).duration(1000).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.Wave).delay(200).duration(200).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.SlideOutUp).delay(250).duration(100)
                    .playOn(tvGoldenTimeCoin)
            } else if (counter % 6 == 3) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.ZoomOutUp).delay(200).duration(200)
                    .playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.SlideOutUp).delay(250).duration(100)
                    .playOn(tvGoldenTimeCoin)
            } else if (counter % 6 == 4) {
                YoYo.with(Techniques.Tada).duration(1000).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.Wave).delay(200).duration(2000).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.SlideOutUp).delay(250).duration(1000)
                    .playOn(tvGoldenTimeCoin)
            } else if (counter % 6 == 5) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.ZoomOutUp).delay(200).duration(200)
                    .playOn(tvGoldenTimeCoin)
                YoYo.with(Techniques.SlideOutUp).delay(250).duration(100)
                    .playOn(tvGoldenTimeCoin)
            }
            counter++
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to close this activity?")
            .setPositiveButton(
                "Yes"
            ) { dialog, which ->
                run {
                    backgroundMusic.stop()
                    finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}

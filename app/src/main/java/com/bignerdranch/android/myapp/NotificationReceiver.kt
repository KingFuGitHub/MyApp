package com.bignerdranch.android.myapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bignerdranch.android.myapp.ForegroundService.Companion.contentText
import com.bignerdranch.android.myapp.ForegroundService.Companion.contentTitle
import com.bignerdranch.android.myapp.ForegroundService.Companion.numID
import com.bignerdranch.android.myapp.ForegroundService.Companion.pictureID
import com.bignerdranch.android.myapp.Music.Companion.mp

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (pictureID == R.drawable.congratulations) {
            pictureID = R.drawable.holiday_image
            contentTitle = "Holiday"
            contentText = "by KSI"
        } else if (pictureID == R.drawable.holiday_image) {
            pictureID = R.drawable.stay_image
            contentTitle = "Stay"
            contentText = "by The Kid LAROI"
        } else if (pictureID == R.drawable.stay_image) {
            pictureID = R.drawable.my_heart_will_go_on
            contentTitle = "My Heart Will Go On"
            contentText = "by Celine Dion"
        } else if (pictureID == R.drawable.my_heart_will_go_on) {
            pictureID = R.drawable.young_and_beautiful
            contentTitle = "Young and Beautiful"
            contentText = "by Lana Del Rey"
        } else if (pictureID == R.drawable.young_and_beautiful) {
            pictureID = R.drawable.dont_turn_back
            contentTitle = "Don't Turn Back"
            contentText = "by Silent Partner"
        } else if (pictureID == R.drawable.dont_turn_back) {
            pictureID = R.drawable.love_me_like_you_do
            contentTitle = "Love Me Like You Do"
            contentText = "by Ellie Goulding"
        } else if (pictureID == R.drawable.love_me_like_you_do) {
            pictureID = R.drawable.in_the_end
            contentTitle = "In The End"
            contentText = "by Linkin Park"
        } else if (pictureID == R.drawable.in_the_end) {
            pictureID = R.drawable.congratulations
            contentTitle = "Congratulations"
            contentText = "by PewDiePie"
        }
        val serviceIntent = Intent(context, ForegroundService::class.java)
        context?.startForegroundService(serviceIntent)
    }
}

class NotificationReceiver2 : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (mp?.isPlaying == true) {
            numID = R.drawable.ic_play
            mp?.pause()
        } else {
            numID = R.drawable.ic_pause
            mp?.start()
        }
        val serviceIntent = Intent(context, ForegroundService::class.java)
        context?.startForegroundService(serviceIntent)
    }
}

class NotificationReceiver3 : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (pictureID == R.drawable.congratulations) {
            pictureID = R.drawable.in_the_end
            contentTitle = "In The End"
            contentText = "by Linkin Park"
        } else if (pictureID == R.drawable.in_the_end) {
            pictureID = R.drawable.love_me_like_you_do
            contentTitle = "Love Me Like You Do"
            contentText = "by Elli Goulding"
        } else if (pictureID == R.drawable.love_me_like_you_do) {
            pictureID = R.drawable.dont_turn_back
            contentTitle = "Don't Turn Back"
            contentText = "by Silent Partner"
        } else if (pictureID == R.drawable.dont_turn_back) {
            pictureID = R.drawable.young_and_beautiful
            contentTitle = "Young and Beautiful"
            contentText = "by Lana Del Rey"
        } else if (pictureID == R.drawable.young_and_beautiful) {
            pictureID = R.drawable.my_heart_will_go_on
            contentTitle = "My Heart Will Go On"
            contentText = "by Celine Dion"
        } else if (pictureID == R.drawable.my_heart_will_go_on) {
            pictureID = R.drawable.stay_image
            contentTitle = "Stay"
            contentText = "by The Kid LAROI"
        } else if (pictureID == R.drawable.stay_image) {
            pictureID = R.drawable.holiday_image
            contentTitle = "Holiday"
            contentText = "by KSI"
        } else if (pictureID == R.drawable.holiday_image) {
            pictureID = R.drawable.congratulations
            contentTitle = "Congratulations"
            contentText = "by PewDiePie"
        }
        val serviceIntent = Intent(context, ForegroundService::class.java)
        context?.startForegroundService(serviceIntent)
    }

}

class NotificationReceiver4 : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, ForegroundService::class.java)
        context?.stopService(serviceIntent)
        if (mp?.isPlaying == true) {
            mp?.stop()
        }
    }
}
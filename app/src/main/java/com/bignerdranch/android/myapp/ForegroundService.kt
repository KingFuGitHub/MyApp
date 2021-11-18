package com.bignerdranch.android.myapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val NOTIFICATION_ID = 1

    companion object {
        var numID = R.drawable.ic_pause
        var pictureID = R.drawable.congratulations
        var contentTitle = "Congratulations"
        var contentText = "by PewDiePie"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        createNotificationChannel()
        val notificationIntent = Intent(this, Music::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )


        val broadcastIntent1 = Intent(this, NotificationReceiver::class.java)
        val pendingIntent1 = PendingIntent.getBroadcast(
            this,
            0, broadcastIntent1, FLAG_CANCEL_CURRENT
        )

        val broadcastIntent2 = Intent(this, NotificationReceiver2::class.java)
        val pendingIntent2 = PendingIntent.getBroadcast(
            this, 0, broadcastIntent2, FLAG_CANCEL_CURRENT
        )

        val broadcastIntent3 = Intent(this, NotificationReceiver3::class.java)
        val pendingIntent3 = PendingIntent.getBroadcast(
            this, 0, broadcastIntent3, FLAG_CANCEL_CURRENT
        )

        val broadcastIntent4 = Intent(this, NotificationReceiver4::class.java)
        val pendingIntent4 = PendingIntent.getBroadcast(
            this, 0, broadcastIntent4, FLAG_CANCEL_CURRENT
        )


        val mediaSession = MediaSessionCompat(this, "MediaNotification")
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder().build()
        )


        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, pictureID)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setLargeIcon(largeIcon)
            .setContentText(contentText)
            .addAction(R.drawable.ic_previous, "Previous", pendingIntent1)
            .addAction(numID, "Pause", pendingIntent2)
            .addAction(R.drawable.ic_skip, "Skip", pendingIntent3)
            .addAction(R.drawable.ic_close, "close", pendingIntent4)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .setColor(Color.RED)
            .build()
        startForeground(NOTIFICATION_ID, notification)

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY
    }


    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_LOW
        )
        val manager: NotificationManager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}



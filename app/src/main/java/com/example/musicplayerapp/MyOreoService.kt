package com.example.musicplayerapp

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicplayerapp.data.Constants

class MyOreoService : Service() {

    val TAG = "MyTag"
    companion object { val MUSIC_COMPLETE = "MusicComplete" }
    val mBinder: Binder = MyServiceBinder()
    lateinit var mPlayer: MediaPlayer

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        super.onCreate()
        mPlayer = MediaPlayer.create(this, R.raw.youngasthemorning)
        mPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            val intent = Intent(MyOreoService.MUSIC_COMPLETE)
            intent.putExtra(FirstFragment.MESSAGE_KEY, "done")
            LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent)
            stopForeground(true)
            stopSelf()
        })
    }

    inner class MyServiceBinder : Binder() {
        var service: MyOreoService = this@MyOreoService
            get() {
                return field
            }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.MUSIC_SERVICE_ACTION_PLAY -> {
                Log.d(TAG, "onStartCommand: play called")
                play()
            }
            Constants.MUSIC_SERVICE_ACTION_PAUSE -> {
                Log.d(TAG, "onStartCommand: pause called")
                pause()
            }
            Constants.MUSIC_SERVICE_ACTION_STOP -> {
                Log.d(TAG, "onStartCommand: stop called")
                val intent = Intent(MyOreoService.MUSIC_COMPLETE)
                intent.putExtra(FirstFragment.MESSAGE_KEY, "done")
                LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent)
                //mPlayer.reset()
                stopForeground(true)
                stopSelf()
            }
            Constants.MUSIC_SERVICE_ACTION_START -> {
                Log.d(TAG, "onStartCommand: start called")
                //showNotification()
            }
            else -> {
            }
        }
        Log.d(TAG, "onStartCommand: ")
        return Service.START_NOT_STICKY
    }

    open fun getNotification(): Notification? {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
        builder.setContentTitle("Foreground Service Notification")
            .setContentText("This is example notification")
            .setSmallIcon(R.mipmap.ic_launcher)
        return builder.build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // TODO: Return the communication channel to the service.
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Called")
        mPlayer.release()
    }

    fun isPlaying(): Boolean {
        return mPlayer.isPlaying
    }

    fun play() {
        mPlayer.start()
    }

    /*fun start() {
        mPlayer.prepare()
        mPlayer.start()
    }*/

    fun pause() {
        mPlayer.pause()
    }
}

package com.example.musicplayerapp

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicplayerapp.data.Constants
import kotlin.reflect.KClass

class MusicPlayerService : Service() {

    val TAG = "MyTag"
    companion object { val MUSIC_COMPLETE = "MusicComplete" }
    val mBinder: Binder = MyServiceBinder()
    lateinit var mPlayer: MediaPlayer


    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        super.onCreate()
        mPlayer = MediaPlayer.create(this, R.raw.youngasthemorning)
        mPlayer.setOnCompletionListener(OnCompletionListener {
            val intent = Intent(MUSIC_COMPLETE)
            intent.putExtra(FirstFragment.MESSAGE_KEY, "done")
            LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent)
            stopForeground(true)
            stopSelf()
        })
    }


    inner class MyServiceBinder : Binder() {
        var service: MusicPlayerService = this@MusicPlayerService
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
                val intent = Intent(MUSIC_COMPLETE)
                intent.putExtra(FirstFragment.MESSAGE_KEY, "done")
                LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent)
                //mPlayer.reset()
                stopForeground(true)
                stopSelf()
            }
            Constants.MUSIC_SERVICE_ACTION_START -> {
                Log.d(TAG, "onStartCommand: start called")
                showNotification()
            }
            else -> {
            }
        }
        Log.d(TAG, "onStartCommand: ")
        return Service.START_NOT_STICKY
    }

    open fun showNotification() {
        val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)

        //Intent for play button
        val pIntent = Intent(this, MusicPlayerService::class.java)
        pIntent.action = Constants.MUSIC_SERVICE_ACTION_PLAY
        val playIntent = PendingIntent.getService(this, 100, pIntent, 0)

        //Intent for pause button
        val psIntent = Intent(this, MusicPlayerService::class.java)
        psIntent.action = Constants.MUSIC_SERVICE_ACTION_PAUSE
        val pauseIntent = PendingIntent.getService(this, 100, psIntent, 0)

        //Intent for stop button
        val sIntent = Intent(this, MusicPlayerService::class.java)
        sIntent.action = Constants.MUSIC_SERVICE_ACTION_STOP
        val stopIntent = PendingIntent.getService(this, 100, sIntent, 0)
        builder.setContentTitle("Music Player")
            .setContentText("This is demo music player")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Play",
                    playIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    pauseIntent
                )
            )
           /* .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_lock_power_off,
                    "Stop",
                    stopIntent
                )
            )*/
        startForeground(123, builder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: ")
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: ")
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind: ")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
        mPlayer.release()
    }

    //public client methods

    //public client methods
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

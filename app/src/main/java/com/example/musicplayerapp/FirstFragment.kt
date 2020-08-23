package com.example.musicplayerapp

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicplayerapp.FirstFragment.Companion.MESSAGE_KEY
import com.example.musicplayerapp.data.Constants

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    lateinit var mContext: Context;
    companion object { val MESSAGE_KEY = "message_key"}
    private lateinit var mMusicPlayerService: MusicPlayerService
    private var mBound = false
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onStart() {
        super.onStart()
        //Log.d(MainActivity.TAG, "onStart: called")
        val intent = Intent(mContext, MusicPlayerService::class.java)
        mContext.bindService(intent, mServiceCon, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(mContext.getApplicationContext())
            .registerReceiver(mReceiver, IntentFilter(MusicPlayerService.MUSIC_COMPLETE))
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            mContext.unbindService(mServiceCon)
            mBound = false
        }
        LocalBroadcastManager.getInstance(mContext.applicationContext)
            .unregisterReceiver(mReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPlayMusic.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            if (mBound) {
                if (mMusicPlayerService.isPlaying()) {
                    mMusicPlayerService.pause()
                    btnPlayMusic.setText("Play")
                } else {
                    //Log.d(MainActivity.TAG, "onBtnMusicClicked: called")
                    val intent = Intent(mContext, MusicPlayerService::class.java)
                    intent.action = Constants.MUSIC_SERVICE_ACTION_START
                    ContextCompat.startForegroundService(mContext, intent)
                    mMusicPlayerService.play()
                    btnPlayMusic.setText("Pause")
                }
            }
        }
    }

    private val mServiceCon: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, iBinder: IBinder) {
            val myServiceBinder: MusicPlayerService.MyServiceBinder = iBinder as MusicPlayerService.MyServiceBinder
            mMusicPlayerService = myServiceBinder.service
            mBound = true
            //Log.d(MainActivity.TAG, "onServiceConnected")
            if (mMusicPlayerService.isPlaying()) {
                btnPlayMusic.setText("Pause")
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            //Log.d(MainActivity.TAG, "onServiceDisconnected")
            mBound = false
        }
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            String songName=intent.getStringExtra(MESSAGE_KEY);
            val result = intent.getStringExtra(MESSAGE_KEY)
            if (result === "done") btnPlayMusic.setText("Play")

            //log(songName+" Downloaded...");
            /*Log.d(
                MainActivity.TAG,
                "onReceive: Thread name: " + Thread.currentThread().name
            )*/
        }
    }
}

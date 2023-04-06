package com.example.mp3servicemessengerpro

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log

class MyMessengerService : Service() {

    // 음악 재생을 위한 클래스(MediaPlayer)
    lateinit var mediaPlayer: MediaPlayer
    // 메시지를 전달(송, 수신)
    lateinit var receiveMessenger: Messenger
    lateinit var replyMessenger : Messenger
    // 1. 음악 객체 생성
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }
    // 2-2. IBinder 전달
    override fun onBind(intent: Intent): IBinder {
        receiveMessenger = Messenger(IncommingHandler(this))
        return receiveMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
    // 3. 음악 객체 해제
    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.release()

    }
    // 2-1. ReceiveMessenger Handler
    inner class IncommingHandler(context : Context, private val applicationContext: Context = context.applicationContext) : Handler(
        Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                // 송신자에게 전송할 메신저를 받아오고, 노래의 총 시간을 보내주고, 노래를 재생
                10 -> {
                    // 송신자에게 응답을 해 줄 메신저를 받음
                    replyMessenger = msg.replyTo
                    // 노래 총 시간을 보냄
                    if(!mediaPlayer.isPlaying){
                        mediaPlayer = MediaPlayer.create(this@MyMessengerService, R.raw.deep_free)
                    }
                    val message = Message()
                    message.what = 10
                    val bundle = Bundle()
                    bundle.putInt("duration", mediaPlayer.duration)
                    message.obj = bundle
                    replyMessenger.send(message)
                    // 노래를 재생
                    mediaPlayer.start()
                }
                // 노래를 종료
                20 -> {
                    if(mediaPlayer.isPlaying){
                        mediaPlayer.stop()
                    }
                }
                // 이외의 것을 방어
                else -> {
                    Log.e("MyMessengerService", " 약속된 프로토콜이 아닙니다. ${msg.what}")
                }
            }
        }
    }
}
package com.example.incomingdocumentprocessingsystem.utilits

import android.media.MediaPlayer
import com.example.incomingdocumentprocessingsystem.database.REF_DATABASE_ROOT
import com.example.incomingdocumentprocessingsystem.database.REF_STORAGE_ROOT
import com.example.incomingdocumentprocessingsystem.database.getFileFromStorage
import java.io.File
import java.lang.Exception

class AppVoicePlayer {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        file = File(APP_ACTIVITY.filesDir, messageKey)
        if (file.exists() && file.length() > 0 && file.isFile) {
            starPlay() {
                function()
            }
        } else{
            file.createNewFile()
            getFileFromStorage(file,fileUrl){
                starPlay() {
                    function()
                }
            }

        }
    }



    private fun starPlay(function: () -> Unit) {
        try {
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                stop {
                    function()
                }
            }

        } catch (e: Exception) {
            showToast(e.message.toString())
        }

    }

     fun stop(function: () -> Unit) {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
            function()
        }

    }

    fun release() {
        mediaPlayer.release()
    }
    fun init(){
        mediaPlayer = MediaPlayer()
    }
}
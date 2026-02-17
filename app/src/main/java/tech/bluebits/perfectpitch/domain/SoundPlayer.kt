package tech.bluebits.perfectpitch.domain

import android.content.Context
import android.media.MediaPlayer
import tech.bluebits.perfectpitch.presentation.game.MusicalNote

class SoundPlayer(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    
    fun playNote(note: MusicalNote) {
        try {
            mediaPlayer?.release()
            
            val resourceId = context.resources.getIdentifier(
                note.resourceName, 
                "raw", 
                context.packageName
            )
            
            if (resourceId != 0) {
                mediaPlayer = MediaPlayer.create(context, resourceId)
                mediaPlayer?.start()
                
                mediaPlayer?.setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

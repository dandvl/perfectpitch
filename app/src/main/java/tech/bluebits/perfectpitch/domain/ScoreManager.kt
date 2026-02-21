package tech.bluebits.perfectpitch.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ScoreManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("PerfectPitchPrefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_BEST_SCORE = "best_score"
    }
    
    fun getBestScore(): Int {
        return sharedPreferences.getInt(KEY_BEST_SCORE, 0)
    }
    
    fun saveBestScore(score: Int) {
        val currentBest = getBestScore()
        if (score > currentBest) {
            sharedPreferences.edit {
                putInt(KEY_BEST_SCORE, score)
            }
        }
    }
}

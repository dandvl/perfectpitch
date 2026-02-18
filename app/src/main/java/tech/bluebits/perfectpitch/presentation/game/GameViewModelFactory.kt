package tech.bluebits.perfectpitch.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.bluebits.perfectpitch.domain.SoundPlayer
import tech.bluebits.perfectpitch.domain.ScoreManager

class GameViewModelFactory(
    private val soundPlayer: SoundPlayer,
    private val scoreManager: ScoreManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(soundPlayer, scoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

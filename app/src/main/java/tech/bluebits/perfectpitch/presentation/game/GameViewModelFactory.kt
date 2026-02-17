package tech.bluebits.perfectpitch.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.bluebits.perfectpitch.domain.SoundPlayer

class GameViewModelFactory(
    private val soundPlayer: SoundPlayer
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(soundPlayer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

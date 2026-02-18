package tech.bluebits.perfectpitch.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.bluebits.perfectpitch.domain.SoundPlayer
import tech.bluebits.perfectpitch.domain.ScoreManager

class GameViewModel(
    private val soundPlayer: SoundPlayer,
    private val scoreManager: ScoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(GameState(bestScore = scoreManager.getBestScore()))
    val state: StateFlow<GameState> = _state.asStateFlow()
    
    fun handleIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.PlaySound -> playRandomNote()
            is GameIntent.SelectNote -> checkAnswer(intent.note)
            is GameIntent.ResetGame -> resetGame()
            is GameIntent.DismissFeedback -> dismissFeedback()
            is GameIntent.PlayAgain -> playAgain()
        }
    }
    
    private fun playRandomNote() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val allNotes = MusicalNote.values()
            val randomNote = allNotes.random()
            
            val otherNotes = allNotes.filter { it != randomNote }.shuffled().take(2)
            val options = (listOf(randomNote) + otherNotes).shuffled()
            
            _state.value = _state.value.copy(
                currentNote = randomNote,
                options = options,
                isPlaying = true,
                feedback = null,
                isLoading = false
            )
            
            soundPlayer.playNote(randomNote)
        }
    }
    
    private fun checkAnswer(selectedNote: MusicalNote) {
        val currentState = _state.value
        val isCorrect = selectedNote == currentState.currentNote
        
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        val newTotalAttempts = currentState.totalAttempts + 1
        
        val feedback = if (isCorrect) {
            "Correct! The note was ${currentState.currentNote.displayName}"
        } else {
            "Wrong! The note was ${currentState.currentNote?.displayName}"
        }
        
        val isGameOver = newTotalAttempts >= 10
        
        if (isGameOver) {
            scoreManager.saveBestScore(newScore)
        }
        
        _state.value = currentState.copy(
            score = newScore,
            totalAttempts = newTotalAttempts,
            feedback = feedback,
            isPlaying = false,
            isGameOver = isGameOver,
            bestScore = if (isGameOver) scoreManager.getBestScore() else currentState.bestScore
        )
    }
    
    private fun resetGame() {
        _state.value = GameState()
    }
    
    private fun dismissFeedback() {
        val currentState = _state.value
        if (currentState.isGameOver) {
            _state.value = currentState.copy(feedback = null)
        } else {
            _state.value = currentState.copy(feedback = null)
            playRandomNote()
        }
    }
    
    private fun playAgain() {
        _state.value = GameState(bestScore = scoreManager.getBestScore())
    }
}

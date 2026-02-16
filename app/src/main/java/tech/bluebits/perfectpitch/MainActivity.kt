package tech.bluebits.perfectpitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import tech.bluebits.perfectpitch.domain.SoundPlayer
import tech.bluebits.perfectpitch.presentation.game.GameScreen
import tech.bluebits.perfectpitch.ui.theme.PerfectPitchTheme
import tech.bluebits.perfectpitch.presentation.game.GameViewModel

class MainActivity : ComponentActivity() {
    private lateinit var soundPlayer: SoundPlayer

    private val gameViewModel : GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        soundPlayer = SoundPlayer(this)
        
        enableEdgeToEdge()
        setContent {
            PerfectPitchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        soundPlayer = soundPlayer,
                        modifier = Modifier.padding(innerPadding),
                        viewModel = gameViewModel
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundPlayer.release()
    }
}
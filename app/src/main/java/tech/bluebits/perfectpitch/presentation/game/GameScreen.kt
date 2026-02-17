package tech.bluebits.perfectpitch.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.bluebits.perfectpitch.domain.SoundPlayer
import tech.bluebits.perfectpitch.ui.theme.PerfectPitchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    soundPlayer: SoundPlayer,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (state.totalAttempts > 0) {
            ScoreSection(
                score = state.score,
                totalAttempts = state.totalAttempts
            )
        }
        
        // Title
        Text(
            text = "Perfect Pitch Game",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Play Button Section
        PlayButtonSection(
            isLoading = state.isLoading,
            isPlaying = state.isPlaying,
            onPlayClick = { viewModel.handleIntent(GameIntent.PlaySound) }
        )
        
        // Options Section
        if (state.isPlaying && state.options.isNotEmpty()) {
            OptionsSection(
                options = state.options,
                onNoteSelected = { note -> viewModel.handleIntent(GameIntent.SelectNote(note)) }
            )
        }
        
        // Feedback Section
        state.feedback?.let { feedback ->
            FeedbackSection(
                feedback = feedback,
                onDismiss = { viewModel.handleIntent(GameIntent.DismissFeedback) }
            )
        }
        
        // Reset Button
        if (state.totalAttempts > 0) {
            Button(
                onClick = { viewModel.handleIntent(GameIntent.ResetGame) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Reset Game")
            }
        }
    }
}

@Composable
fun ScoreSection(
    score: Int,
    totalAttempts: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Score",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$score",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Attempts",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$totalAttempts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (totalAttempts > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Accuracy",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(score * 100 / totalAttempts)}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun PlayButtonSection(
    isLoading: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    } else if (!isPlaying) {
        Button(
            onClick = onPlayClick,
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(60.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "PLAY\nNOTE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun OptionsSection(
    options: List<MusicalNote>,
    onNoteSelected: (MusicalNote) -> Unit
) {
    Text(
        text = "Which note did you hear?",
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { note ->
            Button(
                onClick = { onNoteSelected(note) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = note.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun FeedbackSection(
    feedback: String,
    onDismiss: () -> Unit
) {
    val isCorrect = feedback.startsWith("Correct")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = feedback,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (isCorrect) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCorrect) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreSectionPreview() {
    PerfectPitchTheme {
        ScoreSection(
            score = 7,
            totalAttempts = 10
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreSectionInitialPreview() {
    PerfectPitchTheme {
        ScoreSection(
            score = 0,
            totalAttempts = 0
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayButtonSectionPreview() {
    PerfectPitchTheme {
        PlayButtonSection(
            isLoading = false,
            isPlaying = false,
            onPlayClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayButtonSectionLoadingPreview() {
    PerfectPitchTheme {
        PlayButtonSection(
            isLoading = true,
            isPlaying = false,
            onPlayClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OptionsSectionPreview() {
    PerfectPitchTheme {
        Column {
            OptionsSection(
                options = listOf(MusicalNote.C, MusicalNote.E, MusicalNote.G),
                onNoteSelected = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackSectionCorrectPreview() {
    PerfectPitchTheme {
        FeedbackSection(
            feedback = "Correct! The note was C",
            onDismiss = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FeedbackSectionIncorrectPreview() {
    PerfectPitchTheme {
        FeedbackSection(
            feedback = "Wrong! The note was C",
            onDismiss = {}
        )
    }
}

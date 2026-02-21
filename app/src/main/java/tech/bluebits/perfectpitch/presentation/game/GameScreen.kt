package tech.bluebits.perfectpitch.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.bluebits.perfectpitch.R
import tech.bluebits.perfectpitch.ui.theme.PerfectPitchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    onNavigateToWelcome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ScoreSection(
            score = state.score,
            totalAttempts = state.totalAttempts,
        )

        // Game Over Section
        if (state.isGameOver) {
            GameOverSection(
                score = state.score,
                totalAttempts = state.totalAttempts,
                onPlayAgain = onNavigateToWelcome
            )
        } else {
            // Play Button Section
            PlayButtonSection(
                isLoading = state.isLoading,
                onPlayClick = { viewModel.handleIntent(GameIntent.PlaySound) }
            )
            
            // Options Section
            OptionsSection(
                optionsEnabled = state.feedback == null,
                options = state.options,
                onNoteSelected = { note -> viewModel.handleIntent(GameIntent.SelectNote(note)) }
            )
        }

        // Feedback Section
        state.feedback?.let { feedback ->
            FeedbackSection(
                feedback = feedback,
                isGameOver = state.isGameOver,
                onDismiss = { viewModel.handleIntent(GameIntent.DismissFeedback) }
            )
        }

        // Reset Button (only show when not in game over)
        if (!state.isGameOver) {
            Button(
                onClick = onNavigateToWelcome,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.reset_game))
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
                    text = stringResource(R.string.score),
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
                    text = stringResource(R.string.attempts),
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
                        text = stringResource(R.string.accuracy),
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
fun GameOverSection(
    score: Int,
    totalAttempts: Int,
    onPlayAgain: () -> Unit
) {
    val isPerfectScore = score == totalAttempts && totalAttempts == 10
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPerfectScore) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isPerfectScore) {
                Text(
                    text = stringResource(R.string.perfect_pitch_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.perfect_pitch_message),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = stringResource(R.string.game_over),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            Text(
                text = stringResource(R.string.final_score, score, totalAttempts),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if (isPerfectScore) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(R.string.accuracy_percentage, (score * 100 / totalAttempts)),
                fontSize = 18.sp,
                color = if (isPerfectScore) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPerfectScore)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(R.string.play_again),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlayButtonSection(
    isLoading: Boolean,
    onPlayClick: () -> Unit
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    } else {
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
                text = stringResource(R.string.play_note),
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
    onNoteSelected: (MusicalNote) -> Unit,
    optionsEnabled: Boolean
) {
    Text(
        text = stringResource(R.string.which_note_question),
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
                ),
                enabled = optionsEnabled
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
private fun FeedbackSection(
    feedback: String,
    isGameOver : Boolean,
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

            if (!isGameOver) {
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
                    Text(stringResource(R.string.continue_button))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreSectionPreview() {
    PerfectPitchTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ScoreSection(
                score = 7,
                totalAttempts = 10,
            )
            PlayButtonSection(false,{})
            OptionsSection(
                options = listOf(MusicalNote.C, MusicalNote.E, MusicalNote.G),
                onNoteSelected = {},
                optionsEnabled = true
            )
            FeedbackSection(
                feedback = "Correct! The note was C",
                onDismiss = {},
                isGameOver = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackSectionIncorrectPreview() {
    PerfectPitchTheme {
        FeedbackSection(
            feedback = "Wrong! The note was C",
            onDismiss = {},
            isGameOver = false
        )
    }
}

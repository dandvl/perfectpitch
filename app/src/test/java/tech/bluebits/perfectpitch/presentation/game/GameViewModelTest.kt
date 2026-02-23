package tech.bluebits.perfectpitch.presentation.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.bluebits.perfectpitch.domain.ScoreManager
import tech.bluebits.perfectpitch.domain.SoundPlayer

@ExperimentalCoroutinesApi
class GameViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var soundPlayer: SoundPlayer
    private lateinit var scoreManager: ScoreManager
    private lateinit var gameViewModel: GameViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        soundPlayer = mockk()
        scoreManager = mockk()
        
        every { scoreManager.getBestScore() } returns 0
        every { soundPlayer.playNote(any()) } just runs
        
        gameViewModel = GameViewModel(soundPlayer, scoreManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct default values`() {
        // Then
        val state = gameViewModel.state.value
        assertEquals(0, state.score)
        assertEquals(0, state.totalAttempts)
        assertEquals(0, state.bestScore)
        assertFalse(state.isPlaying)
        assertFalse(state.isLoading)
        assertFalse(state.isGameOver)
        assertNull(state.currentNote)
        assertNull(state.feedback)
        assertTrue(state.options.isEmpty())
    }

    @Test
    fun `init sets loading state and initializes game`() {
        // When
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = gameViewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.currentNote)
        assertEquals(3, state.options.size) // MAX_OPTIONS = 3
        assertEquals(0, state.score)
        assertEquals(0, state.totalAttempts)
        assertFalse(state.isGameOver)
        assertNull(state.feedback)
    }

    @Test
    fun `playSound calls soundPlayer playNote with current note`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()
        val currentNote = gameViewModel.state.value.currentNote

        // When
        gameViewModel.handleIntent(GameIntent.PlaySound)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { soundPlayer.playNote(currentNote!!) }
    }

    @Test
    fun `selectNote with correct answer increments score and provides feedback`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()
        val currentNote = gameViewModel.state.value.currentNote!!

        // When
        gameViewModel.handleIntent(GameIntent.SelectNote(currentNote))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = gameViewModel.state.value
        assertEquals(1, state.score)
        assertEquals(1, state.totalAttempts)
        assertNotNull(state.feedback)
        assertTrue(state.feedback!!.startsWith("Correct"))
        assertFalse(state.isPlaying)
    }

    @Test
    fun `selectNote with wrong answer does not increment score and provides feedback`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()
        val currentNote = gameViewModel.state.value.currentNote!!
        val wrongNote = MusicalNote.entries.first { it != currentNote }

        // When
        gameViewModel.handleIntent(GameIntent.SelectNote(wrongNote))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = gameViewModel.state.value
        assertEquals(0, state.score)
        assertEquals(1, state.totalAttempts)
        assertNotNull(state.feedback)
        assertTrue(state.feedback!!.startsWith("Wrong"))
        assertFalse(state.isPlaying)
    }

    @Test
    fun `game ends after 10 attempts and saves best score`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()
        every { scoreManager.saveBestScore(any()) } just runs

        // When - play 10 rounds
        repeat(10) {
            val currentNote = gameViewModel.state.value.currentNote!!
            gameViewModel.handleIntent(GameIntent.SelectNote(currentNote))
            gameViewModel.handleIntent(GameIntent.DismissFeedback)
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // Then
        val state = gameViewModel.state.value
        assertEquals(10, state.totalAttempts)
        assertEquals(10, state.score)
        assertTrue(state.isGameOver)
        verify { scoreManager.saveBestScore(10) }
    }

    @Test
    fun `dismissFeedback clears feedback and sets new note`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()
        val currentNote = gameViewModel.state.value.currentNote!!
        gameViewModel.handleIntent(GameIntent.SelectNote(currentNote))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        gameViewModel.handleIntent(GameIntent.DismissFeedback)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = gameViewModel.state.value
        assertNull(state.feedback)
        assertNotNull(state.currentNote)
        assertEquals(3, state.options.size)
        assertTrue(state.options.contains(state.currentNote))
    }

    @Test
    fun `resetGame resets state to initial values`() {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        gameViewModel.handleIntent(GameIntent.ResetGame)

        // When
        val state = gameViewModel.state.value

        // Then
        assertEquals(0, state.score)
        assertEquals(0, state.totalAttempts)
        assertEquals(0, state.bestScore)
        assertFalse(state.isPlaying)
        assertFalse(state.isLoading)
        assertFalse(state.isGameOver)
        assertNull(state.currentNote)
        assertNull(state.feedback)
        assertTrue(state.options.isEmpty())
    }

    @Test
    fun `options contain current note and random other notes`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val state = gameViewModel.state.value

        // Then
        assertEquals(3, state.options.size)
        assertTrue(state.options.contains(state.currentNote))
    }

    @Test
    fun `feedback message includes correct note name`() = runTest {
        // Given
        gameViewModel.handleIntent(GameIntent.Init)
        testDispatcher.scheduler.advanceUntilIdle()
        val currentNote = gameViewModel.state.value.currentNote!!

        // When
        gameViewModel.handleIntent(GameIntent.SelectNote(currentNote))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = gameViewModel.state.value
        assertTrue(state.feedback!!.contains(currentNote.displayName))
    }

}

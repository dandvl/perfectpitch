package tech.bluebits.perfectpitch.presentation.game

import org.junit.Test

import org.junit.Assert.*

class GameContractTest {

    @Test
    fun `GameState default values are correct`() {
        // When
        val gameState = GameState()

        // Then
        assertFalse(gameState.isPlaying)
        assertNull(gameState.currentNote)
        assertTrue(gameState.options.isEmpty())
        assertEquals(0, gameState.score)
        assertEquals(0, gameState.totalAttempts)
        assertNull(gameState.feedback)
        assertFalse(gameState.isLoading)
        assertFalse(gameState.isGameOver)
        assertEquals(0, gameState.bestScore)
    }

    @Test
    fun `GameState with custom values stores them correctly`() {
        // Given
        val note = MusicalNote.C
        val options = listOf(MusicalNote.C, MusicalNote.D, MusicalNote.E)
        val feedback = "Correct!"

        // When
        val gameState = GameState(
            isPlaying = true,
            currentNote = note,
            options = options,
            score = 5,
            totalAttempts = 7,
            feedback = feedback,
            isLoading = false,
            isGameOver = false,
            bestScore = 8
        )

        // Then
        assertTrue(gameState.isPlaying)
        assertEquals(note, gameState.currentNote)
        assertEquals(options, gameState.options)
        assertEquals(5, gameState.score)
        assertEquals(7, gameState.totalAttempts)
        assertEquals(feedback, gameState.feedback)
        assertFalse(gameState.isLoading)
        assertFalse(gameState.isGameOver)
        assertEquals(8, gameState.bestScore)
    }

    @Test
    fun `MusicalNote enum has correct display names and resource names`() {
        // Given & Then
        assertEquals("C", MusicalNote.C.displayName)
        assertEquals("c", MusicalNote.C.resourceName)

        assertEquals("C#", MusicalNote.C_SHARP.displayName)
        assertEquals("c_sharp", MusicalNote.C_SHARP.resourceName)

        assertEquals("D", MusicalNote.D.displayName)
        assertEquals("d", MusicalNote.D.resourceName)

        assertEquals("D#", MusicalNote.D_SHARP.displayName)
        assertEquals("d_sharp", MusicalNote.D_SHARP.resourceName)

        assertEquals("E", MusicalNote.E.displayName)
        assertEquals("e", MusicalNote.E.resourceName)

        assertEquals("F", MusicalNote.F.displayName)
        assertEquals("f", MusicalNote.F.resourceName)

        assertEquals("F#", MusicalNote.F_SHARP.displayName)
        assertEquals("f_sharp", MusicalNote.F_SHARP.resourceName)

        assertEquals("G", MusicalNote.G.displayName)
        assertEquals("g", MusicalNote.G.resourceName)

        assertEquals("G#", MusicalNote.G_SHARP.displayName)
        assertEquals("g_sharp", MusicalNote.G_SHARP.resourceName)

        assertEquals("A", MusicalNote.A.displayName)
        assertEquals("a", MusicalNote.A.resourceName)

        assertEquals("A#", MusicalNote.A_SHARP.displayName)
        assertEquals("a_sharp", MusicalNote.A_SHARP.resourceName)

        assertEquals("B", MusicalNote.B.displayName)
        assertEquals("b", MusicalNote.B.resourceName)
    }

    @Test
    fun `MusicalNote entries contains all 12 notes`() {
        // When
        val notes = MusicalNote.entries

        // Then
        assertEquals(12, notes.size)
        assertTrue(notes.contains(MusicalNote.C))
        assertTrue(notes.contains(MusicalNote.C_SHARP))
        assertTrue(notes.contains(MusicalNote.D))
        assertTrue(notes.contains(MusicalNote.D_SHARP))
        assertTrue(notes.contains(MusicalNote.E))
        assertTrue(notes.contains(MusicalNote.F))
        assertTrue(notes.contains(MusicalNote.F_SHARP))
        assertTrue(notes.contains(MusicalNote.G))
        assertTrue(notes.contains(MusicalNote.G_SHARP))
        assertTrue(notes.contains(MusicalNote.A))
        assertTrue(notes.contains(MusicalNote.A_SHARP))
        assertTrue(notes.contains(MusicalNote.B))
    }

    @Test
    fun `GameIntent sealed classes are correctly typed`() {
        // Given & Then
        assertTrue(GameIntent.Init is GameIntent)
        assertTrue(GameIntent.PlaySound is GameIntent)
        assertTrue(GameIntent.ResetGame is GameIntent)
        assertTrue(GameIntent.DismissFeedback is GameIntent)
        
        val selectNoteIntent = GameIntent.SelectNote(MusicalNote.C)
        assertTrue(selectNoteIntent is GameIntent)
        assertEquals(MusicalNote.C, selectNoteIntent.note)
    }

    @Test
    fun `GameState copy function works correctly`() {
        // Given
        val originalState = GameState(
            isPlaying = true,
            currentNote = MusicalNote.C,
            options = listOf(MusicalNote.C, MusicalNote.D),
            score = 3,
            totalAttempts = 5,
            feedback = "Test feedback",
            isLoading = true,
            isGameOver = false,
            bestScore = 7
        )

        // When
        val copiedState = originalState.copy(score = 8, totalAttempts = 10)

        // Then
        assertEquals(originalState.isPlaying, copiedState.isPlaying)
        assertEquals(originalState.currentNote, copiedState.currentNote)
        assertEquals(originalState.options, copiedState.options)
        assertEquals(8, copiedState.score)
        assertEquals(10, copiedState.totalAttempts)
        assertEquals(originalState.feedback, copiedState.feedback)
        assertEquals(originalState.isLoading, copiedState.isLoading)
        assertEquals(originalState.isGameOver, copiedState.isGameOver)
        assertEquals(originalState.bestScore, copiedState.bestScore)
    }

    @Test
    fun `GameState equals and hashCode work correctly`() {
        // Given
        val state1 = GameState(score = 5, totalAttempts = 8)
        val state2 = GameState(score = 5, totalAttempts = 8)
        val state3 = GameState(score = 3, totalAttempts = 8)

        // Then
        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
        assertNotEquals(state1, state3)
        assertNotEquals(state1.hashCode(), state3.hashCode())
    }

    @Test
    fun `MusicalNote toString returns expected format`() {
        // When
        val noteString = MusicalNote.C.toString()

        // Then
        assertTrue(noteString.contains("C"))
    }
}

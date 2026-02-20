package tech.bluebits.perfectpitch.presentation.game

data class GameState(
    val isPlaying: Boolean = false,
    val currentNote: MusicalNote? = null,
    val options: List<MusicalNote> = emptyList(),
    val score: Int = 0,
    val totalAttempts: Int = 0,
    val feedback: String? = null,
    val isLoading: Boolean = false,
    val isGameOver: Boolean = false,
    val bestScore: Int = 0
)

sealed class GameIntent {
    object Init : GameIntent()
    object PlaySound : GameIntent()
    data class SelectNote(val note: MusicalNote) : GameIntent()
    object ResetGame : GameIntent()
    object DismissFeedback : GameIntent()
}

enum class MusicalNote(val displayName: String, val resourceName: String) {
    C("C", "c"),
    C_SHARP("C#", "c_sharp"),
    D("D", "d"),
    D_SHARP("D#", "d_sharp"),
    E("E", "e"),
    F("F", "f"),
    F_SHARP("F#", "f_sharp"),
    G("G", "g"),
    G_SHARP("G#", "g_sharp"),
    A("A", "a"),
    A_SHARP("A#", "a_sharp"),
    B("B", "b")
}

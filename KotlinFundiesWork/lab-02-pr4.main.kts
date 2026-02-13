import khoury.EnabledTest
import khoury.input
import khoury.reactConsole
import khoury.runEnabledTests
import khoury.testSame

enum class Song { START, MIDDLE, END }

data class Loop(
    val songState: Song,
    val count: Int,
)

fun lyrics(words: Loop): String =
    when (words.songState) {
        Song.START -> ("A, B, C")
        Song.MIDDLE -> ("Easy as 1, 2, 3")
        Song.END -> ("A, B, C, 1, 2, 3, baby, you and me, girl! ${words.count}")
    }

fun nextState(
    state: Loop,
    str: String,
): Loop {
    val input = str.lowercase()
    return when {
        state.songState == Song.START -> {
            Loop(Song.MIDDLE, 1)
        }
        input == "exit" -> {
            Loop(Song.END, state.count)
        }
        else -> {
            Loop(Song.MIDDLE, state.count + 1)
        }
    }
}

fun terminate(state: Loop): Boolean = state.songState == Song.END

fun loop(): Loop =
    reactConsole(
        initialState = Loop(Song.START, 0),
        stateToText = ::lyrics,
        nextState = ::nextState,
        isTerminalState = ::terminate,
        terminalStateToText = ::lyrics,
    )

@EnabledTest
fun testText() {
    val a = Loop(Song.START, 0)
    testSame(lyrics(a), "A, B, C")
    val b = Loop(Song.MIDDLE, 0)
    testSame(lyrics(b), "Easy as 1, 2, 3")
    val c = Loop(Song.END, 2)
    testSame(lyrics(c), "A, B, C, 1, 2, 3, baby, you and me, girl! 2")
}

@EnabledTest
fun testNextState() {
    testSame(nextState(Loop(Song.START, 0), "g"), Loop(Song.MIDDLE, 1))
    testSame(nextState(Loop(Song.MIDDLE, 0), "g"), Loop(Song.MIDDLE, 1))
    testSame(nextState(Loop(Song.MIDDLE, 2), "EXIT"), Loop(Song.END, 2))
}

@EnabledTest
fun testTerminate() {
    testSame(terminate(Loop(Song.MIDDLE, 0)), false)
    testSame(terminate(Loop(Song.END, 0)), true)
}

runEnabledTests(this)
loop()

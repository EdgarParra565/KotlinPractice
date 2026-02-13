import khoury.CapturedResult
import khoury.EnabledTest
import khoury.captureResults
import khoury.input
import khoury.runEnabledTests
import khoury.testSame

val prompt = "What hour (1-11am) would you like to wake-up?"
val nan = "Not a number"
val nir = "Not in range (1-11am)"
val early = "Before 8am? Early bird catches the worm!"
val later = "8am or later? Coffee time!"

fun wakeUpTime() {
    println(prompt)
    val user = input()
    val ints = user.toIntOrNull()
    if (ints == null) {
        println(nan)
    } else if (ints < 1 || ints > 11) {
        println(nir)
    } else if (ints < 8) {
        println(early)
    } else {
        println(later)
    }
}

@EnabledTest
fun testWakeupTime() {
    fun _test(
        cin: String,
        cout: String,
    ) {
        testSame(
            captureResults(::wakeUpTime, cin),
            CapturedResult(Unit, prompt, cout),
        )
    }
    _test("Howdy!", nan)
    _test("0", nir)
    _test("12", nir)
    _test("5", early)
    _test("8", later)
    _test("11", later)
}

runEnabledTests(this)

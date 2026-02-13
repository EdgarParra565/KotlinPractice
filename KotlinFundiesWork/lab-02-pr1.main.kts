import khoury.EnabledTest
import khoury.runEnabledTests
import khoury.testSame

fun startsWithY(s: String): Boolean {
    val first = s.first().uppercase()
    if (first == "Y") {
        return true
    } else {
        return false
    }
}
println(startsWithY("yell"))

@EnabledTest
fun testsStartsWithY() {
    testSame(startsWithY("yell"), true, "true")
    testSame(startsWithY("hey"), false, "false")
    testSame(startsWithY("Yo"), true, "true")
}

runEnabledTests(this)

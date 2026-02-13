import khoury.EnabledTest
import khoury.testSame

data class Full(
    val first: String,
    val middle: String,
    val last: String,
)

fun formatName(name: Full): String = (name.last.uppercase() + ", " + name.first + " " + name.middle)

data class StudentPair(
    val student1: Full,
    val student2: Full,
)

fun getCardLength(names: StudentPair): Int {
    val length1: Int = formatName(names.student1).length
    val length2: Int = formatName(names.student2).length
    if (length1 >= length2) {
        return length1
    } else {
        return length2
    }
}

@EnabledTest
fun testGetCardLength() {
    val harry = Full("Harry", "James", "Potter")
    val hermione = Full("Hermione", "Jean", "Granger")
    val ron = Full("Ron", "Bilius", "Weasley")
    val thePair = StudentPair(harry, hermione)
    val thePairTwo = StudentPair(harry, ron)
    testSame(getCardLength(thePair), 22)
    testSame(getCardLength(thePair), 19)
}
runEnabledTests(this)
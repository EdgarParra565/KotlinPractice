import khoury.fileReadAsList

val records = fileReadAsList("records.txt")
val names = fileReadAsList("names.txt")


fun initFn(List:String )
val result = List<String>(desiredSize, ::initFn)

fun build(size: Int, )

@EnabledTest
fun testBuild() {
testSame(
build(2, "404.txt", "404.txt"),
listOf("", ""),
"Empty",
)
testSame(
build(1, "404.txt", "records.txt"),
listOf("PD is crushing it"),
"Person Doe",
)
testSame(
build(3, "names.txt", "records.txt"),
listOf(
"HP is crushing it",
"HG is crushing it",
"RW is crushing it",
),
"1-to-1",
)
testSame(
build(7, "names.txt", "records.txt"),
listOf(
"HP is crushing it",
"HG is crushing it",
"RW is crushing it",
"CC needs support",
"NL is crushing it",
"LL is unknown",
"GW is unknown",
),
"Missing records",
)
}
import khoury.testSame
import khoury.EnabledTest
import khoury.runEnabledTests

fun transform(los:List<String>):List<Int>{
    return los.map({n: String -> n.count() })
}
//{ arg1: argType1, arg2: argType2 -> expression(arg1, arg2) }
@EnabledTest
fun testTransform(){
    testSame(transform(listOf("hello", "fundies")), listOf(5, 7))
}
runEnabledTests(this)
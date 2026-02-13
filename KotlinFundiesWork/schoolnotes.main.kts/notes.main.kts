fun function(arg1: Type1, arg2:Type2):returnType{
    -
    return value/expression
}


import khoury.EnabledTest
import khoury.testSame
import khoury.runEnabledTests

@EnabledTest
fun testFuncName(){
    testSame(funcName(val1,val2,..),expectedValue,"short description")
    ...
}
...
runEnabledTests(this)
main()
Number- Int,Double
Text-String
Functions- Type1,Type2-returnType
small set of option- Boolean, enum class (task-specific values)
Bundle into one value- data class
"unchanged $value ${expression}" - String to String templeate
Boolean to condtional- if, else if, else
options to when expressions- when, enum class
enumerations- enum class Option {a,b,c}
fun useoption(o:Option){
    when(o){
        Option.A-> ...
        Option.B-> ...
        Option.C-> ...
    }
}
Data class- data class Pieces(val a:Int, val b: String)
val p: Pieces= PIeces(1,"one")
fun use Pieces(p: Pieces){
    p.a or p.b
}
main()
input()
println(str)
testSame, captureResults, CapturedResult,
@EnabledTest, runEnabledTests
reactConsole- intial staates to first state-> am i done yet(two paths yes and no)-> if no render(state to Text)-> 
get input(go on)-> next state(x, input){ reurn x+1}-> go back to first state-> am i done (yes)-> terminal state(usually last message)-> return lastState

Sample Quiz Question
1
fun f(p:???)??? {
    val temp= "howdy-$p"
    println(temp.length)
}
Answer
fun f(p:cannot be determined):Unit {
    val temp= "howdy-$p"
    println(temp.length)
}

Sample 2
fun f2(x:???, y:???, z:???):???{
    return when(z.length > x) {
        true-> x + y
        false -> z.length
    }
}
fun f1(a: ???, b:???, c:???):???{
    return if (f2(b,c,a)>= f2(c,b,a) b else c)
}
Answer
fun f2(x:Int , y:Int, z:String): Int{
    return when(z.length > x) {
        true-> x + y
        false -> z.length
    }
}
fun f1(a: ???, b:???, c:???):Unit {
    return if (f2(b,c,a)>= f2(c,b,a)) b 
    else c
}
EnumCLASS and TEST
enum class Workday{Mon,Tue,Wed,Thu,Fri,.}
fun numCupsNeeded(dow:workday):Int{
    return when (dow){
        Workday.Mon -> 3
        Workday.Tue -> 2
        Workday.Wed -> 3
        Workday.Thu -> 2
        Workday.Fri -> 1
    }
}
fun enoughCypsCoffee(dow:Workday,cupsHad:Int):Boolean{
    return cupsHad>= numCupsNeeded(dow)
}
@EnabledTest
fun testEnough(){
    testSame(enoughCupsCoffe(Workday.Mon, 1), false, "practice1")
    testSame(enoughCupsCoffe(Workday.Wed, 1), false, "practice2")
}
//(write at least one test for both cases true and false)
// need to test for every case


enum class Campus{ OAKLAND, BOSTON, LONDON}
enum class Currency{USD, GBP}
data class WelcomeInfo(val greeting:String, val currency:Currency)
fun campusToCurrency(campus:Campus):Currency{
    return when
} 



import khoury.EnabledTest
import khoury.input
import khoury.runEnabledTests
import khoury.testSame
import khoury.reactConsole
enum class SongState{START, MIDDLE, END}
data class SongLoop(val songState : SongState, val count : Int)
//function that returns specific messages based on the song state
fun text(state : SongLoop):String{
    return when(state.songState){
        SongState.START -> "A, B, C"
        SongState.MIDDLE -> "Easy as 1, 2, 3"
        SongState.END -> ("A, B, C, 1, 2, 3, baby, you and me, girl! " + state.count)
    } 
}
//function that changes the state of the song based on user input
fun nextState(state: SongLoop, str: String): SongLoop {
    val input = str.lowercase()
    return when {
        state.songState == SongState.START -> {
            SongLoop(SongState.MIDDLE, 1)
        }
        input == "exit" -> {
            SongLoop(SongState.END, state.count)
        }
        else -> {
            SongLoop(SongState.MIDDLE, state.count + 1)
        }
    }
}
//function that detects if the user has typed exit and terminates the reactconsole loop
fun terminate(state : SongLoop):Boolean{
return state.songState==SongState.END
}
//function that contains reactconsole that returns the song and the loop count
fun song(): SongLoop{
    return reactConsole(
    initialState = SongLoop(SongState.START, 0), 
    stateToText = ::text, 
    nextState = ::nextState, 
    isTerminalState= ::terminate, 
    terminalStateToText = ::text
    )
}
@EnabledTest
fun testText()
{
    val a = SongLoop(SongState.START, 0)
    testSame(text(a),"A, B, C" )
    val b = SongLoop(SongState.MIDDLE, 0)
    testSame(text(b),"Easy as 1, 2, 3")
    val c = SongLoop(SongState.END, 5)
    testSame(text(c),"A, B, C, 1, 2, 3, baby, you and me, girl! 5")
}
@EnabledTest
fun testNextState(){
    testSame(nextState(SongLoop(SongState.START, 0), "a"), SongLoop(SongState.MIDDLE, 1))
    testSame(nextState(SongLoop(SongState.MIDDLE, 0), "a"), SongLoop(SongState.MIDDLE, 1))
    testSame(nextState(SongLoop(SongState.MIDDLE, 5), "EXIT"), SongLoop(SongState.END, 5))
}
@EnabledTest
fun testTerminate(){
    testSame(terminate(SongLoop(SongState.MIDDLE, 0)), false)
    testSame(terminate(SongLoop(SongState.END, 0)), true)
}
fun main()
{
    song()
}
runEnabledTests(this)
main()

fun drawBox(val rows:Int, val ncols:Int, val c, Char){
    for (row in 1... nrwos){
        for (col in 1...ncols){
            println(c)
        }
}
}
drawBox(20,10,'*')
import khoury.reactConsole
import khoury.input

data class MyState(val userInput: Int, val guesses:Int)
// step 0
val initialState = MyState(0,0)
// step 1
fun terminate(currentState: MyState ):Boolean{
    // when the user types 7
    return (currentState.userInput==7)
}

// step 2
fun stateToText(currentState: MyState):String {
    if (currentState.guesses == 0)
        return "Guess a number"

    else if (currentState.userInput >7) 
        return "Too high"

    else if (currentState.userInput <7)  
        return "Too low"

    else 
        return "you found it"
}
//step 3
fun transitionState(currentState:MyState, userInput: String) = MyState(userInput.toInt(), currentState.guesses + 1 )


reactConsole(
    initialState,
    ::stateToText,
    ::transitionState,
    ::terminate
)
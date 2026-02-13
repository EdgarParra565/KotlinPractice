import khoury.CapturedResult
import khoury.EnabledTest
import khoury.captureResults
import khoury.fileExists
import khoury.fileReadAsList
import khoury.input
import khoury.isAnInteger
import khoury.reactConsole
import khoury.runEnabledTests
import khoury.testSame

/*** Step 1: Questions ***/
data class Question(
    val question: String,
    val answer: String,
)
interface taggedQ {

    fun taggedAs(tag:String):Boolean

    fun format():String

}


data class TaggedQuestion(val questionAnswer: Question, val tags:List<String>):taggedQ{
val QASEPARATOR  = "|"
val TAGSEPARATOR = ","
    override fun taggedAs(tag:String):Boolean{
        val newTags = this.tags.map({it.lowercase()})
        return (tag.lowercase() in newTags)
    }

    override fun format():String{
        if (tags.isEmpty()){
            return ("${questionAnswer.question}$QASEPARATOR${questionAnswer.answer}")
        }
        else {

            val str = tags.fold(QASEPARATOR, {a : String, b -> a+b+TAGSEPARATOR })
            val str2 = str.substring(0, str.length - 1)
            return "${questionAnswer.question}$QASEPARATOR${questionAnswer.answer}$str2"
            }
        }
    
}

data class TaggedQuestionBank(val name: String, val questions:List<TaggedQuestion>)

@EnabledTest
fun testTaggedQuestion(){
    val exQ1 = Question("What is 3x3?", "9")
    val exQ2 = Question("Who killed Abraham Lincoln?", "John Wilkes Booth")
    val exQ3 = Question("How many continents are there?", "7")
    val tExQ1 = TaggedQuestion(exQ1, listOf("Math", "Hard"))
    val tExQ2 = TaggedQuestion(exQ2, emptyList<String>())
    
    testSame(tExQ1.taggedAs("math"), true, "taggedAs True")
    testSame(tExQ1.taggedAs("easy"), false, "taggedAs false")

    testSame(tExQ1.format(), "What is 3x3?|9|Math,Hard", "Format")
    testSame(tExQ2.format(), "Who killed Abraham Lincoln?|John Wilkes Booth", "Format empty list")


    
}

fun stringToTaggedQuestion(str:String):TaggedQuestion{
    val lst = str.split("|")
    val lst2 = lst[2].split(",")
    return (TaggedQuestion(Question(lst[0],lst[1]),lst2))
}


// function that reads a file of Questions and converts them to a TaggedQuestionBank
fun readTaggedQuestionBank(filepath: String): TaggedQuestionBank {
    if (fileExists(filepath)) {
        // created values that creates a list of Strings, then converts those strings to Questions
        val questionList = fileReadAsList(filepath)
        val questionList2 = questionList.map(::stringToTaggedQuestion)
        // initialized String that is declared in the following if else statements, name of the file without the path and .txt
        val name: String
        if (filepath.indexOf("/") == -1) {
            name = filepath.substring(0, filepath.indexOf(".txt"))
        } else {
            name = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.indexOf(".txt"))
        }
        // values that capitalize the name of the file then creates the QuestionBank with that name
        val capitalName = name.substring(0, 1).uppercase() + name.substring(1)
        val fileBank = TaggedQuestionBank(capitalName, questionList2)
        return fileBank
    } else {
        return TaggedQuestionBank("Empty", emptyList())
    }
}

@EnabledTest
fun conversions(){
    testSame(stringToTaggedQuestion("What is 3x3?|9|Math,Hard"), TaggedQuestion(Question("What is 3x3?","9"),listOf("Math", "Hard") ), "stringToTaggedQuestion")
    testSame(readTaggedQuestionBank("C:/Users/lucas/OneDrive/Documents/CSCode/DownloadedFiles/music.txt"), 
    TaggedQuestionBank("Music", listOf(
        TaggedQuestion(Question("How many members were in Wutang-Clan?", "10"),listOf("Music", "Medium")),
        TaggedQuestion(Question("What year was Tupac Killed?", "1996"),listOf("Music", "Hard")),
        TaggedQuestion(Question("Where is King Von From?", "O-Block"),listOf("Music", "Easy")),
     
)), "readTaggedQuestionBank")
}

interface IMenuOption {
    fun getTitle(): String
}

/**
 * A menu option with a single value and name.
 */
data class NamedMenuOption<T>(
    val option: T,
    val name: String
) : IMenuOption {
    
    override fun getTitle(): String = name
}

fun <T : IMenuOption> chooseMenu(options: List<T>): T? {
// function that prompts the user to choose from a list of QuestionBanks then returns the index of the users choice
    return reactConsole(
        initialState = options,
        stateToText = ::possibleBanks,
        nextState = ::validNum,
        isTerminalState = ::oneBank,
        terminalStateToText = ::possibleBanks,
    )[0]
}
// helper function used in possibleBanks that returns the index + 1 and the name of a QuestionBank
fun <T: IMenuOption> bankAndIndex(
    index: Int,
    qBank: T?,
): String = (index + 1).toString() + ". " + qBank?.getTitle()

// function used in chooseBank that returns a string of list of QuestionBanks and prompts the user to select one or returns nothing if there is only one element
fun <T: IMenuOption> possibleBanks(list: List<T?>): String {
    if (list.size == 1 ) {
        if (list[0] == null){
            return "Bye."
        }
        else{
            return("You chose to study ${list[0]?.getTitle()}")
        }
    
    } else {
        println("Enter 1, 2, ..., or 0 to quit:")
        val newList = list.mapIndexed(::bankAndIndex)

        for (i in 0..newList.size - 1) {
            println(newList[i])
        }
        return("Return your choice: ")
    }
}

// function used in chooseBank that checks if the user inputed a valid number then returns a list of QuestionBank wiht only one element based off the user input
fun <T> validNum(
    list: List<T?>,
    input: String,
): List<T?> {
    if (isAnInteger(input)) {
        // value that converts the user input to a Int
        val inputInt = input.toInt()
        if (inputInt > list.size || inputInt < 0) {
            return list
        }
        else if (inputInt == 0)
        {
            return listOf(null)
        } else {
            return listOf(list[inputInt - 1])
        }

    } else {
        return list
    }
}

// function that returns true if there is only one element in a supplied list of QuestionBank
fun <T> oneBank(list: List<T?>): Boolean {
    if (list.size == 1) {
        return true
    }
    else {
        return false
    }
    
}


@EnabledTest
fun testChooseMenu() {

    /**
     * Individual examples, as well as a list of those examples
     * (for testing purposes only)
     */
    val anApple = NamedMenuOption(1, "Apple")
    val aBanana = NamedMenuOption(2, "Banana")
    val fruits = listOf(anApple, aBanana)

    /* Some useful outputs */
    val prompt = "Enter 1, 2, ..., or 0 to quit:"
    val quit   = "Bye."

    testSame(
        captureResults(
            { chooseMenu(fruits) },
            "",
            "0",
        ),
        CapturedResult(
            null,
            prompt,
            "1. ${anApple.name}",
            "2. ${aBanana.name}",
            "Return your choice: ",
            prompt,
            "1. ${anApple.name}",
            "2. ${aBanana.name}",
            "Return your choice: ",
            quit,
        ),
        "Quitting",
    )

    testSame(
        captureResults(
            { chooseMenu(fruits) },
            "",
            "10",
            "-3",
            "1",
        ),
        CapturedResult(
            anApple,
            prompt,
            "1. ${anApple.name}", 
            "2. ${aBanana.name}", 
            "Return your choice: ", 
            prompt,
            "1. ${anApple.name}", 
            "2. ${aBanana.name}", 
            "Return your choice: ", 
            prompt,
            "1. ${anApple.name}", 
            "2. ${aBanana.name}", 
            "Return your choice: ", 
            prompt,
            "1. ${anApple.name}", 
            "2. ${aBanana.name}", 
            "Return your choice: ", 
            "You chose to study ${anApple.name}",
        ),
        "Choose Bank #1",
    )

    testSame(
        captureResults(
            { chooseMenu(fruits) },
            "3",
            "2",
        ),
        CapturedResult(
            aBanana, 
            prompt,
            "1. ${anApple.name}", 
            "2. ${aBanana.name}", 
            "Return your choice: ", 
            prompt,
            "1. ${anApple.name}", 
            "2. ${aBanana.name}", 
            "Return your choice: ", 
            
            "You chose to study ${aBanana.name}",
        ),
        "Choose Bank #2",
    )
}


runEnabledTests(this)

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

// step 1
// a data class that stores both the question and answer as strings
data class Question(
    val question: String,
    val answer: String,
)
val q1 = Question("Which continent is England in?", "Europe")
val q2 = Question("Which continent is Greece in?", "Europe")
val q3 = Question("Which continent is Eygpt in?", "Africa")

// step 2
// data class that stores a list of Question data class as well as assigning it a name as a string
data class QuestionBank(
    val name: String,
    val question: List<Question>,
)

val myQuestionBank = QuestionBank("Geography", listOf(q1, q2, q3))

// step 3
// Function that takes a integer and creates a list of questions by multiplying the integer by itslef three times and storing the question
// and answer in a Question Data class then storing the list of Questions into QuestionBank data class as well as assigning the List a name
fun cubes(value: Int): QuestionBank {
    val questions = (1..value).map { i -> Question("Q$i :What is $i cubed?", "${ i * i * i}") }
    return QuestionBank("Perfect Cubes", questions)
}

// step 4
// takes a question data class and converts it to string
fun questionToString(question: Question): String = "${question.question}|${question.answer}"

// takes a string and converts it question data class by breaking the string into two pieces
fun stringToQuestion(s: String): Question {
    val citiesList = s.split("|")
    return Question(citiesList[0], citiesList[1])
}

// takes a file path and returns a QuestionBank data class by mapping each line as a Question data class by using stringToQuestion
fun readQuestionBank(path: String): QuestionBank =
    if (fileExists(path)) {
        val pathToFile = fileReadAsList(path).map(::stringToQuestion)
        QuestionBank("Capitals", pathToFile)
    } else {
        QuestionBank("Empty", emptyList())
    }

// step 5
// takes a string and returns a boolean if the paramter stars with y
fun isCorrect(answer: String): Boolean {
    val capitalLetter = answer.uppercase()
    return capitalLetter.startsWith("Y")
}

// used to differentiate the differnt stages that our function might be in
enum class Stage { CORRECT, WRONG, QUESTION }

// holds 4 different types of data that need to be stored togther
data class State(
    val questionPrompt: QuestionBank,
    val currentStage: Stage,
    val count: Int,
    val correctAnswers: Int,
)

// takes a state data class that prints a question if the stage is in question
fun firstState(state: State): String =
    when (state.currentStage) {
        Stage.QUESTION -> state.questionPrompt.question[state.count].question
        else -> "Invalid stage"
    }

// takes a state and user input and changes stage depending on wheather the user inputed a yes or no that updates the count and adds a 1 to correct answers if they answered yes
fun nextState(
    state: State,
    str: String,
): State {
    println("The correct answer is: ${state.questionPrompt.question[state.count].answer}. Did you get that right? Yes or No")
    val response = input()
    return if (isCorrect(response)) {
        state.copy(currentStage = Stage.CORRECT, count = state.count + 1, correctAnswers = state.correctAnswers + 1)
    } else {
        state.copy(currentStage = Stage.WRONG, count = state.count + 1)
    }
}

// terminates the raect console after stage changes
fun terminate(state: State): Boolean = state.currentStage == Stage.CORRECT || state.currentStage == Stage.WRONG

// prints Good Job or Good Try depending on if they answered yes or no
fun terminateStateToText(state: State): String =
    when (state.currentStage) {
        Stage.CORRECT -> "Good Job"
        Stage.WRONG -> "Good Try"
        else -> ""
    }

// uses react console to go ask, answer, show answer, and self report a single answer
fun studyQuestion(question: State): State =
    reactConsole(
        initialState = question,
        stateToText = ::firstState,
        nextState = ::nextState,
        isTerminalState = ::terminate,
        terminalStateToText = ::terminateStateToText,
    )

// step 6
// takes a state and prints the question the user is on
fun bankFirstState(bank: State): String = "Question:" + (bank.count + 1) + " Press enter to continue"

// takes a state and user input and runs the study question react console to go through the question and get a the number of self reported correct answers
fun bankNextState(
    bankState: State,
    input: String,
): State {
    val correctAnswers = studyQuestion(bankState).correctAnswers
    return(bankState.copy(currentStage = Stage.QUESTION, count = bankState.count + 1, correctAnswers = correctAnswers))
}

// terminates when the count equals three because only three questions are present in each list
fun bankTerminate(state: State): Boolean = state.count >= 3

// states correctAnswers and prints a string with the number of answers you self reported correct
fun bankterminalStateToText(state: State): String = "You got ${state.correctAnswers} answer(s) correctly. Bye"

// uses react console to go through each question within the QuestionBank
fun studyQuestionBank(bank: QuestionBank) {
    reactConsole(
        initialState = State(bank, Stage.QUESTION, 0, 0),
        stateToText = ::bankFirstState,
        nextState = ::bankNextState,
        isTerminalState = ::bankTerminate,
        terminalStateToText = ::bankterminalStateToText,
    )
}

// step 7

// Helper function used in chooseBank that returns the indez plus 1 and the name of a Question Bank
fun f(
    index: Int,
    bank: QuestionBank,
): String = "${ index + 1 }. ${bank.name}"

// takes a list of QuestionBanks that prints a string with each of the QuestionBanks as choices and prompts the user to choose one
fun chooseBankFirst(options: List<QuestionBank>): String {
    println("Welcome to Question Time. Choose one of the options!")

    return options.mapIndexed(::f).joinToString("\n") + "\nEnter your choice"
}

// takes the list of QuestionBanks and a user input and returns the choosen bank or else just keeps printing the bank choice selection string
fun chooseNextState(
    banks: List<QuestionBank>,
    str: String,
): List<QuestionBank> =
    if (isAnInteger(str)) {
        val choice = str.toInt()
        if (choice in 1..banks.size) listOf(banks[choice - 1]) else banks
    } else {
        banks
    }

// terminates once a single QuestionBank has been choosen
fun chooseTerminalState(banks: List<QuestionBank>): Boolean = banks.size == 1

// takes a list of QuestionBanks that uses react console to get a single QuestionBank via user input
fun chooseBank(questionLists: List<QuestionBank>): QuestionBank =

    reactConsole(
        initialState = questionLists,
        stateToText = ::chooseBankFirst,
        nextState = ::chooseNextState,
        isTerminalState = ::chooseTerminalState,
        terminalStateToText = ::chooseBankFirst,
    )[0]

// step 8
// creates a list of QuestionBanks that can be used in chooseBank that can then be used in studyQuestionBank
fun play() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    val selectedBank = chooseBank(banks)
    studyQuestionBank(selectedBank)
}

fun main() {
    play()
}

// kotlin -cp khoury.jar question-time.main.kts
// ktlint --format question-time.main.kts

@EnabledTest
fun testCubes() {
    testSame(
        cubes(3),
        QuestionBank(
            "Perfect Cubes",
            listOf(
                Question("Q1 :What is 1 cubed?", "1"),
                Question("Q2 :What is 2 cubed?", "8"),
                Question("Q3 :What is 3 cubed?", "27"),
            ),
        ),
        "creates a question bank for perfect cubes up to 3",
    )
}

@EnabledTest
fun testQuestionToString() {
    testSame(
        questionToString(Question("Which continent is England in?", "Europe")),
        "Which continent is England in?|Europe",
        "converts a Question to a correctly formatted string",
    )
    testSame(
        questionToString(Question("Which continent is Greece in?", "Europe")),
        "Which continent is Greece in?|Europe",
        "converts a Question to a correctly formatted string",
    )
}

@EnabledTest
fun testStringToQuestion() {
    testSame(
        stringToQuestion("What is the capital of England?|London"),
        Question("What is the capital of England?", "London"),
        "parses a string into a Question data class",
    )
    testSame(
        stringToQuestion("What is the capital of Spain?|Madrid"),
        Question("What is the capital of Spain?", "Madrid"),
        "parses a string into a Question data class",
    )
    testSame(
        stringToQuestion("What is the capital of Ecuador?|Quito"),
        Question("What is the capital of Ecuador?", "Quito"),
        "parses a string into a Question data class",
    )
}

@EnabledTest
fun testReadQuestionBank() {
    testSame(
        readQuestionBank("my-questions.txt"),
        QuestionBank(
            "Capitals",
            listOf(
                Question("What is the capital of England?", "London"),
                Question("What is the capital of Spain?", "Madrid"),
                Question("What is the capital of Ecuador?", "Quito"),
            ),
        ),
        "reads a question bank from an existing file",
    )
    testSame(
        readQuestionBank("empty.txt"),
        QuestionBank("Empty", emptyList()),
        "returns an empty question bank when file does not exist",
    )
}

@EnabledTest
fun testIsCorrect() {
    testSame(isCorrect("Yes"), true, "sees if parameter starts with y as a correct response")
    testSame(isCorrect("No"), false, "sees if parameter does not start with y as an incorrect response")
}

@EnabledTest
fun testFirstState() {
    val questionBank =
        QuestionBank(
            "Perfect Cubes",
            listOf(
                Question("Q1 :What is 1 cubed?", "1"),
                Question("Q2 :What is 2 cubed?", "8"),
                Question("Q3 :What is 3 cubed?", "27"),
            ),
        )

    testSame(
        firstState(State(questionBank, Stage.QUESTION, 0, 0)),
        "Q1 :What is 1 cubed?",
        "displays the first question in the bank",
    )
    testSame(
        firstState(State(questionBank, Stage.QUESTION, 1, 0)),
        "Q2 :What is 2 cubed?",
        "displays the second question in the bank",
    )
    testSame(
        firstState(State(questionBank, Stage.CORRECT, 0, 0)),
        "Invalid stage",
        "displays 'Invalid stage' when state is not QUESTION",
    )
}

@EnabledTest
fun testNextState() {
    val questionBank =
        QuestionBank(
            "Perfect Cubes",
            listOf(
                Question("Q1 :What is 1 cubed?", "1"),
                Question("Q2 :What is 2 cubed?", "8"),
                Question("Q3 :What is 3 cubed?", "27"),
            ),
        )

    testSame(
        nextState(State(questionBank, Stage.QUESTION, 0, 0), "Yes"),
        State(questionBank, Stage.CORRECT, 1, 1),
        "updates state to CORRECT and increments counters when response is correct",
    )
    testSame(
        nextState(State(questionBank, Stage.QUESTION, 0, 0), "No"),
        State(questionBank, Stage.WRONG, 1, 0),
        "updates state to WRONG and increments count when response is incorrect",
    )
}

@EnabledTest
fun testTerminate() {
    testSame(
        terminate(State(QuestionBank("Perfect Cubes", listOf()), Stage.WRONG, 1, 0)),
        true,
        "terminates when state is WRONG",
    )
    testSame(
        terminate(State(QuestionBank("Perfect Cubes", listOf()), Stage.CORRECT, 1, 1)),
        true,
        "terminates when state is CORRECT",
    )
}

@EnabledTest
fun testTerminateStateToText() {
    testSame(
        terminateStateToText(State(QuestionBank("Perfect Cubes", listOf()), Stage.CORRECT, 1, 1)),
        "Good Job",
        "displays 'Good Job' when state is CORRECT",
    )
}

@EnabledTest
fun testChooseBankFirst() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    testSame(
        chooseBankFirst(banks),
        "1. Geography\n2. Capitals\n3. Perfect Cubes\nEnter your choice",
        "displays available question banks for selection",
    )
}

@EnabledTest
fun testStudyQuestion() {
    val testQState = State(myQuestionBank, Stage.QUESTION, 0, 0)
    val testCState = State(myQuestionBank, Stage.CORRECT, 1, 1)
    val testIState = State(myQuestionBank, Stage.WRONG, 1, 0)
    testSame(
        captureResults(
            {
                (studyQuestion(testQState))
            },
            "Europe",
            "yes",
        ),
        CapturedResult(
            State(myQuestionBank, Stage.CORRECT, 1, 1),
            "Which continent is England in?",
            "The correct answer is: Europe. Did you get that right? Yes or No",
            "Good Job",
        ),
        "Study myQuestionBank 1 Correct Case",
    )
    testSame(
        captureResults(
            {
                (studyQuestion(testQState))
            },
            "Africa",
            "no",
        ),
        CapturedResult(
            State(myQuestionBank, Stage.WRONG, 1, 0),
            "Which continent is England in?",
            "The correct answer is: Europe. Did you get that right? Yes or No",
            "Good Try",
        ),
        "Study myQuestionBank 1 Incorrect Case",
    )
    testSame(
        firstState(testQState),
        "Which continent is England in?",
        "firstState Question",
    )
    testSame(
        firstState(testCState),
        "Invalid stage",
        "firstState else",
    )
    testSame(
        nextState(testQState, "yes"),
        testCState,
        "nextState Answer Correct",
    )
    testSame(
        nextState(testQState, "no"),
        testIState,
        "nextState Answer Correct",
    )
    testSame(
        terminate(testQState),
        false,
        "terminate false",
    )
    testSame(
        terminate(testCState),
        true,
        "terminate true",
    )
    testSame(
        terminateStateToText(testQState),
        "",
        "terminalStateToText else",
    )
    testSame(
        terminateStateToText(testCState),
        "Good Job",
        "terminalStateToText Correct",
    )
    testSame(
        terminateStateToText(testIState),
        "Good Try",
        "terminalStateToText WRONG",
    )
}

@EnabledTest
fun testStudyQuestionBank() {
    val testQState = State(myQuestionBank, Stage.QUESTION, 0, 0)
    val testCState = State(myQuestionBank, Stage.CORRECT, 1, 1)
    val testIState = State(myQuestionBank, Stage.WRONG, 1, 0)

    testSame(
        captureResults(
            {
                studyQuestionBank(testQState.questionPrompt)
            },
            "",
            "no",
            "",
            "yes",
            "",
            "yes",
            "",
        ),
        CapturedResult(
            2,
            "Question:1 Press enter to continue",
            "Which continent is England in?",
            "The correct answer is: Europe. Did you get that right? Yes or No",
            "Good Try",
            "Question:2 Press enter to continue",
            "Which continent is Greece in?",
            "The correct answer is: Europe. Did you get that right? Yes or No",
            "Good Job",
            "Question:3 Press enter to continue",
            "Which continent is Eygpt in?",
            "The correct answer is: Africa. Did you get that right? Yes or No",
            "Good Job",
            "You got 2 answer(s) correctly. Bye",
        ),
        "studyQuestionBank myQuestionBank",
    )

    testSame(
        bankFirstState(testQState),
        "Question:1 Press enter to continue",
        "bankFirstState for initial state",
    )

    val terminatedState = State(myQuestionBank, Stage.QUESTION, 3, 2)
    testSame(
        bankTerminate(terminatedState),
        true,
        "bankTerminate true when reaching end of question bank",
    )
    testSame(
        bankTerminate(testQState),
        false,
        "bankTerminate false when questions are still available",
    )
    testSame(
        bankterminalStateToText(terminatedState),
        "You got 2 answer(s) correctly. Bye",
        "bankterminalStateToText correct message",
    )
}

@EnabledTest
fun testChooseBank() {
    val testedFinalBanks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))

    testSame(
        captureResults({
            chooseBank(listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3)))
        }, "4", "1"),
        CapturedResult(
            myQuestionBank,
            "Welcome to Question Time. Choose one of the options!",
            "1. Geography",
            "2. Capitals",
            "3. Perfect Cubes",
            "Enter your choice",
            "Welcome to Question Time. Choose one of the options!",
            "1. Geography",
            "2. Capitals",
            "3. Perfect Cubes",
            "Enter your choice",
            "Welcome to Question Time. Choose one of the options!",
            "1. Geography",
            "Enter your choice",
        ),
        "chooseBank test with invalid and valid choice sequences",
    )

    testSame(
        chooseBankFirst(testedFinalBanks),
        "1. Geography\n2. Capitals\n3. Perfect Cubes\nEnter your choice",
        "shows correct bank list prompt",
    )

    testSame(
        chooseNextState(testedFinalBanks, "1"),
        listOf(myQuestionBank),
        "chooseNextState selects correct bank with valid input",
    )

    testSame(
        captureResults({
            chooseNextState(testedFinalBanks, "10")
        }),
        CapturedResult(
            testedFinalBanks,
            "Invalid Choice",
        ),
        "invalid input shows invalid choice",
    )

    testSame(
        chooseTerminalState(testedFinalBanks),
        false,
        "chooseTerminalState returns false when multiple banks are available",
    )

    val oneBankList = listOf(QuestionBank("questionBankExample", listOf(Question("0", "0"))))
    testSame(
        chooseTerminalState(oneBankList),
        true,
        "chooseTerminalState returns true with only one bank available",
    )

    testSame(
        chooseBankFirst(oneBankList),
        "1. questionBankExample\nEnter your choice",
        "chooseBankFirst shows the selected bank message",
    )
}

runEnabledTests(this)
main()

import khoury.EnabledTest
import khoury.fileExists
import khoury.fileReadAsList
import khoury.input
import khoury.isAnInteger
import khoury.reactConsole
import khoury.testSame
import khoury.runEnabledTests
data class Question(
    val question: String,
    val answer: String,
)
val q1 = Question("Which continent is England in?", "Europe")
val q2 = Question("Which continent is Greece in?", "Europe")
val q3 = Question("Which continent is Eygpt in?", "Africa")
data class QuestionBank(
    val name: String,
    val question: List<Question>,
)
val myQuestionBank = QuestionBank("Geography", listOf(q1, q2, q3))
fun cubes(value: Int): QuestionBank {
    val questions = (1..value).map { i -> Question("Q$i :What is $i cubed?", "${ i * i * i}") }
    return QuestionBank("Perfect Cubes", questions)
}
fun questionToString(question: Question): String = "${question.question}|${question.answer}"
fun stringToQuestion(s: String): Question {
    val citiesList = s.split("|")
    return Question(citiesList[0], citiesList[1])
}
fun readQuestionBank(path: String): QuestionBank =
    if (fileExists(path)) {
        val pathToFile = fileReadAsList(path).map(::stringToQuestion)
        QuestionBank("Captials", pathToFile)
    } else {
        QuestionBank("Empty", emptyList())
    }
fun isCorrect(answer: String): Boolean {
    val capitalLetter = answer.uppercase()
    return capitalLetter.startsWith("Y")
}
enum class Stage { CORRECT, WRONG, QUESTION }
data class State(
    val questionPrompt: QuestionBank,
    val currentStage: Stage,
    val count: Int,
    val correctAnswers: Int,
)
fun firstState(state: State): String =
    when (state.currentStage) {
        Stage.QUESTION -> state.questionPrompt.question[state.count].question
        else -> "Invalid stage"
    }
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
fun terminate(state: State): Boolean = state.currentStage == Stage.CORRECT || state.currentStage == Stage.WRONG
fun terminateStateToText(state: State): String =
    when (state.currentStage) {
        Stage.CORRECT -> "Good Job"
        Stage.WRONG -> "Good Try"
        else -> ""
    }
fun studyQuestion(question: State): State =
    reactConsole(
        initialState = question,
        stateToText = ::firstState,
        nextState = ::nextState,
        isTerminalState = ::terminate,
        terminalStateToText = ::terminateStateToText,
    )
fun bankFirstState(bank: State): String = "Question:" + (bank.count + 1) + " Press enter to continue"
fun bankNextState(
    bankState: State,
    input: String,
): State {
    val correctAnswers = studyQuestion(bankState).correctAnswers
    return(bankState.copy(currentStage = Stage.QUESTION, count = bankState.count + 1, correctAnswers = correctAnswers))
}
fun bankTerminate(state: State): Boolean = state.count >= 3
fun bankterminalStateToText(state: State): String = "You got ${state.correctAnswers} answer(s) correctly. Bye"
fun studyQuestionBank(bank: QuestionBank) {
    reactConsole(
        initialState = State(bank, Stage.QUESTION, 0, 0),
        stateToText = ::bankFirstState,
        nextState = ::bankNextState,
        isTerminalState = ::bankTerminate,
        terminalStateToText = ::bankterminalStateToText,
    )
}
fun f(
    index: Int,
    bank: QuestionBank,
): String = "${ index + 1 }. ${bank.name}"
fun chooseBankFirst(options: List<QuestionBank>): String {
    println("Welcome to Question Time. Choose one of the options!")
    return options.mapIndexed(::f).joinToString("\n") + "\nEnter your choice"
}
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
fun chooseTerminalState(banks: List<QuestionBank>): Boolean = banks.size == 1
fun chooseBank(questionLists: List<QuestionBank>): QuestionBank =
    reactConsole(
        initialState = questionLists,
        stateToText = ::chooseBankFirst,
        nextState = ::chooseNextState,
        isTerminalState = ::chooseTerminalState,
        terminalStateToText = ::chooseBankFirst,
    )[0]
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
                Question("Q3 :What is 3 cubed?", "27")
            )
        ),
        "creates a question bank for perfect cubes up to 3"
    )
}

@EnabledTest
fun testQuestionToString() {
    testSame(
        questionToString(Question("Which continent is England in?", "Europe")),
        "Which continent is England in?|Europe",
        "converts a Question to a correctly formatted string"
    )
    testSame(
        questionToString(Question("Which continent is Greece in?", "Europe")),
        "Which continent is Greece in?|Europe",
        "converts a Question to a correctly formatted string"
    )
}

@EnabledTest
fun testStringToQuestion() {
    testSame(
        stringToQuestion("What is the capital of England?|London"),
        Question("What is the capital of England?", "London"),
        "parses a string into a Question data class"
    )
    testSame(
        stringToQuestion("What is the capital of Spain?|Madrid"),
        Question("What is the capital of Spain?", "Madrid"),
        "parses a string into a Question data class"
    )
    testSame(
        stringToQuestion("What is the capital of Ecuador?|Quito"),
        Question("What is the capital of Ecuador?", "Quito"),
        "parses a string into a Question data class"
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
                Question("What is the capital of Ecuador?", "Quito")
            )
        ),
        "reads a question bank from an existing file"
    )
    testSame(
        readQuestionBank("empty.txt"),
        QuestionBank("Empty", emptyList()),
        "returns an empty question bank when file does not exist"
    )
}

@EnabledTest
fun testIsCorrect() {
    testSame(isCorrect("Yes"), true, "correctly identifies 'Yes' as a correct response")
    testSame(isCorrect("No"), false, "correctly identifies 'No' as an incorrect response")
}

@EnabledTest
fun testFirstState() {
    val questionBank = QuestionBank(
        "Perfect Cubes",
        listOf(
            Question("Q1 :What is 1 cubed?", "1"),
            Question("Q2 :What is 2 cubed?", "8"),
            Question("Q3 :What is 3 cubed?", "27")
        )
    )

    testSame(
        firstState(State(questionBank, Stage.QUESTION, 0, 0)),
        "Q1 :What is 1 cubed?",
        "displays the first question in the bank"
    )
    testSame(
        firstState(State(questionBank, Stage.QUESTION, 1, 0)),
        "Q2 :What is 2 cubed?",
        "displays the second question in the bank"
    )
    testSame(
        firstState(State(questionBank, Stage.CORRECT, 0, 0)),
        "Invalid stage",
        "displays 'Invalid stage' when state is not QUESTION"
    )
}

@EnabledTest
fun testNextState() {
    val questionBank = QuestionBank(
        "Perfect Cubes",
        listOf(
            Question("Q1 :What is 1 cubed?", "1"),
            Question("Q2 :What is 2 cubed?", "8"),
            Question("Q3 :What is 3 cubed?", "27")
        )
    )

    testSame(
        nextState(State(questionBank, Stage.QUESTION, 0, 0), "Yes"),
        State(questionBank, Stage.CORRECT, 1, 1),
        "updates state to CORRECT and increments counters when response is correct"
    )
    testSame(
        nextState(State(questionBank, Stage.QUESTION, 0, 0), "No"),
        State(questionBank, Stage.WRONG, 1, 0),
        "updates state to WRONG and increments count when response is incorrect"
    )
}

@EnabledTest
fun testTerminate() {
    testSame(
        terminate(State(QuestionBank("Perfect Cubes", listOf()), Stage.WRONG, 1, 0)),
        true,
        "terminates when state is WRONG"
    )
    testSame(
        terminate(State(QuestionBank("Perfect Cubes", listOf()), Stage.CORRECT, 1, 1)),
        true,
        "terminates when state is CORRECT"
    )
}

@EnabledTest
fun testTerminateStateToText() {
    testSame(
        terminateStateToText(State(QuestionBank("Perfect Cubes", listOf()), Stage.CORRECT, 1, 1)),
        "Good Job",
        "displays 'Good Job' when state is CORRECT"
    )
}
@EnabledTest
fun testChooseBankFirst() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    testSame(
        chooseBankFirst(banks),
        "Welcome to Question Time. Choose one of the options!\n1. Geography\n2. Capitals\n3. Perfect Cubes\nEnter your choice",
        "displays available question banks for selection"
    )
}

/*
@EnabledTest
fun testCubes() {
    testSame(
        cubes(3),
        QuestionBank(
            "Perfect Cubes",
            listOf(Question("Q1 :What is 1 cubed?", "1"), Question("Q2 :What is 2 cubed?", "8"), Question("Q3 :What is 3 cubed?", "27")),
        ),
        "makes 3 questions",
    )
}
@EnabledTest
fun testQuestionToString() {
    testSame(questionToString(Question(??)), "${question.question}|${question.answer}","")
    testSame(questionToString(Question(??)), "${question.question}|${question.answer}","")
}
@EnabledTest
fun testStringToQuestion() {
    testSame(
        stringToQuestion("What is the capital of England?|London"),
        Question("What is the capital of England?", "London"),
        "turns a string into Question data class",
    )
    testSame(
        stringToQuestion("What is the capital of Spain?|Madrid"),
        Question("What is the capital of Spain?", "Madrid"),
        "turns a string into Question data class",
    )
    testSame(
        stringToQuestion("What is the capital of Ecuador?|Quito"),
        Question("What is the capital of Ecuador?", "Quito"),
        "turns a string into Question data class",
    )
}
@EnabledTest
fun testReadQuestionBank() {
    testSame(
        readQuestionBank("my-question.txt"),
        QuestionBank(
            "Capitals",
            listOf(
                Question("What is the capital of England?", "London"),
                Question("What is the capital of Spain?", "Madrid"),
                Question("What is the capital of Ecuador?", "Quito"),
            ),
        ),
        "File Exists",
    )
    testSame(readQuestionBank("empty.txt"), QuestionBank("Empty", emptylist()), "Empty file")
}
@EnabledTest
fun testIsCorrect() {
    testSame(isCorrect("Yes"), true, "tests if input starts with y")
    testSame(isCorrect("No"), false, "tests if input starts with y")
}
@EnabledTest
fun testFirstState() {
    testSame(
        firstState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.QUESTION,
                0,
                0,
            ),
        ),
        "Q1 :What is 1 cubed?",
        "takes count from state and uses the index to displays question.answer",
    )
    testSame(
        firstState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.QUESTION,
                1,
                0,
            ),
        ),
        "Q2 :What is 2 cubed?",
        "takes count from state and uses the index to displays question.answer",
    )
    testSame(
        firstState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.QUESTION,
                2,
                0,
            ),
        ),
        "Q3 :What is 3 cubed?",
        "takes count from state and uses the index to displays question.answer",
    )
    testSame(
        firstState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.CORRECT,
                0,
                0,
            ),
        ),
        "Invalid Stage",
        "takes count from state and uses the index to displays question.answer",
    )
    testSame(
        firstState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.WRONG,
                0,
                0,
            ),
        ),
        "Invalid Stage",
        "takes count from state and uses the index to displays question.answer",
    )
}
@EnabledTest
fun testNextState() {
    testSame(
        nextState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27")
                    )
                ),
            Stage.QUESTION,
            0,
            0
        )
        ),
        State(
            QuestionBank(
                "Perfect Cubes",
                listOf(
                    Question("Q1 :What is 1 cubed?", "1"),
                    Question("Q2 :What is 2 cubed?", "8"),
                    Question("Q3 :What is 3 cubed?", "27")
                )
            ),
            Stage.CORRECT,
            1,
            1
        ),
        "Checks if answer was correct or incorrect and adds to count and might add to correct answer"
    )
    testSame(
        nextState(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27")
                    )
                ),
            Stage.QUESTION,
            0,
            0
        )
        ),
        State(
            QuestionBank(
                "Perfect Cubes",
                listOf(
                    Question("Q1 :What is 1 cubed?", "1"),
                    Question("Q2 :What is 2 cubed?", "8"),
                    Question("Q3 :What is 3 cubed?", "27")
                )
            ),
            Stage.WRONG,
            1,
            0
        ),
        "Checks if answer was correct or incorrect and adds to count and might add to correct answer"
    )
}
@EnabledTest

fun testTerminate() {
    testSame(
        terminate(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27")
                    )
                ),
                Stage.WRONG,
                1,
                0
            )
        ),
        true,
        "checks to see if stage has reacehd correct or wrong",
    )
    testSame(
        terminate(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.CORRECT,
                1,
                1,
            ),
        ),
        true,
        "checks to see if stage has reacehd correct or wrong",
    )
}
@EnabledTest
fun testTerminateStateToText() {
    testSame(
        terminateStateToText(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.CORRECT,
                1,
                1,
            ),
        ),
        "Good Job",
        "checks to see if what to print when stage is correct or wrong",
    )
    testSame(
        terminateStateToText(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.WRONG,
                1,
                0,
            ),
        ),
        "Good Try",
        "checks to see if what to print when stage is correct or wrong",
    )
    testSame(
        terminateStateToText(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.QUESTION,
                1,
                0,
            ),
        ),
        "",
        "checks to see if what to print when stage is correct or wrong",
    )
}
@EnabledTest
fun testStudyQuestion() {
    testSame(
        studyQuestion(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.QUESTION,
                0,
                0,
            ),
        ),
        State(
            QuestionBank(
                "Perfect Cubes",
                listOf(
                    Question("Q1 :What is 1 cubed?", "1"),
                    Question("Q2 :What is 2 cubed?", "8"),
                    Question("Q3 :What is 3 cubed?", "27"),
                ),
            ),
            Stage.CORRECT,
            1,
            1,
        ),
        "runs react console to see if user got a singular question right or wrong",
    )
    testSame(
        studyQuestion(
            State(
                QuestionBank(
                    "Perfect Cubes",
                    listOf(
                        Question("Q1 :What is 1 cubed?", "1"),
                        Question("Q2 :What is 2 cubed?", "8"),
                        Question("Q3 :What is 3 cubed?", "27"),
                    ),
                ),
                Stage.QUESTION,
                0,
                0,
            ),
        ),
        State(
            QuestionBank(
                "Perfect Cubes",
                listOf(
                    Question("Q1 :What is 1 cubed?", "1"),
                    Question("Q2 :What is 2 cubed?", "8"),
                    Question("Q3 :What is 3 cubed?", "27"),
                ),
            ),
            Stage.WRONG,
            1,
            0,
        ),
        "runs react console to see if user got a singular question right or wrong",
    )
}
@EnabledTest
fun testBankFirstState() {
    testSame(bankFirstState(State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),"Question:"+(bank.count+1)+" Press enter to continue",)
    testSame(bankFirstState(),,)
}
@EnabledTest
fun testBankNextState() {
    testSame(bankNextState(State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),)
    testSame(bankNextState(State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),)
}
@EnabledTest
fun testBankTerminate() {
    testSame(terminate(State(QuestionBank("Perfect Cubes",listOf(Question("Q1 :What is 1 cubed?", "1"),Question("Q2 :What is 2 cubed?", "8"),Question("Q3 :What is 3 cubed?", "27")))),Stage.WRONG,1,0),false,"checks to see if count is at three")
    testSame(terminate(State(QuestionBank("Perfect Cubes",listOf(Question("Q1 :What is 1 cubed?", "1"),Question("Q2 :What is 2 cubed?", "8"),Question("Q3 :What is 3 cubed?", "27")))),Stage.CORRECT,2,1),false,"checks to see if count is at three")
    testSame(terminate(State(QuestionBank("Perfect Cubes",listOf(Question("Q1 :What is 1 cubed?", "1"),Question("Q2 :What is 2 cubed?", "8"),Question("Q3 :What is 3 cubed?", "27")))),Stage.CORRECT,3,2),true,"checks to see if count is at three")
}
@EnabledTest
fun testBankTerminalStateToText() {
    testSame(bankTerminalStateToText(State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),"You got ??? answer(s) correctly. Bye",)
    testSame(bankTerminalStateToText(State(QuestionBank("???",listOf(Question("",""),Question("",""),Question("",""))),Stage.???,Int,Int)),"You got ??? answer(s) correctly. Bye",)
}
@EnabledTest
fun testStudyQuestionBank() {
    testSame(studyQuestionBank(QuestionBank("???",Question("",""))),??? )
    testSame(studyQuestionBank(QuestionBank("???",Question("",""))),??? )
}
@EnabledTest
fun testF() {
    testSame(f(Int,QuestionBank("???",Question("",""))),"${index +1}. ${bank.name}",)
    testSame(f(Int,QuestionBank("???",Question("",""))),"${index +1}. ${bank.name}",)
}
@EnabledTest
fun testChooseBankFirst() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    testSame(chooseBankFirst(banks),"Welcome to Question Time. Choose one of the options!\n\nEnter your choice","Starts the game by accepting questions as a parameter")
}
@EnabledTest
fun testChooseNextState() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    testSame(chooseNextState(banks,"1"),List<QuestionBank>,"choose one of the options")
    testSame(chooseNextState(banks,"2"),List<QuestionBank>,"choose one of the options")
    testSame(chooseNextState(banks,"3"),List<QuestionBank>,"choose one of the options")
    testSame(chooseNextState(banks,"9"),banks,"did not choose an option")
}
//
fun testChooseTerminateState() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    testSame(chooseTerminalState(banks),true, "choose one of the options")
    testSame(chooseTerminalState(banks),false,"did not choose an option")
}
fun testChooseBank() {
    val banks = listOf(myQuestionBank, readQuestionBank("my-questions.txt"), cubes(3))
    testSame(chooseBank(banks),cubes(3),"choose Perfect Cubes")
    testSame(chooseBank(banks),readQuestionBank("my-questions.txt"),"choose Capitals")
    testSame(chooseBank(banks),myQuestionBank,"choose Geography")
}
runEnabledTests(this)
main()

            "Question:1 Press enter to continue",
            "What river runs alongside Saint Louis?",
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
        "studyQuestionBank myQuestionBank"
    )*/
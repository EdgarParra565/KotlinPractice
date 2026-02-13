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

// Example tagged questions for tests
val exQ1 = Question("What is 3x3?", "9")
val exQ2 = Question("Who killed Abraham Lincoln?", "John Wilkes Booth")
val exQ3 = Question("How many continents are there?", "7")
val tExQ1 = TaggedQuestion(exQ1, listOf("Math", "Hard"))
val tExQ2 = TaggedQuestion(exQ2, emptyList<String>())

/*** Step 1: Questions ***/
// data class called question that contains two strings that are a question and a answer
data class Question(
    val question: String,
    val answer: String,
)

// Interface that represnts tagged question and has methods to check the tags and to format the tagged question
interface TaggedQ {
    fun taggedAs(tag: String): Boolean

    fun format(): String
}

// data class for a tagged question with a question answer pair including a list of tags that are strings
data class TaggedQuestion(
    val questionAnswer: Question,
    val tags: List<String>,
) : TaggedQ {
    val QASEPARATOR = "|"
    val TAGSEPARATOR = ","

// checks if the tagged question has a tag
    override fun taggedAs(tag: String): Boolean {
        val newTags = this.tags.map({ it.lowercase() })
        return (tag.lowercase() in newTags)
    }

// formats tagged question into a combined string
    override fun format(): String {
        if (tags.isEmpty()) {
            return ("${questionAnswer.question}$QASEPARATOR${questionAnswer.answer}")
        } else {
            val str = tags.fold(QASEPARATOR, { a: String, b -> a + b + TAGSEPARATOR })
            val str2 = str.substring(0, str.length - 1)
            return "${questionAnswer.question}$QASEPARATOR${questionAnswer.answer}$str2"
        }
    }
}

// data class for a tagged question bank that has a name for a list of tagged questions
data class TaggedQuestionBank(
    val name: String,
    val questions: List<TaggedQuestion>,
)

@EnabledTest
fun testTaggedQuestion() {
    testSame(tExQ1.taggedAs("math"), true, "taggedAs True")
    testSame(tExQ1.taggedAs("easy"), false, "taggedAs false")

    testSame(tExQ1.format(), "What is 3x3?|9|Math,Hard", "Format")
    testSame(tExQ2.format(), "Who killed Abraham Lincoln?|John Wilkes Booth", "Format empty list")
}

// function that takes a string and formats it into a TaggedQuestion using commas and bar to break up the string
fun stringToTaggedQuestion(str: String): TaggedQuestion {
    val lst = str.split("|")
    val lst2 = lst[2].split(",")
    return (TaggedQuestion(Question(lst[0], lst[1]), lst2))
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
fun testConversions() {
    testSame(
        stringToTaggedQuestion("What is 3x3?|9|Math,Hard"),
        TaggedQuestion(Question("What is 3x3?", "9"), listOf("Math", "Hard")),
        "stringToTaggedQuestion",
    )
    testSame(
        readTaggedQuestionBank("music.txt"),
        TaggedQuestionBank(
            "Music",
            listOf(
                TaggedQuestion(Question("How many members were in Wutang-Clan?", "10"), listOf("Music", "Medium")),
                TaggedQuestion(Question("What year was Tupac Killed?", "1996"), listOf("Music", "Hard")),
                TaggedQuestion(Question("Where is King Von From?", "O-Block"), listOf("Music", "Easy")),
            ),
        ),
        "readTaggedQuestionBank",
    )
}

/*** Step 3: Question bank design ***/

/**
 * The bank is either completed,
 * showing a question or showing
 * an answer
 */
enum class QuestionBankState { COMPLETED, QUESTIONING, ANSWERING }

/**
 * Basic functionality of any question bank
 */
interface IQuestionBank {
    /**
     * Returns the state of a question bank.
     */
    fun getState(): QuestionBankState

    /**
     * Returns the currently visible text (or null if completed).
     */
    fun getText(): String?

    /**
     * Returns the number of question-answer pairs.
     * (Size does not change when a question is put
     * to the end of the question bank.)
     */
    fun getSize(): Int

    /**
     * Shifts from question to answer. If not QUESTIONING,
     * returns the same IQuestionBank.
     */
    fun show(): IQuestionBank

    /**
     * Shifts from an answer to the next question (or completion).
     * If the current question was answered correctly, it discards
     * it. Otherwise it cycles the question to the end.
     *
     * If not ANSWERING, returns the same IQuestionBank.
     */
    fun next(correct: Boolean): IQuestionBank
}

// data class that represents a bank of questions from a list of tagged questions
data class ListBasedQuestionBank(
    val questions: TaggedQuestionBank,
    private val state: QuestionBankState,
) : IQuestionBank {

// gets the current state of the question bank
    override fun getState(): QuestionBankState = this.state

// displays text depending on the state of the question bank or nothing if the bank is completed
    override fun getText(): String? {
        if (this.state == QuestionBankState.COMPLETED) {
            return null
        } else if (this.state == QuestionBankState.QUESTIONING) {
            return questions.questions[0].questionAnswer.question
        } else {
            println("The correct Answer was ${questions.questions[0].questionAnswer.answer}")
            return "Were you correct?"
        }
    }

// gets the number of questions left
    override fun getSize(): Int = this.questions.questions.size

// transitions the question bank from question to answer or else it will stay in the same state
    override fun show(): IQuestionBank {
        if (this.state == QuestionBankState.QUESTIONING) {
            return ListBasedQuestionBank(
                TaggedQuestionBank(
                    this.questions.name,
                    this.questions.questions,
                ),
                QuestionBankState.ANSWERING,
            )
        }
        return this
    }

// goes to the next question or changes the state depending on if the question was answer correctly
    override fun next(correct: Boolean): IQuestionBank {
        if (this.questions.questions.size == 1) {
            if (correct) {
                return ListBasedQuestionBank(
                    TaggedQuestionBank(
                        this.questions.name,
                        emptyList<TaggedQuestion>(),
                    ),
                    QuestionBankState.COMPLETED,
                )
            } else {
                return ListBasedQuestionBank(
                    TaggedQuestionBank(
                        this.questions.name,
                        this.questions.questions,
                    ),
                    QuestionBankState.QUESTIONING,
                )
            }
        }
        if (correct) {
            return ListBasedQuestionBank(
                TaggedQuestionBank(
                    this.questions.name,
                    this.questions.questions.drop(1),
                ),
                QuestionBankState.QUESTIONING,
            )
        }
        return ListBasedQuestionBank(
            TaggedQuestionBank(
                this.questions.name,
                this.questions.questions.drop(1) + questions.questions[0],
            ),
            QuestionBankState.QUESTIONING,
        )
    }
}

// step 3.2
// data class that represents a question bank that generates questions and answers
data class AutoGeneratedQuestionBank<T>(
    val generatorQuestion: (T) -> String,
    val generatorAnswer: (T) -> String,
    val sequence: List<T>,
    private val state: QuestionBankState,
) : IQuestionBank {

// gets the current state of the question bank
    override fun getState(): QuestionBankState = this.state

// displays text based on which state the question bank is in
    override fun getText(): String? {
        if (this.state == QuestionBankState.COMPLETED) {
            return null
        }
        if (this.state == QuestionBankState.QUESTIONING) {
            return generatorQuestion(sequence.first())
        }
        return "The correct Answer is ${generatorAnswer(sequence.first())}"
    }

// gets the number of questions left in the bank
    override fun getSize(): Int = this.sequence.size

// transitions the question bank from questioning to answering or else will keep the state the same
    override fun show(): IQuestionBank {
        if (this.state == QuestionBankState.QUESTIONING) {
            return AutoGeneratedQuestionBank(
                this.generatorQuestion,
                this.generatorAnswer,
                this.sequence,
                QuestionBankState.ANSWERING,
            )
        }
        return this
    }

// goes to the next question or changes the state depending on if the question was answer correctly
    override fun next(correct: Boolean): IQuestionBank {
        if (this.sequence.size == 1) {
            if (correct) {
                return AutoGeneratedQuestionBank(
                    this.generatorQuestion,
                    this.generatorAnswer,
                    emptyList(),
                    QuestionBankState.COMPLETED,
                )
            } else {
                return this
            }
        }
        if (correct) {
            return AutoGeneratedQuestionBank(
                this.generatorQuestion,
                this.generatorAnswer,
                this.sequence.drop(1),
                QuestionBankState.QUESTIONING,
            )
        }
        return AutoGeneratedQuestionBank(
            this.generatorQuestion,
            this.generatorAnswer,
            this.sequence.drop(1) + sequence[0],
            QuestionBankState.QUESTIONING,
        )
    }
}

@EnabledTest
fun testPart3() {
    val exQ1 = Question("What is 3x3?", "9")

    val testBank1 =
        ListBasedQuestionBank(
            TaggedQuestionBank(
                "exBank1",
                listOf(TaggedQuestion(exQ1, listOf("Math", "Hard"))),
            ),
            QuestionBankState.QUESTIONING,
        )

    val testBank3 =
        ListBasedQuestionBank(
            TaggedQuestionBank(
                "exBank2",
                listOf(TaggedQuestion(exQ1, listOf("Math", "Hard")), TaggedQuestion(exQ2, listOf("History", "Easy"))),
            ),
            QuestionBankState.QUESTIONING,
        )

    val testBank2 =
        AutoGeneratedQuestionBank(
            generatorQuestion = { it -> "What is the cube of $it?" },
            generatorAnswer = { it -> "${it * it * it}" },
            sequence = listOf(1, 2, 3),
            QuestionBankState.QUESTIONING,
        )

    val testBank4 =
        AutoGeneratedQuestionBank(
            generatorQuestion = { it -> "What is the cube of $it?" },
            generatorAnswer = { it -> "${it * it * it}" },
            sequence = listOf(1),
            QuestionBankState.QUESTIONING,
        )

    testSame(testBank1.getState(), QuestionBankState.QUESTIONING, "ListBasedQuestionBank getState")
    testSame(testBank1.getSize(), 1, "ListBasedQuestionBank getSize")
    testSame(testBank1.getText(), "What is 3x3?", "ListBasedQuestionBank getText, QUESTIONING")
    testSame(
        captureResults({
            ListBasedQuestionBank(TaggedQuestionBank(testBank1.questions.name, listOf(tExQ1)), QuestionBankState.ANSWERING).getText()
        }),
        CapturedResult(
            "Were you correct?",
            "The correct Answer was 9",
        ),
        "ListBasedQuestionBank getText, ANSWERING",
    )
    testSame(
        ListBasedQuestionBank(
            TaggedQuestionBank(testBank1.questions.name, emptyList<TaggedQuestion>()),
            QuestionBankState.COMPLETED,
        ).getText(),
        null,
        "ListBasedQuestionBank getText, COMPLETED",
    )
    testSame(
        testBank1.show(),
        ListBasedQuestionBank(
            TaggedQuestionBank(
                testBank1.questions.name,
                listOf(TaggedQuestion(exQ1, listOf("Math", "Hard"))),
            ),
            QuestionBankState.ANSWERING,
        ),
        "ListBasedQuestionBank show, QUESTIONING",
    )
    testSame(
        ListBasedQuestionBank(
            TaggedQuestionBank(
                testBank1.questions.name,
                listOf(TaggedQuestion(exQ1, listOf("Math", "Hard"))),
            ),
            QuestionBankState.ANSWERING,
        ).show(),
        ListBasedQuestionBank(
            TaggedQuestionBank(
                testBank1.questions.name,
                listOf(TaggedQuestion(exQ1, listOf("Math", "Hard"))),
            ),
            QuestionBankState.ANSWERING,
        ),
        "ListBasedQuestionBank show, ANSWERING/COMPLETED",
    )
    testSame(
        testBank1.next(true),
        ListBasedQuestionBank(TaggedQuestionBank(testBank1.questions.name, emptyList<TaggedQuestion>()), QuestionBankState.COMPLETED),
        "ListBasedQuestionBank next Correct, list of 1",
    )
    testSame(
        testBank1.next(false),
        ListBasedQuestionBank(
            TaggedQuestionBank(
                testBank1.questions.name,
                listOf(TaggedQuestion(exQ1, listOf("Math", "Hard"))),
            ),
            QuestionBankState.QUESTIONING,
        ),
        "ListBasedQuestionBank next Incorrect, list of 1",
    )
    testSame(
        testBank3.next(true),
        ListBasedQuestionBank(
            TaggedQuestionBank(testBank3.questions.name, listOf(TaggedQuestion(exQ2, listOf("History", "Easy")))),
            QuestionBankState.QUESTIONING,
        ),
        "ListBasedQuestionBank next Correct, list of 2",
    )
    testSame(
        testBank3.next(false),
        ListBasedQuestionBank(
            TaggedQuestionBank(
                testBank3.questions.name,
                listOf(TaggedQuestion(exQ2, listOf("History", "Easy")), TaggedQuestion(exQ1, listOf("Math", "Hard"))),
            ),
            QuestionBankState.QUESTIONING,
        ),
        "ListBasedQuestionBank next Incorrect, list of 2",
    )

    testSame(testBank2.getState(), QuestionBankState.QUESTIONING, "AutoGeneratedQuestionBank getState")
    testSame(testBank2.getSize(), 3, "AutoGeneratedQuestionBank getSize")
    testSame(testBank2.getText(), "What is the cube of 1?", "AutoGeneratedQuestionBank getText, QUESTIONING")
    testSame(
        AutoGeneratedQuestionBank(
            testBank2.generatorQuestion,
            testBank2.generatorAnswer,
            emptyList<Int>(),
            QuestionBankState.COMPLETED,
        ).getText(),
        null,
        "AutoGeneratedQuestionBank getText COMPLETED",
    )
    testSame(
        AutoGeneratedQuestionBank(
            testBank2.generatorQuestion,
            testBank2.generatorAnswer,
            testBank2.sequence,
            QuestionBankState.ANSWERING,
        ).getText(),
        "The correct Answer is 1",
        "AutoGeneratedQuestionBank getText ANSWERING",
    )

    testSame(
        testBank2.show(),
        AutoGeneratedQuestionBank(testBank2.generatorQuestion, testBank2.generatorAnswer, listOf(1, 2, 3), QuestionBankState.ANSWERING),
        "AutoGeneratedQuestionBank show QUESTIONING",
    )
    testSame(
        AutoGeneratedQuestionBank(testBank2.generatorQuestion, testBank2.generatorAnswer, listOf(1), QuestionBankState.ANSWERING).show(),
        AutoGeneratedQuestionBank(testBank2.generatorQuestion, testBank2.generatorAnswer, listOf(1), QuestionBankState.ANSWERING),
        "AutoGeneratedQuestionBank show ANSWERING",
    )
    testSame(
        testBank2.next(true),
        AutoGeneratedQuestionBank(testBank2.generatorQuestion, testBank2.generatorAnswer, listOf(2, 3), QuestionBankState.QUESTIONING),
        "AutoGeneratedQuestionBank next Correct, list of 3",
    )
    testSame(
        testBank2.next(false),
        AutoGeneratedQuestionBank(testBank2.generatorQuestion, testBank2.generatorAnswer, listOf(2, 3, 1), QuestionBankState.QUESTIONING),
        "AutoGeneratedQuestionBank next Incorrect, list of 3",
    )
    testSame(
        testBank4.next(true),
        AutoGeneratedQuestionBank(testBank4.generatorQuestion, testBank4.generatorAnswer, emptyList<Int>(), QuestionBankState.COMPLETED),
        "AutoGeneratedQuestionBank next Correct, list of 1",
    )
    testSame(
        testBank4.next(false),
        AutoGeneratedQuestionBank(testBank4.generatorQuestion, testBank4.generatorAnswer, listOf(1), QuestionBankState.QUESTIONING),
        "AutoGeneratedQuestionBank next Incorrect, list of 1",
    )
}

// interface that represents a menu option that has a title
interface IMenuOption {
    fun getTitle(): String
}

/**
 * A menu option with a single value and name.
 */
data class NamedMenuOption<T>(
    val option: T,
    val name: String,
) : IMenuOption {

// gets the title of a menu option
    override fun getTitle(): String = name
}

// function that diplsyas choices and gets a user respone from a list of options
// can return a option or a null value
fun <T : IMenuOption> chooseMenu(options: List<T>): T? =
    reactConsole(
        initialState = options,
        stateToText = ::possibleBanks,
        nextState = ::validNum,
        isTerminalState = ::oneBank,
        terminalStateToText = ::possibleBanks,
    )[0]

// function that takes a undex and a menu option and outputs a string
fun <T : IMenuOption> bankAndIndex(
    index: Int,
    qBank: T?,
): String = (index + 1).toString() + ". " + qBank?.getTitle()

// takes a list of menu options and turns it into a string depending on the list size
fun <T : IMenuOption> possibleBanks(list: List<T?>): String {
    if (list.size == 1) {
        if (list[0] == null) {
            return "Bye."
        } else {
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
        } else if (inputInt == 0) {
            return listOf(null)
        } else {
            return listOf(list[inputInt - 1])
        }
    } else {
        return list
    }
}

// function that returns true if there is only one element in a supplied list of NamedMenuOptions
fun <T> oneBank(list: List<T?>): Boolean {
    if (list.size == 1) {
        return true
    } else {
        return false
    }
}

@EnabledTest
fun testChooseMenu() {
    val options = mutableListOf(sportsNamedOption, musicNamedMenu, cubesNamedMenu)

    testSame(bankAndIndex(1, NamedMenuOption(1, "name")), "2. name", "bankAndIndex")
    testSame(
        captureResults({ possibleBanks(options) }),
        CapturedResult(
            "Return your choice: ",
            "Enter 1, 2, ..., or 0 to quit:",
            "1. Sports",
            "2. Music",
            "3. Cubes",
        ),
        "possibleBanks",
    )
    testSame(validNum(options, "0"), listOf(null), "validNum quit")
    testSame(validNum(options, "1"), listOf(sportsNamedOption), "validNum valid")
    testSame(validNum(options, "4"), options, "validNum invalid")
    testSame(oneBank(options), false, "oneBank false")
    testSame(oneBank(listOf(sportsNamedOption)), true, "oneBank true")

    /**
     * Individual examples, as well as a list of those examples
     * (for testing purposes only)
     */
    val anApple = NamedMenuOption(1, "Apple")
    val aBanana = NamedMenuOption(2, "Banana")
    val fruits = listOf(anApple, aBanana)

    // Some useful outputs
    val prompt = "Enter 1, 2, ..., or 0 to quit:"
    val quit = "Bye."

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

/*** Step 5: Sentiment analysis ***/

// TEST

// uses levnshtein distance formula to find the distance between two strings
fun levenshteinDistance(
    a: String,
    b: String,
): Int {
    val aLength = a.length
    val bLength = b.length

    if (aLength == 0) {
        return bLength
    }
    if (bLength == 0) {
        return aLength
    }

// intializes two mutable rows
    var previousRow = MutableList(bLength + 1) { it }
    var currentRow = MutableList(bLength + 1) { 0 }

// iterates through each char of string a
    for (i in 1..aLength) {
        currentRow[0] = i

// iterates through each char of string b
        for (ii in 1..bLength) {
            // points given for varying differences in the characters
            val deletion = previousRow[ii] + 1
            val insertion = currentRow[ii - 1] + 1
            val sub = previousRow[ii - 1] + if (a[i - 1] == b[ii - 1]) 0 else 1
            // calculates all three and takes the minimum value of the three
            currentRow[ii] = minOf(insertion, deletion, sub)
        }
        var temp = previousRow
        previousRow = currentRow
        currentRow = temp
    }
    // returns the distance
    return previousRow[bLength]
}

@EnabledTest
fun testLevenshteinDistance() {
    testSame(levenshteinDistance("kitten", "sitting"), 3, "levenshteinDistance , kitten sitting")
    testSame(levenshteinDistance("hello", "hello"), 0, "levenshteinDistance, hello hello")
    testSame(levenshteinDistance("sitting", "kitten"), 3, "levenshteinDistance, sitting kitten")
    testSame(levenshteinDistance("SUPERLONGWORD", "wrd"), 13, "levenshteinDistance, SUPERLONGWORD wrd")
}

// takes astring and returns a boolean 
typealias Classifier = (String) -> Boolean

// data class representing a label example that includes both a example of type E and a label of type L
data class LabeledExample<E, L>(
    val example: E,
    val label: L,
)
val dataset: List<LabeledExample<String, Boolean>> =
    listOf(
        // Some positive examples
        LabeledExample("yes", true),
        LabeledExample("y", true),
        LabeledExample("indeed", true),
        LabeledExample("aye", true),
        LabeledExample("oh yes", true),
        LabeledExample("affirmative", true),
        LabeledExample("roger", true),
        LabeledExample("uh huh", true),
        LabeledExample("true", true),
        // Some negative examples
        LabeledExample("no", false),
        LabeledExample("n", false),
        LabeledExample("nope", false),
        LabeledExample("negative", false),
        LabeledExample("nay", false),
        LabeledExample("negatory", false),
        LabeledExample("uh uh", false),
        LabeledExample("absolutely not", false),
        LabeledExample("false", false),
    )

/**
 * Heuristically determines if the supplied string
 * is positive based on the first letter being Y!
 *
 * This is our naive classifier.
 */
 // function that tests if a string starts with Y and returns a Boolean answer
fun naiveClassifier(s: String): Boolean = s.uppercase().startsWith("Y")
/**
 * Tests whether our classifier returns the expected result
 * for an element of our data set (at given index).
 */

// test function that tests a classifer on a example
fun testOne(
    index: Int,
    expected: Boolean,
    classifier: Classifier,
) {
    val sample = dataset[index]
    testSame(
        classifier(sample.example),
        when (expected) {
            true -> sample.label
            false -> !sample.label
        },
        when (expected) {
            true -> "${sample.example}"
            false -> "${sample.example} Error"
        },
    )
}

@EnabledTest
fun testNaiveClassifier() {
    testOne(0, true, ::naiveClassifier)
    testOne(1, true, ::naiveClassifier)

    testOne(2, false, ::naiveClassifier)
    testOne(3, false, ::naiveClassifier)
    testOne(4, false, ::naiveClassifier)
    testOne(5, false, ::naiveClassifier)
    testOne(6, false, ::naiveClassifier)
    testOne(7, false, ::naiveClassifier)
    testOne(8, false, ::naiveClassifier)

    testOne(9, true, ::naiveClassifier)
    testOne(10, true, ::naiveClassifier)
    testOne(11, true, ::naiveClassifier)
    testOne(12, true, ::naiveClassifier)
    testOne(13, true, ::naiveClassifier)
    testOne(14, true, ::naiveClassifier)
    testOne(15, true, ::naiveClassifier)
    testOne(16, true, ::naiveClassifier)
    testOne(17, true, ::naiveClassifier)
}

//a typealias for a evulation function that takes type t and turns it into a integer
typealias EvaluationFunction<T> = (T) -> Int

/**
 * A distance function producing a integer distance
 * between two elements of type T.
 */
typealias DistanceFunction<T> = (T, T) -> Int

// data class that represents the result with both a label of type l and the number of votes as a integer
data class ResultWithVotes<L>(
    val label: L,
    val votes: Int,
)

// gets the top k elements in a list of type t by the use of a metric function that turns t into a int
fun <T> topK(
    items: List<T>,
    metric: (T) -> Int,
    k: Int,
): MutableList<T> {

// list that stores elements of type t
    val newItems = mutableListOf<T>()

    for (i in items) {
        // gets the score for the current item
        val stat = metric(i)

        if (newItems.size < k) {
            newItems.add(i)
        } else {
            // gets the element with the lowest score in the result list
            var minI = 0
            var min = metric(newItems[0])
            for (ii in 1 until newItems.size) {
                val current = metric(newItems[ii])
                if (current < min) {
                    min = current
                    minI = ii
                }
            }
            if (stat > min) {
                newItems[minI] = i
            }
        }
    }
    return newItems
}

@EnabledTest
fun testTopK() {
    val items = listOf(5, 3, 8, 2, 7)
    val metric: (Int) -> Int = { it }
    testSame(topK(items, metric, 3), listOf(5, 7, 8), "topK")
}

// gets the label of the closest neighbor using the distance function
fun <E, L> getLabel(
    query: E,
    dataset: List<LabeledExample<E, L>>,
    distFn: DistanceFunction<E>,
    k: Int,
): ResultWithVotes<L> {
    /**
     * Your code.
     *
     * 1. Use top-K to find the k-closest dataset elements.
     *
     *    Note that finding the elements whose negated distance
     *    is the greatest is the same as finding those that are
     *    closest!
     *
     * 2. Use `map { it.label }` to keep only the newlist of
     *    the k-closest elements.
     *
     * 3. For each distinct label, count how many times it
     *    showed up in step #2.
     *
     * 4. Use topK to get the label with the greatest count.
     *
     * 5. Return both the label and the number of votes.
     */

// gets the k closest element from the distance function
    val closest = topK(dataset, { -distFn(query, it.example) }, k)

// gets the label of the k closest element
    val newList = closest.map { it.label }

// gets the count of occurrences of each label
    val distinctList = mutableListOf<L>()
    val count = mutableListOf<Int>()
    for (item in newList) {
        val index = distinctList.indexOf(item)
        if (index <= -1) {
            distinctList.add(item)
            count.add(1)
        } else {
            count[index] += 1
        }
    }

// gets the label with the highest count
    var max = 0
    var most = 0
    for (i in count.indices) {
        if (count[i] > max) {
            max = count[i]
            most = i
        }
    }

// returns the label with the most votes and the count 
    return ResultWithVotes(distinctList[most], max)
}

@EnabledTest
fun testGetLabel() {
    /**
     * Think of the data set below as points on a line.
     * '?' refers to the example below.
     *
     *       A   A       ?       B           B
     * |--- --- --- --- --- --- --- --- --- ---|
     *   1   2   3   4   5   6   7   8   9  10
     */
    val dataset =
        listOf(
            LabeledExample(2, "A"),
            LabeledExample(3, "A"),
            LabeledExample(7, "B"),
            LabeledExample(10, "B"),
        )

    /**
     * A simple distance function (absolute value).
     */
    fun distance(
        a: Int,
        b: Int,
    ): Int {
        val delta = a - b
        return when (delta >= 0) {
            true -> delta
            false -> -delta
        }
    }

    /**
     * Demonstrate that you understand how k-NN is
     * supposed to work by writting tests here for
     * a selection of cases that use the data set,
     * as well as the distance function above.
     *
     * To help you get started, consider a test for
     * a point 5, with k=3:
     *
     * (a) All the points with their distances are:
     *
     *     a = | 3 - 5| = 2
     *     a = | 2 - 5| = 3
     *     b = | 7 - 5| = 2
     *     b = |10 - 5| = 5
     *
     * (b) So, the labels of the three closest are:
     *
     *      i) 'a' with 2 votes;
     *     ii) 'b' with 1 vote.
     *
     * (c) kNN in this situation would predict the
     *     label for this point to be "a".
     *
     *     Its confidence is 2/3.
     *
     * We capture this test as follows:
     */
    testSame(
        getLabel(5, dataset, ::distance, k = 3),
        ResultWithVotes("A", 2),
        "5 -> A, 2/3",
    )

    /**
     * Now your task is to write tests for the following
     * additional cases:
     *
     *  1 (k=1)
     *  1 (k=2)
     * 10 (k=1)
     * 10 (k=2)
     */

    testSame(
        getLabel(1, dataset, ::distance, k = 1),
        ResultWithVotes("A", 1),
        "1 -> A, 1/1",
    )

    testSame(
        getLabel(1, dataset, ::distance, k = 2),
        ResultWithVotes("A", 2),
        "1 -> A, 2/2",
    )

    testSame(
        getLabel(10, dataset, ::distance, k = 1),
        ResultWithVotes("B", 1),
        "10 -> B, 1/1",
    )

    testSame(
        getLabel(10, dataset, ::distance, k = 2),
        ResultWithVotes("B", 2),
        "10 -> B, 2/2",
    )
}

// classifier function to classify a string
fun classifier(s: String): ResultWithVotes<Boolean> {
    /**
     * 1. Convert the input to lowercase (since the data
     *    set is all lowercase!)
     *
     * 2. Check to see if the lowercased input exists in
     the data set (you can assume no duplicates).

     * 3. If the input was found, simply return its label
     *    with 100% confidence (3/3).
     *
     *    Otherwise, return the result of a 3-NN classifi-
     *    cation using the Levenshtein distance metric and
     *    the data set.
     */

// converts the parameter to lowercase
    val str = s.lowercase()

// checks if the parameter is in the dataset
    if (dataset.filter { it.example == str }.count() >= 1) {
        return ResultWithVotes(dataset.filter { it.example == str }[0].label, 3)
    }

// uses levenshtein distance for the classification
    return getLabel(str, dataset, ::levenshteinDistance, 3)
}

@EnabledTest
fun testClassifier() {
    testSame(
        classifier("YES"),
        ResultWithVotes(true, 3),
        "YES: 3/3",
    )

    testSame(
        classifier("no"),
        ResultWithVotes(false, 3),
        "no: 3/3",
    )

    // Good ML!
    testSame(
        classifier("nadda"),
        ResultWithVotes(false, 2),
        "nadda: 2/3",
    )

    // Good ML!
    testSame(
        classifier("yerp"),
        ResultWithVotes(true, 3),
        "yerp: 3/3",
    )

    // Very confident in the wrong answer.
    testSame(
        classifier("ouch"),
        ResultWithVotes(true, 3),
        "ouch: 3/3",
    )

    // Very confident, but does the input make sense?!
    testSame(
        classifier("now"),
        ResultWithVotes(false, 3),
        "now 3/3",
    )
}

/*** Step 6: Putting all together ***/

/**
 * Represents the result of a study session:
 *
 * (i)  Number of questions in the question bank; and
 * (ii) Number of attempts to get them all correct.
 */
 // data class that is the result of going through a deck of questions
data class StudyDeckResult(
    val numQuestions: Int,
    val numAttempts: Int,
)

// data class that is the progess of a bank of questions and the study deck result
data class QuestionBankDeckResult(
    val qBanker: IQuestionBank,
    val deckResult: StudyDeckResult,
)

// function to study a bank of questions
fun studyQuestionBank(qBanks: IQuestionBank) {
    reactConsole(
        initialState = QuestionBankDeckResult(qBanks, StudyDeckResult(qBanks.getSize(), 0)),
        stateToText = ::text,
        nextState = ::nextState,
        isTerminalState = ::terminate,
        terminalStateToText = ::text,
    )
}

// displays text depending on the state
fun text(progress: QuestionBankDeckResult): String {
    var response = progress.qBanker.getText()
    if (response == null) {
        return ("Bye.Questions:${progress.deckResult.numQuestions}, Attempts:${progress.deckResult.numAttempts}")
    }
    return response.toString()
}

// transitions the state depending on the input
fun nextState(
    progress: QuestionBankDeckResult,
    input: String,
): QuestionBankDeckResult {
    if (progress.qBanker.getState() == QuestionBankState.QUESTIONING) {
        return QuestionBankDeckResult(
            progress.qBanker.show(),
            StudyDeckResult(
                progress.deckResult.numQuestions,
                progress.deckResult.numAttempts + 1,
            ),
        )
    }
    if (naiveClassifier(input.lowercase())) {
        return QuestionBankDeckResult(progress.qBanker.next(true), progress.deckResult)
    }
    return QuestionBankDeckResult(progress.qBanker.next(classifier(input).label), progress.deckResult)
}

// checks to see if the program should terminate
fun terminate(progress: QuestionBankDeckResult): Boolean = (progress.qBanker.getState() == QuestionBankState.COMPLETED)

@EnabledTest
fun testStudyQuestionBank() {
    testSame(
        text(QuestionBankDeckResult(sportsListBased, StudyDeckResult(3, 0))),
        "Who is the NBA's all time leading scorer?",
        "studyQuestionBank, text Question",
    )
    testSame(
        captureResults(
            { text(QuestionBankDeckResult(ListBasedQuestionBank(sports, QuestionBankState.ANSWERING), StudyDeckResult(3, 0))) },
        ),
        CapturedResult(
            "Were you correct?",
            "The correct Answer was Lebron James",
        ),
        "studyQuestionBank, text Answer",
    )
    testSame(
        captureResults(
            { text(QuestionBankDeckResult(ListBasedQuestionBank(sports, QuestionBankState.COMPLETED), StudyDeckResult(3, 0))) },
        ),
        CapturedResult(
            "Bye.Questions:3, Attempts:0",
        ),
        "studyQuestionBank, text Completed",
    )

    testSame(
        nextState(
            QuestionBankDeckResult(sportsListBased, StudyDeckResult(3, 0)),
            "Lebron James",
        ),
        QuestionBankDeckResult(ListBasedQuestionBank(sports, QuestionBankState.ANSWERING), StudyDeckResult(3, 1)),
        "studyQuestionBank, nextState Questioning",
    )
    testSame(
        nextState(
            QuestionBankDeckResult(
                ListBasedQuestionBank(sports, QuestionBankState.ANSWERING),
                StudyDeckResult(3, 1),
            ),
            "yes",
        ),
        QuestionBankDeckResult(
            ListBasedQuestionBank(TaggedQuestionBank("Sports", listOf(sportsq2, sportsq3)), QuestionBankState.QUESTIONING),
            StudyDeckResult(3, 1),
        ),
        "studyQuestionBank, nextState Answering yes",
    )
    testSame(
        nextState(
            QuestionBankDeckResult(
                ListBasedQuestionBank(sports, QuestionBankState.ANSWERING),
                StudyDeckResult(3, 1),
            ),
            "no",
        ),
        QuestionBankDeckResult(
            ListBasedQuestionBank(TaggedQuestionBank("Sports", listOf(sportsq2, sportsq3, sportsq1)), QuestionBankState.QUESTIONING),
            StudyDeckResult(3, 1),
        ),
        "studyQuestionBank, nextState Answering no",
    )
    testSame(
        terminate(
            QuestionBankDeckResult(
                ListBasedQuestionBank(sports, QuestionBankState.COMPLETED),
                StudyDeckResult(3, 1),
            ),
        ),
        true,
        "terminate true",
    )
    testSame(
        terminate(
            QuestionBankDeckResult(
                ListBasedQuestionBank(sports, QuestionBankState.QUESTIONING),
                StudyDeckResult(3, 1),
            ),
        ),
        false,
        "terminate false",
    )
}

// tagged questions
val sportsq1 = TaggedQuestion(Question("Who is the NBA's all time leading scorer?", "Lebron James"), listOf("Easy", "Sports", "Basketball"))
val sportsq2 = TaggedQuestion(Question("How many teams are in the NFL", "32"), listOf("Medium", "Sports", "Football"))
val sportsq3 = TaggedQuestion(Question("Who has one the most PGA Masters", "Jack Nicklaus"), listOf("Hard", "Sports", "Golf"))

// tagged question bank for sports
val sports = TaggedQuestionBank("Sports", listOf(sportsq1, sportsq2, sportsq3))
// list based question bank of sports with questioning state
val sportsListBased = ListBasedQuestionBank(sports, QuestionBankState.QUESTIONING)
// named menu option of sports question bank
val sportsNamedOption =
    NamedMenuOption(
        sportsListBased,
        "Sports",
    )

//tagged question bank for music from txt
val music = readTaggedQuestionBank("music.txt")
// list based question bank of music with questioning state
val musicListBased =
    ListBasedQuestionBank(
        music,
        QuestionBankState.QUESTIONING,
    )

// named menu option of music question bank
val musicNamedMenu =
    NamedMenuOption(
        musicListBased,
        "Music",
    )

// auto generates autogeneratedquestionbank for cubes questions
val cubes =
    AutoGeneratedQuestionBank(
        generatorQuestion = { it -> "What is the cube of $it?" },
        generatorAnswer = { it -> "${it * it * it}, Were you correct?" },
        sequence = listOf(1, 2, 3),
        QuestionBankState.QUESTIONING,
    )

// named menu option of cubes question bank
val cubesNamedMenu =
    NamedMenuOption(
        cubes,
        "Cubes",
    )

// function to study the question banks
fun study(): String {
    val options = mutableListOf(sportsNamedOption, musicNamedMenu, cubesNamedMenu)
    do {
        // prompts user to choose a bank from the menu options
        var questionBank = chooseMenu(options.toList())
        if (questionBank == null) {
            break
        }
        // goes through the questions and removes the bank once completed
        println(studyQuestionBank(questionBank!!.option))
        options.remove(questionBank)
    }
    while (options.size > 0)

    return ("Quitting")
}

@EnabledTest
fun testStudy() {
    testSame(
        captureResults(
            { study() },
            "0",
        ),
        CapturedResult(
            "Quitting",
            "Enter 1, 2, ..., or 0 to quit:",
            "1. Sports",
            "2. Music",
            "3. Cubes",
            "Return your choice: ",
            "Bye.",
        ),
        "study quit",
    )

    testSame(
        captureResults(
            { study() },
            "1",
            "-",
            "Of course",
            "-",
            "yes",
            "-",
            "no",
            "-",
            "easily",
            "0",
        ),
        CapturedResult(
            "Quitting",
            "Enter 1, 2, ..., or 0 to quit:",
            "1. Sports",
            "2. Music",
            "3. Cubes",
            "Return your choice: ",
            "You chose to study Sports",
            "Who is the NBA's all time leading scorer?",
            "The correct Answer was Lebron James",
            "Were you correct?",
            "How many teams are in the NFL",
            "The correct Answer was 32",
            "Were you correct?",
            "Who has one the most PGA Masters",
            "The correct Answer was Jack Nicklaus",
            "Were you correct?",
            "Who has one the most PGA Masters",
            "The correct Answer was Jack Nicklaus",
            "Were you correct?",
            "Bye.Questions:3, Attempts:4",
            "kotlin.Unit",
            "Enter 1, 2, ..., or 0 to quit:",
            "1. Music",
            "2. Cubes",
            "Return your choice: ",
            "Bye.",
        ),
        "study",
    )
}

// function that calls the study function to run
fun main() {
    println(study())
}

runEnabledTests(this)
main()

import khoury.testSame
import khoury.EnabledTest
import khoury.runEnabledTests

//A Book is a data class that contains a title and an author. Design a function with
//lambda(s) that takes a list of books and returns a list of unique authors

data class Book(val title:String, val author:String)

fun unique(lob:List<Book>):List<Book.author>{
    return lob.map({n:Book->n.author.distinct()})
}
@EnabledTest
fun testUnique(){
    testSame(unique(listOf(Book("Yo","me"),Book("He","ne"),Book("Ge","Je"))),listOf("Yo","He","Ge"))
}
runEnabledTests(this)
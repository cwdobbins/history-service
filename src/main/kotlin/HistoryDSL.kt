import java.util.Date
import java.time.Instant
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat

data class Search(
    val id: String,
    val name: String,
    val submitted: Date = Date.from(Instant.now()),
    val keywordSearch: KeywordSearch? = null
)

data class KeywordSearch(
    val language: String,
    val keyword: String
)

object Searches : Table() {
    val id = text("id").primaryKey()
    val userName = text("user_name").primaryKey()
    val name = text("name")
    val submitted = datetime("submitted")
    val keywordLanguage = text("keyword_language").nullable()
    val keywordKeyword = text("keyword_keyword").nullable()
}

class HistoryDSL(jdbcUrl: String, jdbcDriver: String) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    init {
        Database.connect(jdbcUrl, jdbcDriver)
    }

    fun get(user: String, id: String): Search {
        return Searches.select {
            (Searches.id.eq(id) and Searches.userName.eq(user))
        }.first().let {
            var keyword: KeywordSearch? = null
            it[Searches.keywordLanguage]?.let { a ->
              it[Searches.keywordKeyword]?.let { b ->
                keyword = KeywordSearch(a, b)
              }
            }

            Search(
                it[Searches.id],
                it[Searches.name],
                it[Searches.submitted].toDate(),
                keyword
            )
        }
    }

    fun put(user: String, search: Search): Unit {
        Searches.insert {
            it[id] = search.id
            it[userName] = user
            it[name] = search.name
            it[submitted] = DateTime.parse(dateFormat.format(search.submitted))
            search.keywordSearch?.let { kw ->
                it[keywordLanguage] = kw.language
                it[keywordKeyword] = kw.keyword
            }
        }
    }
}
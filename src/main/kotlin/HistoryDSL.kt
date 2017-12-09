import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

data class Search(
    val id: String,
    val name: String,
    val language: String = "",
    val submitted: String = ""
)

object Searches : Table() {
    val id = text("id").primaryKey()
    val userName = text("user_name").primaryKey()
    val name = text("name").nullable()
    val language = text("language").nullable()
    val submitted = datetime("submitted").nullable()
}

class HistoryDSL(jdbcUrl: String, jdbcDriver: String) {
    init {
        Database.connect(jdbcUrl, jdbcDriver)
    }

    fun get(user: String, id: String): Search {
        Searches.select {
            (Searches.id.eq(id) and Searches.userName.eq(user))
        }
        return Search("1", "test")
    }

    fun put(user: String, search: Search): Unit {
        Searches.insert {
            it[id] = search.id
            it[userName] = user
            it[name] = search.name
            it[language] = search.language
            //it[submitted] = DateTime.parse(search.submitted)
        }
    }
}
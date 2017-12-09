import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.junit.After
import org.junit.Before

import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class ITestHistoryDSL {
    val testUser = "test_user"
    val testSearch = Search("123", "Test Search", Date.from(Instant.now()))
    val username = "postgres"
    val password = "testy"
    val jdbcUrl = "jdbc:postgresql://localhost:5432/?user=$username&password=$password"
    val conn = DriverManager.getConnection(jdbcUrl, username, password)

    val dsl = HistoryDSL(
        jdbcUrl,
        "org.postgresql.Driver"
    )

    @Before
    fun setup() {
        transaction {
            create(Searches)
        }
    }

    @After
    fun teardown() {
        transaction {
            drop(Searches)
        }
    }

    @Test
    fun testInsert() {
        transaction {
            dsl.put(testUser, testSearch)
            dsl.put(testUser, testSearch.copy(id = "234"))
            dsl.put(testUser, testSearch.copy(id = "345"))
        }
        checkResult(
            "SELECT COUNT(*) FROM searches WHERE id = '${testSearch.id}'",
            { assert(it.getInt(1) == 1) {"Result set size should be 1"} }
        )
        checkResult(
            "SELECT COUNT(*) FROM searches",
            { assert(it.getInt("count") == 3) }
        )
    }

    @Test
    fun testGet() {
        transaction {
            dsl.put(testUser, testSearch)
            val search = dsl.get(testUser, testSearch.id)
            println(search)
            println(testSearch)
            println(search.equals(testSearch))
            assert(search.equals(testSearch))
        }
    }

    private fun checkResult(sql: String, assertion: (ResultSet) -> Unit) {
        val rs = conn
            .createStatement()
            .executeQuery(sql)
        rs.next()
        assertion(rs)
        rs.close()
    }
}

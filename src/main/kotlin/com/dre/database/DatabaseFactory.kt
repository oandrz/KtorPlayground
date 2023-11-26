package com.dre.database

import com.dre.model.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val driverClassName = config.property("storage.class_name").getString()
        val jdbcURL = config.property("storage.jbdc_url").getString() +
                (config.propertyOrNull("storage.dbFilePath")?.getString()?.let {
                    File(it).canonicalFile.absolutePath
                } ?: "")
        // Create database without connection pooling
//        val database = Database.connect(jdbcURL, driverClassName)
        // Create database with connection pooling
        val database = Database.connect(
            createHikariDataSource(
                url = jdbcURL,
                driver = driverClassName
            )
        )
        /*
        * If you have only one database, you can omit it. In this case, Exposed automatically uses the last connected database for transactions.
        * */
        transaction(database) {
            // transaction call at the bottom of the init function to instruct the database to create this table if it doesn't yet exist:
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun createHikariDataSource(
        url: String,
        driver: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3 // size of the connection pool can reach
        /*
        * When isAutoCommit is set to true, each statement is committed immediately after execution.
        * When set to false, the developer needs to explicitly commit the transaction using the commit() method after
        * executing a series of statements. This setting can affect the atomicity of transactions, where a series of
        * statements can be treated as a single unit
        * */
        isAutoCommit = false
        // transactionIsolation behavior of transactions concerning concurrent access and changes made by other transactions
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}
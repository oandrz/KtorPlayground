package com.dre.database

import com.dre.database.DatabaseFactory.dbQuery
import com.dre.model.User
import com.dre.model.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DaoFacadeImpl : DaoFacade {
    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map(::toUser)
    }

    private fun toUser(it: ResultRow): User = User(
        email = it[Users.email],
        pass = it[Users.pass]
    )

    override suspend fun getUser(id: String): User? = dbQuery {
        Users.select { Users.id eq id.toInt() }.map(::toUser).singleOrNull()
    }

    override suspend fun getUserFromEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email }.map(::toUser).singleOrNull()
    }

    override suspend fun checkUserExist(email: String, password: String): User? = dbQuery {
        Users.select { Users.email eq email and (Users.pass eq password) }.map(::toUser).singleOrNull()
    }

    override suspend fun deleteUser(email: String, password: String): Boolean = dbQuery {
        Users.deleteWhere { Users.email eq email and (Users.pass eq password) } > 0
    }

    override suspend fun addUser(email: String, password: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.email] = email
            it[Users.pass] = password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::toUser)
    }
}

val dao: DaoFacade = DaoFacadeImpl().apply {
    runBlocking {
        if(getUsers().isEmpty()) {
            addUser(email = "admin", password = "admin")
        }
    }
}
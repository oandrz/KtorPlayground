package com.dre.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
    val email: String,
    val pass: String
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 1024)
    val pass = varchar("pass", 1024)

    override val primaryKey = PrimaryKey(id)
}

val userStorage = mutableListOf<User>()
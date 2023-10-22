package com.dre.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String,
    val pass: String
)

val userStorage = mutableListOf<User>()
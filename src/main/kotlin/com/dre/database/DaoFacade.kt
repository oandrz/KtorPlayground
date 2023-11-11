package com.dre.database

import com.dre.model.User

interface DaoFacade {
    suspend fun getUsers(): List<User>
    suspend fun getUser(id: String): User?
    suspend fun getUserFromEmail(email: String): User?
    suspend fun checkUserExist(email: String, password: String): User?
    suspend fun deleteUser(email: String, password: String): Boolean
    suspend fun addUser(email: String, password: String): User?
}
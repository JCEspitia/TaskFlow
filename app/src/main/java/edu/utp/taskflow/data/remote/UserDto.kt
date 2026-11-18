package edu.utp.taskflow.data.remote

import edu.utp.taskflow.model.RemoteUser

data class UserDto(
    val id: String,
    val name: String,
    val email: String
) {
    fun toUser(): RemoteUser {
        return RemoteUser(
            id = id,
            name = name,
            email = email
        )
    }
}
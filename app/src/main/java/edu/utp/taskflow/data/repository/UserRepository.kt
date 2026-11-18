package edu.utp.taskflow.data.repository

import edu.utp.taskflow.data.remote.TaskApiService
import edu.utp.taskflow.model.RemoteUser

class UserRepository (
    private val api: TaskApiService
) {
    suspend fun getUsers(): List<RemoteUser> {
        return api.getUsers().map { dto ->
            dto.toUser()
        }
    }
}
package edu.utp.taskflow.data.remote

data class RemoteTaskDto(
    val id: Int,
    val todo: String,
    val completed: Boolean,
    val userId: Int
)

data class RemoteTasksResponse(
    val todos: List<RemoteTaskDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
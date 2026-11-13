package edu.utp.taskflow.data.repository

import edu.utp.taskflow.data.remote.TaskApiService
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskPriority

class RemoteTaskRepository(
    private val api: TaskApiService
) {
    suspend fun getTasks(): List<Task> {
        val response = api.getTasks()

        return response.todos.map { dto ->
            Task(
                id = dto.id,
                title = dto.todo,
                category = "En línea",
                priority = TaskPriority.MEDIUM,
                isCompleted = dto.completed
            )
        }
    }
}
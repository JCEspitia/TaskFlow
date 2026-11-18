package edu.utp.taskflow.data.repository

import edu.utp.taskflow.data.remote.RemoteTaskDto
import edu.utp.taskflow.data.remote.TaskApiService
import edu.utp.taskflow.data.remote.TaskRequestDto
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskPriority

class RemoteTaskRepository(
    private val api: TaskApiService
) {
    suspend fun getTasks(userId: String): List<Task> {
        return api.getTasks(userId)
            .filter { it.userId == userId }
            .map { dto ->
                dto.toTask()
            }
    }

    suspend fun createTask(
        title: String,
        priority: TaskPriority,
        userId: String
    ) {
        val request = TaskRequestDto(
            title = title,
            category = "MockAPI",
            priority = priority.name,
            isCompleted = false,
            userId = userId
        )

        api.createTask(request)
    }

    suspend fun toggleTask(task: Task, userId: String) {
        val request = TaskRequestDto(
            title = task.title,
            category = task.category,
            priority = task.priority.name,
            isCompleted = !task.isCompleted,
            userId = userId
        )

        api.updateTask(
            id = task.id.toString(),
            task = request
        )
    }

    suspend fun updateTask(
        task: Task,
        newTitle: String,
        newPriority: TaskPriority
    ) {
        // TODO: por impplementar
    }

    suspend fun deleteTask(taskId: Int) {
        api.deleteTask(taskId.toString())
    }

    private fun RemoteTaskDto.toTask(): Task {
        val parsedId = id.toIntOrNull()
            ?: error("Identicador remoto no númerico: $id")

        val parsedPriority = TaskPriority.entries
            .firstOrNull { it.name == priority }
            ?: TaskPriority.MEDIUM

        return Task(
            id = parsedId,
            title = title,
            category = category,
            priority = parsedPriority,
            isCompleted = isCompleted
        )
    }
}
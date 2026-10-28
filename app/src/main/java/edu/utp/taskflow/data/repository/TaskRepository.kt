package edu.utp.taskflow.data.repository

import edu.utp.taskflow.data.local.TaskDao
import edu.utp.taskflow.data.local.TaskEntity
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.indexOfFirst

class TaskRepository(
    private val dao: TaskDao
) {

    val tasks: Flow<List<Task>> =
        dao.observeAll().map { entities ->
            entities.map { entity ->
                entity.toModel()
            }
        }

    suspend fun addTask(
        title: String,
        priority: TaskPriority
    ) {
        val cleanTitle = title.trim()

        if (cleanTitle.isBlank()) return

        val entity = TaskEntity(
            title = cleanTitle,
            category = "Personal",
            priority = priority.name
        )

        dao.insert(entity)
    }

    suspend fun toggleTask(taskId: Int) {
        val entity = dao.findById(taskId) ?: return

        dao.update(
            entity.copy(
                isCompleted = !entity.isCompleted
            )
        )
    }

    suspend fun deleteTask(taskId: Int) {
        val entity = dao.findById(taskId) ?: return

        dao.delete(entity)
    }
}


private fun TaskEntity.toModel(): Task {
    return Task(
        id = id,
        title = title,
        category = category,
        priority =
            TaskPriority.entries.firstOrNull {
                it.name == priority
            } ?: TaskPriority.MEDIUM,
        isCompleted = isCompleted
    )
}
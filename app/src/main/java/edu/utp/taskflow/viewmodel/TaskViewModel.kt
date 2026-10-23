package edu.utp.taskflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel

import edu.utp.taskflow.data.sampleTasks
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskFilter
import edu.utp.taskflow.model.TaskPriority

class TaskViewModel: ViewModel() {

    // LISTA DE TAREAS
    val tasks = mutableStateListOf<Task>().apply() {
        addAll(sampleTasks)
    }

    // FILTRO SELECCIONADO
    var selectedFilter by mutableStateOf(TaskFilter.ALL)
        private set

    var nextId = (tasks.maxOfOrNull { it.id } ?: 0) + 1


    // CAMBIO DE FILTRO
    fun changeFilter(filter: TaskFilter) {
        selectedFilter = filter
    }

    // COMPLETAR O REABRIR TAREA
    fun toggleTask(taskId: Int) {
        val index = tasks.indexOfFirst { task ->
            task.id == taskId
        }

        if (index != -1) {
            val currentTask = tasks[index]

            tasks[index] = currentTask.copy(
                isCompleted = !currentTask.isCompleted
            )
        }
    }

    // ELIMINAR TAREA
    fun deleteTask(taskId: Int) {
        tasks.removeAll { task ->
            task.id == taskId
        }
    }

    // AGREGAR TAREA
    fun addTask(
        title: String,
        priority: TaskPriority
    ){
        val cleanTitle = title.trim()

        if (cleanTitle.isBlank()){
            return
        }

        val newTask = Task(
            id = nextId++,
            title = title.trim(),
            category = "Personal",
            priority = priority
        )

        tasks.add(newTask)
    }
}
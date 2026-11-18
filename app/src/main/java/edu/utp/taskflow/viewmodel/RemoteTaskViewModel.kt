package edu.utp.taskflow.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.utp.taskflow.data.repository.RemoteTaskRepository
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskPriority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemoteTaskViewModel(
    private val repository: RemoteTaskRepository
) : ViewModel() {

    private var currentUserId: String? = null

    private var loadJob: Job? = null
    private var operationJob: Job? = null

    private val _tasks =
        MutableStateFlow<List<Task>>(emptyList())

    val tasks: StateFlow<List<Task>> =
        _tasks.asStateFlow()

    private val _isLoading =
        MutableStateFlow(true)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _isSaving =
        MutableStateFlow(false)

    val isSaving: StateFlow<Boolean> =
        _isSaving.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    init {
        loadTasks()
    }

    fun setUser(userId: String?) {
        if (currentUserId == userId) return

        loadJob?.cancel()
        operationJob?.cancel()

        currentUserId = userId

        _tasks.value = emptyList()
        _errorMessage.value = null
        _isLoading.value = false
        _isSaving.value = false

        if (userId != null) {
            loadTasks()
        }
    }

    fun loadTasks() {
        val userId = currentUserId ?: return
        if (_isSaving.value) return

        loadJob?.cancel()

        _isLoading.value = true
        _errorMessage.value = null

        loadJob = viewModelScope.launch {


            try {
                _tasks.value = repository.getTasks(userId)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _errorMessage.value =
                    "Error: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTask(
        title: String,
        priority: TaskPriority
    ) {
        if (title.isBlank()) return

        executeOperation { userId ->
            repository.createTask(
                title = title.trim(),
                priority = priority,
                userId = userId
            )
        }
    }

    fun toggleTask(task: Task) {
        if (_tasks.value.none {it.id == task.id}) return

        executeOperation { userId ->
            repository.toggleTask(task = task, userId = userId)
        }
    }

    fun updateTask(
        task: Task,
        title: String,
        priority: TaskPriority
    ) {
        // TODO: Por implementar
    }

    fun deleteTask(taskId: Int) {
        executeOperation {
            repository.deleteTask(taskId)
        }
    }

    private fun executeOperation(
        operation: suspend (String) -> Unit
    ) {

        val userId = currentUserId ?: return
        if (_isSaving.value || _isLoading.value) return

        _isSaving.value = true
        _errorMessage.value = null

        operationJob = viewModelScope.launch {
            try {
                operation(userId)

                val updatedTasks = repository.getTasks(userId)

                if (currentUserId == userId){
                    _tasks.value = updatedTasks
                }

                _tasks.value = repository.getTasks(userId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if(currentUserId == userId) {
                    _errorMessage.value = "No fue posible completar la operación"
                }
            } finally {
                if(currentUserId == userId) {
                    _isSaving.value = false
                }
            }
        }

    }
}

class RemoteTaskViewModelFactory(
    private val repository: RemoteTaskRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        require(
            modelClass.isAssignableFrom(
                RemoteTaskViewModel::class.java
            )
        )

        return RemoteTaskViewModel(repository) as T
    }
}
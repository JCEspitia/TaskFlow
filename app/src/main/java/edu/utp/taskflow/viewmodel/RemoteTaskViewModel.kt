package edu.utp.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.utp.taskflow.data.repository.RemoteTaskRepository
import edu.utp.taskflow.model.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemoteTaskViewModel(
    private val repository: RemoteTaskRepository
) : ViewModel() {

    private val _tasks =
        MutableStateFlow<List<Task>>(emptyList())

    val tasks: StateFlow<List<Task>> =
        _tasks.asStateFlow()

    private val _isLoading =
        MutableStateFlow(true)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {
                _tasks.value = repository.getTasks()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _errorMessage.value =
                    "No fue posible consultar las tareas. ${e.message}"

            } finally {
                _isLoading.value = false
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
package edu.utp.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.utp.taskflow.data.repository.UserRepository
import edu.utp.taskflow.model.RemoteUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(
    private val repository: UserRepository
): ViewModel() {

    private val _users = MutableStateFlow<List<RemoteUser>>(emptyList())
    val users: StateFlow<List<RemoteUser>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<RemoteUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _users.value = repository.getUsers()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "No fue posible consultar los usuarios."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Inicio de sesión (simulado)
    fun login(user: RemoteUser) {
        val selected = _users.value.firstOrNull {
            it.id == user.id
        } ?: return

        _currentUser.value = selected
    }

    // Cerrar sesión (simulado)
    fun logout() {
        _currentUser.value = null
    }

}

class SessionViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        require(
            modelClass.isAssignableFrom(
                SessionViewModel::class.java
            )
        )

        return SessionViewModel(repository) as T
    }
}
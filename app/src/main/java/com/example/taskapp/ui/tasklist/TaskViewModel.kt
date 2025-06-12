package com.example.taskapp.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.model.Priority
import com.example.taskapp.model.Task
import com.example.taskapp.data.model.Status
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Classe que representa todo o estado da tela
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val activePriorityFilter: Priority? = null,
    val activeStatusFilter: Status? = null,
    val ordenarPrazoMaisProximo: Boolean? = null
)

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository() // Instancia o repositório

    // Estados internos para os filtros e busca
    private val _searchQuery = MutableStateFlow("")
    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    private val _statusFilter = MutableStateFlow<Status?>(null)
    private val _prazoOrder = MutableStateFlow<Boolean?>(null) // ← Novo: ordem por prazo

    // Estado da UI exposto para a tela (imutável)
    private val _uiState = MutableStateFlow(TaskListUiState(isLoading = true))
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        // Combina o Flow de tarefas do repositório com os Flows de filtro e busca
        viewModelScope.launch {
            combine(
                repository.getTasks(),
                _searchQuery,
                _priorityFilter,
                _statusFilter,
                _prazoOrder
            ) { tasks, query, priority, status, ordenarPrazoMaisProximo ->

                val filteredTasks = tasks
                    .filter { task -> // Filtro de busca no título
                        task.titulo.contains(query, ignoreCase = true)
                    }
                    .filter { task -> // Filtro de prioridade
                        priority == null || task.prioridade == priority
                    }
                    .filter { task -> // Filtro de status
                        status == null || task.status == status
                    }
                    .let { list ->
                        when (ordenarPrazoMaisProximo) {
                            true -> list.sortedBy { it.prazoCalculado }
                            false -> list.sortedByDescending { it.prazoCalculado }
                            else -> list
                        }
                    }

                // Atualiza o estado da UI com os dados filtrados
                TaskListUiState(
                    tasks = filteredTasks,
                    isLoading = false,
                    searchQuery = query,
                    activePriorityFilter = priority,
                    activeStatusFilter = status,
                    ordenarPrazoMaisProximo = ordenarPrazoMaisProximo
                )
            }.catch {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.collect { state ->
                // Emite o novo estado completo para a UI
                _uiState.value = state
            }
        }
    }

    // Funções chamadas pela UI para alterar o estado
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onPriorityFilterChanged(priority: Priority?) {
        _priorityFilter.value = priority
    }

    fun onStatusFilterChanged(status: Status?) {
        _statusFilter.value = status
    }

    fun onPrazoOrderChanged(maisProximo: Boolean?) {
        _prazoOrder.value = maisProximo
    }

    fun onSubtaskStatusChanged(taskId: String, subtaskId: String, newStatus: Status) {
        viewModelScope.launch {
            try {
                repository.updateSubtaskStatus(taskId, subtaskId, newStatus)
            } catch (e: Exception) {
            }
        }
    }
}
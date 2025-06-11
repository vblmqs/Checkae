package com.example.taskapp.ui.taskform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FormResult {
    object Idle : FormResult()
    object Success : FormResult()
    data class Error(val message: String) : FormResult()
}

class TaskFormViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _taskState = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _taskState.asStateFlow()

    private val _formResult = MutableStateFlow<FormResult>(FormResult.Idle)
    val formResult: StateFlow<FormResult> = _formResult.asStateFlow()

    fun carregarTarefa(taskId: String) {
        viewModelScope.launch {
            _taskState.value = repository.getTaskById(taskId)
        }
    }

    fun salvarOuAtualizarTarefa(task: Task) {
        viewModelScope.launch {
            try {
                if (task.id.isNotBlank()) {
                    repository.updateTask(task)
                } else {
                    repository.addTask(task)
                }
                _formResult.value = FormResult.Success
            } catch (e: Exception) {
                _formResult.value = FormResult.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun excluirTarefa(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
                _formResult.value = FormResult.Success
            } catch (e: Exception) {
                _formResult.value = FormResult.Error(e.message ?: "Erro ao excluir")
            }
        }
    }

    fun resetFormResult() {
        _formResult.value = FormResult.Idle
    }
}
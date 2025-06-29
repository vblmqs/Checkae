package com.example.taskapp.ui.taskform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.taskapp.data.model.Status
import java.util.UUID

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
                // Obtenha a tarefa atual (do Firestore) antes de qualquer modificação, se for uma atualização
                val existingTask = if (task.id.isNotBlank()) {
                    repository.getTaskById(task.id)
                } else {
                    null // Se o ID estiver em branco, é uma nova tarefa
                }

                var taskToSave = task

                // Lógica para definir dataFim baseada na mudança de status
                if (taskToSave.status == Status.CONCLUIDA && existingTask?.status != Status.CONCLUIDA) {
                    taskToSave = taskToSave.copy(dataFim = System.currentTimeMillis())
                } else if (taskToSave.status != Status.CONCLUIDA && existingTask?.status == Status.CONCLUIDA) {
                    // Se o status mudou de CONCLUIDA para outro (tarefa reaberta)
                    taskToSave = taskToSave.copy(dataFim = null)
                }

                // Salvar ou atualizar a tarefa no repositório
                if (taskToSave.id.isNotBlank()) {
                    repository.updateTask(taskToSave)
                } else {
                    // Se for uma nova tarefa, gera um ID único antes de adicionar
                    val newTaskWithId = taskToSave.copy(id = UUID.randomUUID().toString())
                    repository.addTask(newTaskWithId)
                }

                // Atualizar o resultado do formulário
                _formResult.value = FormResult.Success
            } catch (e: Exception) {
                _formResult.value = FormResult.Error(e.message ?: "Erro desconhecido ao salvar/atualizar tarefa.")
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
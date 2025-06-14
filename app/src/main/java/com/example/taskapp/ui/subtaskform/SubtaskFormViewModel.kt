package com.example.taskapp.ui.subtaskform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Subtask
import com.example.taskapp.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class SubtaskFormViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado")

    private val _subtaskState = MutableStateFlow<Subtask?>(null)
    val subtask: StateFlow<Subtask?> = _subtaskState

    fun carregarSubtarefa(tarefaId: String, subtarefaId: String) {
        viewModelScope.launch {
            try {
                // Para carregar uma subtarefa, primeiro carregamos a tarefa pai
                val taskDoc = db.collection("users").document(userId)
                    .collection("tasks").document(tarefaId).get().await()

                val task = taskDoc.toObject(Task::class.java)
                _subtaskState.value = task?.subtarefas?.find { it.id == subtarefaId }
            } catch (e: Exception) {
                e.printStackTrace()
                _subtaskState.value = null
            }
        }
    }

    private fun getTaskRef(tarefaId: String) =
        db.collection("users").document(userId).collection("tasks").document(tarefaId)

    fun cadastrarSubtarefa(tarefaId: String, subtarefa: Subtask) {
        viewModelScope.launch {
            val taskRef = getTaskRef(tarefaId)
            // Gera um ID único para a nova subtarefa
            val novaSubtarefa = subtarefa.copy(id = UUID.randomUUID().toString())
            // Adiciona a nova subtarefa ao array 'subtarefas' no Firestore
            taskRef.update("subtarefas", FieldValue.arrayUnion(novaSubtarefa)).await()
        }
    }

    fun atualizarSubtarefa(tarefaId: String, subtarefaAtualizada: Subtask) {
        viewModelScope.launch {
            val taskRef = getTaskRef(tarefaId)
            db.runTransaction { transaction ->
                val taskSnapshot = transaction.get(taskRef)
                val task = taskSnapshot.toObject(Task::class.java)
                if (task != null) {
                    val subtarefasAntigas = task.subtarefas

                    val subtarefaAntiga = subtarefasAntigas.find { it.id == subtarefaAtualizada.id }

                    var subtarefaParaSalvar = subtarefaAtualizada

                    // Lógica para definir dataFim se a subtarefa for concluída
                    if (subtarefaAtualizada.status == Status.CONCLUIDA && subtarefaParaSalvar.dataFim == null) {
                        subtarefaParaSalvar = subtarefaParaSalvar.copy(dataFim = System.currentTimeMillis())
                    }
                    // Se a subtarefa for "reaberta"
                    else if (subtarefaAntiga?.status == Status.CONCLUIDA && subtarefaAtualizada.status != Status.CONCLUIDA) {
                        subtarefaParaSalvar = subtarefaParaSalvar.copy(dataFim = null)
                    }

                    val subtarefasNovas = subtarefasAntigas.map {
                        if (it.id == subtarefaParaSalvar.id) subtarefaParaSalvar else it
                    }
                    transaction.update(taskRef, "subtarefas", subtarefasNovas)
                }
            }.await()
        }
    }

    fun excluirSubtarefa(tarefaId: String, subtarefaId: String) {
        viewModelScope.launch {
            val taskRef = getTaskRef(tarefaId)
            db.runTransaction { transaction ->
                val taskSnapshot = transaction.get(taskRef)
                val task = taskSnapshot.toObject(Task::class.java)
                if (task != null) {
                    // Cria uma subtarefa "fantasma" apenas com o ID para o arrayRemove encontrar
                    val subtaskParaRemover = task.subtarefas.find { it.id == subtarefaId }
                    if(subtaskParaRemover != null) {
                        // Usa FieldValue.arrayRemove para remover o objeto do array
                        transaction.update(taskRef, "subtarefas", FieldValue.arrayRemove(subtaskParaRemover))
                    }
                }
            }.await()
        }
    }
}
package com.example.taskapp.ui.subtaskform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Subtask
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SubtaskFormViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Estado observável da subtarefa atual
    private val _subtask = MutableStateFlow<Subtask?>(null)
    val subtask: StateFlow<Subtask?> = _subtask

    /**
     * Carrega uma subtarefa específica de dentro de uma tarefa no Firestore.
     * Usado para edição.
     */
    fun carregarSubtarefa(tarefaId: String, subtarefaId: String) {
        viewModelScope.launch {
            try {
                val documento = db.collection("tarefas")
                    .document(tarefaId)
                    .collection("subtarefas")
                    .document(subtarefaId)
                    .get()
                    .await()

                _subtask.value = documento.toObject(Subtask::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                _subtask.value = null
            }
        }
    }

    /**
     * Cadastra uma nova subtarefa dentro de uma tarefa.
     */
    fun cadastrarSubtarefa(tarefaId: String, subtarefa: Subtask) {
        viewModelScope.launch {
            try {
                val referencia = db.collection("tarefas")
                    .document(tarefaId)
                    .collection("subtarefas")
                    .document()

                val novaSubtarefa = subtarefa.copy(id = referencia.id)

                referencia.set(novaSubtarefa).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Atualiza uma subtarefa existente.
     */
    fun atualizarSubtarefa(tarefaId: String, subtarefa: Subtask) {
        viewModelScope.launch {
            try {
                db.collection("tarefas")
                    .document(tarefaId)
                    .collection("subtarefas")
                    .document(subtarefa.id)
                    .set(subtarefa)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Exclui uma subtarefa específica.
     */
    fun excluirSubtarefa(tarefaId: String, subtarefaId: String) {
        viewModelScope.launch {
            try {
                db.collection("tarefas")
                    .document(tarefaId)
                    .collection("subtarefas")
                    .document(subtarefaId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun atualizarStatusSubtarefa(tarefaId: String, subtarefaId: String, novoStatus: Status) {
        viewModelScope.launch {
            try {
                val ref = db.collection("tasks")
                    .document(tarefaId)
                    .collection("subtasks")
                    .document(subtarefaId)

                ref.update("status", novoStatus.name).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

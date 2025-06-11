package com.example.taskapp.data.repository

import android.util.Log
import com.example.taskapp.data.model.Status
import com.example.taskapp.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "TaskRepository"

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado.")

    //lista tasks
    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val collection = firestore.collection("users").document(userId).collection("tasks")

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Erro ao ouvir tarefas em getTasks", error)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val tasks = snapshot.documents.mapNotNull { document ->
                    try {
                        document.toObject<Task>()
                    } catch (e: Exception) {
                        Log.e(TAG, "Falha ao converter documento ${document.id} para Task", e)
                        null
                    }
                }
                trySend(tasks)
            }
        }
        awaitClose { listener.remove() }
    }

    //atualizar tasks status
    suspend fun updateSubtaskStatus(taskId: String, subtaskId: String, newStatus: Status) {
        val taskRef = firestore.collection("users").document(userId).collection("tasks").document(taskId)
        try {
            firestore.runTransaction { transaction ->
                val task = transaction.get(taskRef).toObject<Task>()
                if (task != null) {
                    val updatedSubtasks = task.subtarefas.map { subtask ->
                        if (subtask.id == subtaskId) subtask.copy(status = newStatus) else subtask
                    }
                    transaction.update(taskRef, "subtarefas", updatedSubtasks)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Erro em updateSubtaskStatus para taskId: $taskId", e)
        }
    }

    //buscatask por id (util para criar subtarefa)
    suspend fun getTaskById(taskId: String): Task? {
        return try {
            firestore.collection("users").document(userId)
                .collection("tasks").document(taskId)
                .get().await().toObject(Task::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Erro em getTaskById para taskId: $taskId", e)
            null
        }
    }

    //atualizar task
    suspend fun updateTask(task: Task) {
        try {
            firestore.collection("users").document(userId)
                .collection("tasks").document(task.id)
                .set(task).await()
        } catch (e: Exception) {
            Log.e(TAG, "Erro em updateTask para taskId: ${task.id}", e)
        }
    }

    //create tasks
    suspend fun addTask(task: Task) {
        try {
            val taskRef = firestore.collection("users").document(userId).collection("tasks").document()
            val novaTarefa = task.copy(id = taskRef.id)
            taskRef.set(novaTarefa).await()
        } catch (e: Exception) {
            Log.e(TAG, "Erro em addTask", e)
        }
    }

  //excluir task
    suspend fun deleteTask(taskId: String) {
        try {
            firestore.collection("users").document(userId)
                .collection("tasks").document(taskId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Erro em deleteTask para taskId: $taskId", e)
        }
    }
}
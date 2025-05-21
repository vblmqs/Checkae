package com.example.taskapp.ui.tasklist

import androidx.lifecycle.ViewModel
import com.example.taskapp.model.Task
import com.example.taskapp.model.Subtask
import com.example.taskapp.model.Status
import com.example.taskapp.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class TaskViewModel : ViewModel() {

    // Estado interno (mutável)
    private val _tasks = MutableStateFlow(sampleTasks())

    // Exposição para a UI (imutável)
    val tasks: StateFlow<List<Task>> = _tasks

    }

/*
implementar corretamente
fun atualizarStatusSubtarefa(taskId: String, subtaskId: String, novoStatus: Status) {

    _tasks.value = _tasks.value.map { task ->
        if (task.id == taskId) {
            task.copy(
                subtarefas = task.subtarefas.map { subtask ->
                    if (subtask.id == subtaskId) {
                        subtask.copy(status = novoStatus)
                    } else subtask
                }
            )
        } else task
    }
}
*/

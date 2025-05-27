package com.example.taskapp.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.model.Task
import com.example.taskapp.data.model.Subtask
import com.example.taskapp.data.model.Status
import com.example.taskapp.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
fun sampleTasks(): List<Task> {
    val now = System.currentTimeMillis()

    val subtasks1 = listOf(
        Subtask(
            id = "sub1",
            titulo = "Comprar leite",
            descricao = "Leite integral 1L",
            status = Status.INICIADA,
            prazo = now + 2 * 3600_000, // 2 horas a partir de agora
            dataInicio = now
        ),
        Subtask(
            id = "sub2",
            titulo = "Comprar pão",
            descricao = "Pão integral",
            status = Status.CONCLUIDA,
            prazo = now + 3 * 3600_000, // 3 horas
            dataInicio = now
        )
    )

    val subtasks2 = listOf(
        Subtask(
            id = "sub3",
            titulo = "Ler capítulo 1 do livro",
            descricao = "Kotlin para Android",
            status = Status.INICIADA,
            prazo = now + 24 * 3600_000, // 1 dia
            dataInicio = now
        )
    )

    return listOf(
        Task(
            id = "task1",
            titulo = "Fazer compras",
            descricao = "Lista de compras para a semana",
            prioridade = Priority.ALTA,
            status = Status.INICIADA,
            dataInicio = now,
            prazoManual = now + 4 * 3600_000, // 4 horas
            subtarefas = subtasks1
        ),
        Task(
            id = "task2",
            titulo = "Estudar Kotlin",
            descricao = "Preparar para prova",
            prioridade = Priority.MEDIA,
            status = Status.INICIADA,
            dataInicio = now,
            prazoManual = now + 48 * 3600_000, // 2 dias
            subtarefas = subtasks2
        ),
        Task(
            id = "task3",
            titulo = "Organizar quarto",
            descricao = "Limpar e arrumar o quarto",
            prioridade = Priority.BAIXA,
            status = Status.INICIADA,
            dataInicio = now,
            prazoManual = now + 72 * 3600_000 // 3 dias
            // Sem subtarefas
        )
    )
}

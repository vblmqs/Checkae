package com.example.taskapp.model

import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Subtask

data class Task(
    val id: String = "", // Gerado automaticamente pelo Firebase
    val titulo: String = "",
    val descricao: String? = null,
    val prioridade: Priority = Priority.MEDIA,
    val status: Status = Status.INICIADA,
    val dataInicio: Long = System.currentTimeMillis(), // Em millis
    val dataFim: Long? = null, // Em millis
    val prazoManual: Long = System.currentTimeMillis() + 86400000L, // 1 dia depois por padrão
    val subtarefas: List<Subtask> = emptyList()
) {
    // Duração formatada (hh:mm:ss)
    val duracao: String
        get() = if (dataFim != null) {
            formatarDuracao(dataFim - dataInicio)
        } else {
            "N/A"
        }

    // Prazo calculado com base nas subtarefas (ou manual se não tiver subtarefa)
    val prazoCalculado: Long
        get() = if (subtarefas.isNotEmpty()) {
            subtarefas.maxOfOrNull { it.prazo ?: 0L } ?: prazoManual
        } else {
            prazoManual
        }

    private fun formatarDuracao(millis: Long): String {
        val segundos = millis / 1000 % 60
        val minutos = millis / (1000 * 60) % 60
        val horas = millis / (1000 * 60 * 60)
        return String.format("%02d:%02d:%02d", horas, minutos, segundos)
    }

}

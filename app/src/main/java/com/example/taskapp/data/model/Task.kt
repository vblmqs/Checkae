package com.example.taskapp.model

import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Subtask
import java.util.concurrent.TimeUnit

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
    // Verifica se todas as subtarefas estão concluídas
    val todasSubtarefasConcluidas: Boolean
        get() = subtarefas.isNotEmpty() && subtarefas.all { it.status == Status.CONCLUIDA }

    // Duração formatada
    val duracao: String
        get() {
            // Se a Task não tem subtarefas, sua duração é baseada em seu próprio dataFim
            if (subtarefas.isEmpty()) {
                return if (dataFim != null) {
                    formatarDuracao(dataFim - dataInicio)
                } else {
                    "N/A"
                }
            } else {
                // Se tem subtarefas, a duração só é calculada se TODAS as subtarefas estiverem concluídas
                // E a Task principal também estiver marcada como CONCLUIDA (via status da Task)
                if (todasSubtarefasConcluidas && status == Status.CONCLUIDA) {
                    // Encontra a data de fim mais recente entre as subtarefas concluídas
                    val ultimaDataFimSubtarefa = subtarefas
                        .filter { it.status == Status.CONCLUIDA && it.dataFim != null }
                        .maxOfOrNull { it.dataFim!! }

                    return if (ultimaDataFimSubtarefa != null) {
                        formatarDuracao(ultimaDataFimSubtarefa - dataInicio)
                    } else {
                        "N/A"
                    }
                } else {
                    // Se nem todas as subtarefas estão concluídas ou a Task principal não está CONCLUIDA
                    return "N/A"
                }
            }
        }

    // Prazo calculado com base nas subtarefas (ou manual se não tiver subtarefa)
    val prazoCalculado: Long
        get() = if (subtarefas.isNotEmpty()) {
            // Pega o maior prazo entre as subtarefas. Se nenhuma tiver prazo (null), usa o prazoManual da Task
            subtarefas.maxOfOrNull { it.prazo ?: 0L } ?: prazoManual
        } else {
            prazoManual
        }

    // formatarDuracao PADRONIZADA IGUAL À DA SUBTASK ---
    private fun formatarDuracao(millis: Long): String {
        if (millis < 0) return "Inválida" // Garante que durações negativas não causem problemas

        val umMinutoEmMs = TimeUnit.MINUTES.toMillis(1)
        val umaHoraEmMs = TimeUnit.HOURS.toMillis(1)
        val umDiaEmMs = TimeUnit.DAYS.toMillis(1)
        val umMesEmMs = umDiaEmMs * 30

        return when {
            millis >= umMesEmMs -> {
                val meses = millis / umMesEmMs
                if (meses == 1L) "$meses mês" else "$meses meses"
            }
            millis >= umDiaEmMs -> {
                val dias = millis / umDiaEmMs
                if (dias == 1L) "$dias dia" else "$dias dias"
            }
            millis >= umaHoraEmMs -> {
                val horas = millis / umaHoraEmMs
                if (horas == 1L) "$horas hora" else "$horas horas"
            }
            millis >= umMinutoEmMs -> {
                val minutos = millis / umMinutoEmMs
                if (minutos == 1L) "$minutos minuto" else "$minutos minutos"
            }
            millis > 0 -> "Menos de 1 minuto"
            else -> "Inválida"
        }
    }
}

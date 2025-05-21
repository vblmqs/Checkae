package com.example.taskapp.model

data class Subtask(
    val id: String = "",
    val titulo: String = "",
    val descricao: String? = null,
    val status: Status = Status.INICIADA,
    val prazo: Date,
    val dataInicio: Long = System.currentTimeMillis(),
    val dataFim: Long? = null
) {

    // Duração formatada (hh:mm:ss)
    val duracao: String
        get() = if (dataFim != null) {
            formatarDuracao(dataFim - dataInicio)
        } else {
            "N/A"
        }

    private fun formatarDuracao(millis: Long): String {
        val segundos = millis / 1000 % 60
        val minutos = millis / (1000 * 60) % 60
        val horas = millis / (1000 * 60 * 60)
        return String.format("%02d:%02d:%02d", horas, minutos, segundos)
    }
}

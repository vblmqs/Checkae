package com.example.taskapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DeadlineNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            val now = LocalDateTime.now()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.failure()

            val tarefasSnapshot = firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .await()

            for (taskDoc in tarefasSnapshot.documents) {
                val taskId = taskDoc.id
                val taskTitulo = taskDoc.getString("titulo") ?: "Tarefa sem título"

                val prazoManual = taskDoc.getLong("prazoManual")
                if (prazoManual != null) {
                    val deadline = Instant.ofEpochMilli(prazoManual)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                    if (now.toLocalDate() == deadline.toLocalDate()) {
                        sendNotification(
                            id = "tarefa_$taskId",
                            titulo = "Tarefa com prazo hoje!",
                            mensagem = "“$taskTitulo” vence hoje."
                        )
                    }
                }

                @Suppress("UNCHECKED_CAST")
                val subtarefas = taskDoc.get("subtarefas") as? List<HashMap<String, Any>> ?: emptyList()
                for (sub in subtarefas) {
                    val subTitulo = sub["titulo"] as? String ?: continue
                    val prazo = (sub["prazo"] as? Number)?.toLong() ?: continue

                    val subDeadline = Instant.ofEpochMilli(prazo)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()

                    if (now.toLocalDate() == subDeadline.toLocalDate()) {
                        sendNotification(
                            id = "sub_${taskId}_${subTitulo.hashCode()}",
                            titulo = "Subtarefa com prazo hoje!",
                            mensagem = "“$subTitulo” da tarefa “$taskTitulo” vence hoje."
                        )
                    }
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun sendNotification(id: String, titulo: String, mensagem: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "subtask_deadline_channel",
                "Tarefas/Subtarefas Pendentes",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "subtask_deadline_channel")
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }
}
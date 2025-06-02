package com.example.taskapp.ui.taskform

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskapp.components.CampoData
import com.example.taskapp.components.CampoFormulario
import com.example.taskapp.components.BotoesFormulario
import com.example.taskapp.components.Header
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskFormScreen(navController: NavHostController) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var prazo by remember { mutableStateOf(calendar.timeInMillis) }

    val showDatePicker = {
        val today = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                prazo = Calendar.getInstance().apply {
                    set(year, month, day)
                }.timeInMillis
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(
                titulo = "Cadastrar tarefa",
                onVoltar = { navController.popBackStack() },
                onClickIconeDireita = {
                    // ação
                }
            )

            CampoFormulario(label = "Título", value = titulo, onValueChange = { titulo = it })
            CampoFormulario(label = "Descrição", value = descricao, onValueChange = { descricao = it })
            CampoFormulario(label = "Prioridade", value = prioridade, onValueChange = { prioridade = it })
            CampoFormulario(label = "Status", value = status, onValueChange = { status = it })
            CampoData(
                label = "Prazo",
                value = dateFormat.format(Date(prazo)),
                onClick = { showDatePicker() }
            )

            Spacer(modifier = Modifier.weight(1f))

            BotoesFormulario(
                onDelete = { /* excluir tarefa */ },
                onConfirm = { /* confirmar tarefa */ }
            )
        }
    }
}

package com.example.taskapp.ui.subtaskform

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Necessário para ExperimentalMaterial3Api se CampoCombobox usar TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.components.CampoData
import com.example.taskapp.components.CampoFormulario
import com.example.taskapp.components.BotoesFormulario // Assumindo que aceita onDelete opcional
import com.example.taskapp.components.Header
import com.example.taskapp.components.CampoCombobox // Nosso novo componente de combobox
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Subtask
import java.text.SimpleDateFormat
import java.util.*

// Função auxiliar para obter o timestamp de hoje (início do dia) como padrão para a UI
private fun getDefaultPrazoForUI(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class) // Pode ser necessário para CampoCombobox
@Composable
fun SubtaskFormScreen(
    navController: NavHostController,
    tarefaId: String,
    subtarefaId: String?,
    subtaskViewModel: SubtaskFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditing = subtarefaId != null // Determina se está em modo de edição

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    // O status padrão é INICIADA. Se não estiver editando, o usuário não muda isso na UI.
    var statusSelecionado by remember { mutableStateOf(Status.INICIADA) }
    var prazo by remember { mutableStateOf(getDefaultPrazoForUI()) }

    val subtaskState by subtaskViewModel.subtask.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(key1 = subtarefaId) {
        if (isEditing) {
            subtaskViewModel.carregarSubtarefa(tarefaId, subtarefaId!!)
        } else {
            // Modo de criação: reseta os campos
            titulo = ""
            descricao = ""
            statusSelecionado = Status.INICIADA // Garante o padrão para novas subtarefas
            prazo = getDefaultPrazoForUI()
        }
    }

    LaunchedEffect(key1 = subtaskState) {
        // Popula os campos apenas se estiver editando e houver dados carregados
        if (isEditing) {
            subtaskState?.let { loadedSubtask ->
                titulo = loadedSubtask.titulo
                descricao = loadedSubtask.descricao ?: ""
                statusSelecionado = loadedSubtask.status
                prazo = loadedSubtask.prazo // Prazo virá não-nulo do modelo atualizado
            }
        }
    }

    val showDatePicker = {
        val calendar = Calendar.getInstance().apply { timeInMillis = prazo }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year); set(Calendar.MONTH, month); set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                prazo = selectedCalendar.timeInMillis
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(modifier = Modifier.fillMaxSize() .background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(
                titulo = if (isEditing) "Editar Subtarefa" else "Cadastrar Subtarefa",
                onVoltar = { navController.popBackStack() },
                onClickIconeDireita = if (isEditing) {
            {
                // Coloque aqui a ação que deve acontecer quando o ícone de notificações for clicado
                // APENAS NO MODO DE EDIÇÃO.
                println("Ícone de notificações clicado no modo EDIÇÃO!")
            }
        } else null // Passa null quando não está editando (isEditing é false)
            )

            CampoFormulario(label = "Título", value = titulo, onValueChange = { titulo = it })
            CampoFormulario(label = "Descrição", value = descricao, onValueChange = { descricao = it })

            // ---- CAMPO STATUS CONDICIONAL ----
            if (isEditing) {
                CampoCombobox(
                    label = "Status",
                    options = Status.values().toList(),
                    selectedOption = statusSelecionado,
                    onOptionSelected = { statusSelecionado = it },
                    optionToDisplayedString = { status ->
                        // Ex: when (status) { Status.INICIADA -> "Iniciada"; ... }
                        status.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    },
                    placeholder = "Selecione o Status"
                )
            } // Fim do if (isEditing) para o campo Status

            CampoData(
                label = "Prazo",
                value = dateFormat.format(Date(prazo)),
                onClick = { showDatePicker() }
            )

            Spacer(modifier = Modifier.weight(1f))

            BotoesFormulario(
                onConfirm = {
                    if (!isEditing) { // Criar nova subtarefa
                        val novaSubtarefa = Subtask(
                            titulo = titulo,
                            descricao = if (descricao.isBlank()) null else descricao,
                            status = Status.INICIADA, // Status padrão para novas subtarefas
                            prazo = prazo,
                            dataInicio = System.currentTimeMillis()
                        )
                        subtaskViewModel.cadastrarSubtarefa(tarefaId, novaSubtarefa)
                    } else { // Atualizar subtarefa existente
                        val subtaskCarregada = subtaskState
                        if (subtaskCarregada != null) {
                            val subtaskAtualizada = subtaskCarregada.copy(
                                titulo = titulo,
                                descricao = if (descricao.isBlank()) null else descricao,
                                status = statusSelecionado, // Status selecionado pelo usuário no modo edição
                                prazo = prazo
                            )
                            subtaskViewModel.atualizarSubtarefa(tarefaId, subtaskAtualizada)
                        }
                    }
                    navController.popBackStack()
                },
                // ---- BOTÃO EXCLUIR CONDICIONAL ----
                // Passa a ação de deletar SOMENTE se estiver editando
                onDelete = if (isEditing) {
                    {
                        // O subtarefaId é não-nulo aqui por causa da verificação isEditing
                        subtaskViewModel.excluirSubtarefa(tarefaId, subtarefaId!!)
                        navController.popBackStack()
                    }
                } else null // Se não estiver editando, onDelete é null (o botão não deve aparecer)
            )
        }
    }
}
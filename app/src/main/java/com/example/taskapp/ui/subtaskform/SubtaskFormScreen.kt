package com.example.taskapp.ui.subtaskform

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.components.BotoesFormulario
import com.example.taskapp.components.CampoCombobox
import com.example.taskapp.components.CampoData
import com.example.taskapp.components.CampoFormulario
import com.example.taskapp.components.Header
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

@OptIn(ExperimentalMaterial3Api::class)
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
    var statusSelecionado by remember { mutableStateOf(Status.INICIADA) }
    var prazo by remember { mutableStateOf(getDefaultPrazoForUI()) }

    val subtaskState by subtaskViewModel.subtask.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(key1 = subtarefaId) {
        if (isEditing) {
            subtaskViewModel.carregarSubtarefa(tarefaId, subtarefaId!!)
        } else {
            // Modo de criação: reseta os campos para garantir que não haja dados de edições anteriores
            titulo = ""
            descricao = ""
            statusSelecionado = Status.INICIADA
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
                prazo = loadedSubtask.prazo
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
                    // Zera o tempo para consistência
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                prazo = selectedCalendar.timeInMillis
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(
                titulo = if (isEditing) "Editar Subtarefa" else "Cadastrar Subtarefa",
                onVoltar = { navController.popBackStack() },
                onClickIconeDireita = null // Defina uma ação se necessário
            )

            CampoFormulario(label = "Título", value = titulo, onValueChange = { titulo = it })
            CampoFormulario(label = "Descrição", value = descricao, onValueChange = { descricao = it })

            // O campo Status só aparece no modo de edição
            if (isEditing) {
                CampoCombobox(
                    label = "Status",
                    options = Status.values().toList(),
                    selectedOption = statusSelecionado,
                    onOptionSelected = { statusSelecionado = it },
                    optionToDisplayedString = { status ->
                        status.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
                    },
                    placeholder = "Selecione o Status"
                )
            }

            CampoData(
                label = "Prazo",
                value = dateFormat.format(Date(prazo)),
                onClick = { showDatePicker() }
            )

            Spacer(modifier = Modifier.weight(1f))

            BotoesFormulario(
                onConfirm = {
                    if (titulo.isBlank()) {
                        Toast.makeText(context, "O título da subtarefa é obrigatório.", Toast.LENGTH_SHORT).show()
                        return@BotoesFormulario
                    }

                    if (!isEditing) {
                        val novaSubtarefa = Subtask(
                            // O ID será gerado automaticamente pelo ViewModel
                            titulo = titulo,
                            descricao = if (descricao.isBlank()) null else descricao,
                            status = Status.INICIADA, // Novas subtarefas sempre iniciam com este status
                            prazo = prazo,
                            dataInicio = System.currentTimeMillis()
                        )
                        subtaskViewModel.cadastrarSubtarefa(tarefaId, novaSubtarefa)
                    } else { // Atualizar subtarefa existente
                        subtaskState?.let { subtaskCarregada ->
                            val subtaskAtualizada = subtaskCarregada.copy(
                                titulo = titulo,
                                descricao = if (descricao.isBlank()) null else descricao,
                                status = statusSelecionado, // Usa o status que o usuário selecionou na UI
                                prazo = prazo
                            )
                            subtaskViewModel.atualizarSubtarefa(tarefaId, subtaskAtualizada)
                        }
                    }
                    navController.popBackStack()
                },
                // O botão de excluir só aparece no modo de edição
                onDelete = if (isEditing) {
                    {
                        subtaskViewModel.excluirSubtarefa(tarefaId, subtarefaId!!)
                        navController.popBackStack()
                    }
                } else null
            )
        }
    }
}
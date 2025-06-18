package com.example.taskapp.ui.taskform

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
import com.example.taskapp.model.Priority
import com.example.taskapp.model.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskFormScreen(
    navController: NavHostController,
    tarefaId: String? = null,
    viewModel: TaskFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditing = tarefaId != null
    val taskState by viewModel.task.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf(Priority.MEDIA) }
    var status by remember { mutableStateOf(Status.INICIADA) }
    var prazo by remember { mutableStateOf(System.currentTimeMillis()) }


    LaunchedEffect(key1 = tarefaId) {
        if (isEditing) {
            viewModel.carregarTarefa(tarefaId!!)
        }
    }

    LaunchedEffect(key1 = taskState) {
        if (isEditing) {
            taskState?.let { loadedTask ->
                titulo = loadedTask.titulo
                descricao = loadedTask.descricao ?: ""
                prioridade = loadedTask.prioridade
                status = loadedTask.status
                prazo = loadedTask.prazoManual
            }
        }
    }

    val formResult by viewModel.formResult.collectAsState()
    LaunchedEffect(formResult) {
        when (val result = formResult) {
            is FormResult.Success -> {
                Toast.makeText(context, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                viewModel.resetFormResult()
            }
            is FormResult.Error -> {
                Toast.makeText(context, "Erro: ${result.message}", Toast.LENGTH_LONG).show()
                viewModel.resetFormResult()
            }
            else -> {}
        }
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val showDatePicker = {
        val calendar = Calendar.getInstance().apply { timeInMillis = prazo }
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            prazo = calendar.timeInMillis
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(
                titulo = if (isEditing) "Editar Tarefa" else "Cadastrar Tarefa",
                onVoltar = { navController.popBackStack() }
            )

            CampoFormulario(label = "Título", value = titulo, onValueChange = { titulo = it })
            CampoFormulario(label = "Descrição", value = descricao, onValueChange = { descricao = it })
            CampoCombobox(label = "Prioridade", options = Priority.values().toList(), selectedOption = prioridade, onOptionSelected = { prioridade = it }, optionToDisplayedString = { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } })

            // O campo de status é editável. A lógica de dataFim é tratada no ViewModel ao salvar.
            val canMarkAsCompleted = taskState?.todasSubtarefasConcluidas ?: true
            val statusOptions = if (taskState?.subtarefas?.isNotEmpty() == true) {
                // Se tem subtarefas, a opção "Concluída" só é habilitada se todas as subtasks estiverem concluídas.
                Status.values().toList().filter { it != Status.CONCLUIDA || canMarkAsCompleted || status == Status.CONCLUIDA }
            } else {
                Status.values().toList() // Se não tem subtarefas, todas as opções de status estão disponíveis
            }

            CampoCombobox(
                label = "Status",
                options = statusOptions,
                selectedOption = status,
                onOptionSelected = { newStatus ->
                    // Permite alterar o status apenas se for válido
                    if (newStatus == Status.CONCLUIDA && taskState?.subtarefas?.isNotEmpty() == true && !canMarkAsCompleted) {
                        Toast.makeText(context, "Conclua todas as subtarefas primeiro.", Toast.LENGTH_SHORT).show()
                    } else {
                        status = newStatus
                    }
                },
                optionToDisplayedString = { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
            )

            CampoData(label = "Prazo", value = dateFormat.format(Date(prazo)), onClick = { showDatePicker() })

            Spacer(modifier = Modifier.weight(1f))

            BotoesFormulario(
                onConfirm = {
                    if (titulo.isNotBlank()) {
                        val taskParaSalvar = Task(
                            id = if (isEditing) tarefaId!! else "",
                            titulo = titulo,
                            descricao = descricao.ifBlank { null },
                            prioridade = prioridade,
                            status = status, // O status é o que o usuário selecionou
                            prazoManual = prazo, // prazoManual é o que o usuário escolheu
                            subtarefas = if (isEditing) taskState?.subtarefas ?: emptyList() else emptyList()
                        )
                        viewModel.salvarOuAtualizarTarefa(taskParaSalvar)
                    } else {
                        Toast.makeText(context, "O título é obrigatório.", Toast.LENGTH_SHORT).show()
                    }
                },
                onDelete = if (isEditing) { { viewModel.excluirTarefa(tarefaId!!) } } else null
            )
        }
    }
}
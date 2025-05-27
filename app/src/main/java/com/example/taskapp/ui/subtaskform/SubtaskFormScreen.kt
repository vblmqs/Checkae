package com.example.taskapp.ui.subtaskform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Subtask
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtaskFormScreen(
    tarefaId: String,
    navController: NavHostController,
    subtarefaId: String? = null, // null para criar nova subtarefa
    viewModel: SubtaskFormViewModel = viewModel(),
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val subtask by viewModel.subtask.collectAsState()

    // Estados locais dos campos do formulário
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(Status.INICIADA) }
    var prazo by remember { mutableStateOf<Long?>(null) }

    // Carrega subtarefa para edição
    LaunchedEffect(subtarefaId) {
        if (subtarefaId != null) {
            viewModel.carregarSubtarefa(tarefaId, subtarefaId)
        }
    }

    // Atualiza campos quando subtarefa é carregada
    LaunchedEffect(subtask) {
        subtask?.let {
            titulo = it.titulo
            descricao = it.descricao ?: ""
            status = it.status
            prazo = it.prazo
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (subtarefaId == null) "Nova Subtarefa" else "Editar Subtarefa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (subtarefaId != null) {
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.excluirSubtarefa(tarefaId, subtarefaId)
                                onBack()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir subtarefa")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatusDropdown(
                selectedStatus = status,
                onStatusSelected = { status = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = prazo?.toString() ?: "",
                onValueChange = { prazo = it.toLongOrNull() },
                label = { Text("Prazo (timestamp millis)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.isBlank()) return@Button

                    val novaSubtarefa = Subtask(
                        id = subtask?.id ?: "",
                        titulo = titulo,
                        descricao = if (descricao.isBlank()) null else descricao,
                        status = status,
                        prazo = prazo,
                        dataInicio = subtask?.dataInicio ?: System.currentTimeMillis(),
                        dataFim = subtask?.dataFim
                    )

                    scope.launch {
                        if (subtarefaId == null) {
                            viewModel.cadastrarSubtarefa(tarefaId, novaSubtarefa)
                        } else {
                            viewModel.atualizarSubtarefa(tarefaId, novaSubtarefa)
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (subtarefaId == null) "Criar" else "Atualizar")
            }
        }
    }
}

@Composable
fun StatusDropdown(
    selectedStatus: Status,
    onStatusSelected: (Status) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedStatus.name,
            onValueChange = {},
            label = { Text("Status") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Selecionar status"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Status.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option.name) }, // Passe o Text para o parâmetro 'text'
                            onClick = {
                                onStatusSelected(option)
                                expanded = false
                            }
                            // Não há mais um lambda de conteúdo no final para o texto principal
                        )


// ...
            }
        }
    }
}

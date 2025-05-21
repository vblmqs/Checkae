package com.example.taskapp.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.model.Subtask
import com.example.taskapp.ui.tasklist.TaskViewModel

@Composable
fun TaskListScreen(navController: NavHostController) {
    val viewModel: TaskViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()
    var expandedTaskId by remember { mutableStateOf<String?>(null) }
    var filterOpen by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    // cabeçalho da tela, nome e ícone de logout
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Minhas tarefas", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { /* Implementar sair*/ }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // barra de pesquisa, implementar busca
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar tarefa") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // filtro que permite filtrar as tarefas por prioridade e status
            Box {
                IconButton(onClick = { filterOpen = !filterOpen }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtro")
                }

                DropdownMenu(expanded = filterOpen, onDismissRequest = { filterOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Prioridade") },
                        onClick = { /* implementar filtro por prioridade */ }
                    )
                    DropdownMenuItem(
                        text = { Text("Status") },
                        onClick = { /* implementar filtro por status */ }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // lista de tarefas
        LazyColumn {
            items(tasks) { task ->
                var lastClickTime by remember { mutableStateOf(0L) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFDFFFE1), shape = RoundedCornerShape(15.dp))
                        .clickable {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime < 300) {
                                navController.navigate("EditarTarefa/${task.id}")
                            } else {
                                expandedTaskId = if (expandedTaskId == task.id) null else task.id
                            }
                            lastClickTime = currentTime
                        }
                        .padding(12.dp)
                ) {
                    Text(task.titulo, fontWeight = FontWeight.Bold)

                    if (expandedTaskId == task.id) {
                        Spacer(modifier = Modifier.height(8.dp))

                        task.subtarefas.forEach { subtask ->

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {

                                //check para concluir tarefa rapidamente
                                Checkbox(
                                    checked = subtask.status == Status.CONCLUIDA,
                                    onCheckedChange = { isChecked ->
                                        val novoStatus = if (isChecked) Status.CONCLUIDA else Status.INICIADA

                                        // Atualiza o status da subtask no ViewModel -- implementar viewmodel
                                        viewModel.atualizarStatusSubtarefa(
                                            taskId = task.id,
                                            subtaskId = subtask.id,
                                            novoStatus = novoStatus
                                        )
                                    }
                                )

                                Column {
                                    Text(subtask.titulo)
                                    Text("Prazo: ${subtask.prazo}")
                                    Text("Duração: ${subtask.duracao}")
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Button(onClick = { expandedTaskId = null }) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                navController.navigate("CadastrarSubtarefa/${task.id}")
                            }) {
                                Text("Nova subtarefa")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // botão de nova tarefa
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                navController.navigate("CadastrarTarefa")
            }) {
                Text("Nova tarefa")
            }
        }
    }
}

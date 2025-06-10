package com.example.taskapp.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.data.model.Status
import com.example.taskapp.model.Priority
import com.example.taskapp.model.Task
import com.example.taskapp.ui.subtaskform.SubtaskFormViewModel
import com.example.taskapp.ui.theme.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TaskListScreen(navController: NavHostController, themeViewModel: ThemeViewModel) {
    val viewModel: TaskViewModel = viewModel()
    val tarefas by viewModel.tasks.collectAsState()
    var tarefaExpandidaId by remember { mutableStateOf<String?>(null) }
    val subtaskFormViewModel: SubtaskFormViewModel = viewModel()
    var mostrarModalLogout by remember { mutableStateOf(false) }
    var filtroAberto by remember { mutableStateOf(false) }
    var busca by remember { mutableStateOf("") }
    var filtroAtivo by remember { mutableStateOf("Nenhum") }

    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value

    fun filtrarTarefas(tarefas: List<Task>): List<Task> {
        var listaFiltrada = tarefas.filter {
            it.titulo.contains(busca, ignoreCase = true)
        }

        listaFiltrada = when (filtroAtivo) {
            "Status" -> {
                listaFiltrada.sortedWith(compareBy { tarefa ->
                    tarefa.subtarefas.minOfOrNull { subtarefa ->
                        when (subtarefa.status) {
                            Status.INICIADA -> 0
                            Status.PAUSADA -> 1
                            Status.CONCLUIDA -> 2
                        }
                    } ?: 3
                })
            }
            "Prioridade" -> {
                listaFiltrada.sortedWith(compareBy {
                    when (it.prioridade) {
                        Priority.ALTA -> 0
                        Priority.MEDIA -> 1
                        Priority.BAIXA -> 2
                    }
                })
            }
            else -> listaFiltrada
        }

        return listaFiltrada
    }

    val tarefasFiltradas = filtrarTarefas(tarefas)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp)) {

        // Cabeçalho com título e botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Minhas tarefas", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
            Row {
                IconButton(onClick = { themeViewModel.toggleTheme() }) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Alternar Tema",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(onClick = { mostrarModalLogout = true }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de busca + filtro
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it },
                placeholder = { Text("Buscar tarefa", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurface)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                IconButton(onClick = { filtroAberto = !filtroAberto }) {
                    Icon(Icons.Default.FilterAlt, contentDescription = "Filtro", tint = MaterialTheme.colorScheme.onBackground)
                }
                DropdownMenu(
                    expanded = filtroAberto,
                    onDismissRequest = { filtroAberto = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nenhum") },
                        onClick = {
                            filtroAtivo = "Nenhum"
                            filtroAberto = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Status") },
                        onClick = {
                            filtroAtivo = "Status"
                            filtroAberto = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Prioridade") },
                        onClick = {
                            filtroAtivo = "Prioridade"
                            filtroAberto = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tarefas com subtarefas
        LazyColumn {
            items(tarefasFiltradas) { tarefa ->
                var ultimoClique by remember { mutableStateOf(0L) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFDFFFE1), RoundedCornerShape(15.dp))
                        .clickable {
                            val agora = System.currentTimeMillis()
                            if (agora - ultimoClique < 300) {
                                navController.navigate("editarTarefa/${tarefa.id}")
                            } else {
                                tarefaExpandidaId = if (tarefaExpandidaId == tarefa.id) null else tarefa.id
                            }
                            ultimoClique = agora
                        }
                        .padding(12.dp)
                ) {
                    Text(tarefa.titulo, fontWeight = FontWeight.Bold)

                    if (tarefaExpandidaId == tarefa.id) {
                        Spacer(modifier = Modifier.height(8.dp))

                        tarefa.subtarefas
                            .sortedBy { it.prazo ?: Long.MAX_VALUE }
                            .forEach { subtarefa ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = subtarefa.status == Status.CONCLUIDA,
                                        onCheckedChange = { marcado ->
                                            val novoStatus = if (marcado) Status.CONCLUIDA else Status.INICIADA
                                            subtaskFormViewModel.atualizarStatusSubtarefa(
                                                tarefaId = tarefa.id,
                                                subtarefaId = subtarefa.id,
                                                novoStatus = novoStatus
                                            )
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .clickable {
                                                navController.navigate("editarSubtarefa/${subtarefa.id}")
                                            }
                                            .padding(start = 8.dp)
                                    ) {
                                        Text(subtarefa.titulo, fontWeight = FontWeight.Medium)
                                        Text("Status: ${subtarefa.status.name}")
                                        Text("Prazo: ${subtarefa.prazo ?: "Sem prazo"}")
                                    }
                                }
                            }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Button(
                                onClick = { tarefaExpandidaId = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    navController.navigate("subtaskForm/new/${tarefa.id}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Nova subtarefa")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { navController.navigate("CadastrarTarefa") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Nova tarefa")
            }
        }

        if (mostrarModalLogout) {
            AlertDialog(
                onDismissRequest = { mostrarModalLogout = false },
                confirmButton = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        mostrarModalLogout = false
                        navController.navigate("login") {
                            popUpTo("ListaTarefas") { inclusive = true }
                        }
                    }) {
                        Text("Sim", color = Color(0xFF2E7D32))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarModalLogout = false }) {
                        Text("Não", color = Color(0xFF2E7D32))
                    }
                },
                text = {
                    Text(
                        "Tem certeza que deseja sair?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    )
                },
                containerColor = Color(0xFFF0FFF1),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}


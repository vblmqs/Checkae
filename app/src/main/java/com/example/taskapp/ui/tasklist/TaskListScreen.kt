package com.example.taskapp.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.data.model.Status
import com.example.taskapp.model.Priority
import com.example.taskapp.model.Task
import com.example.taskapp.ui.theme.TaskAppTheme
import com.example.taskapp.ui.theme.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatarPrazo(prazo: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(prazo))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavHostController, themeViewModel: ThemeViewModel) {
    val viewModel: TaskViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value

    var tarefaExpandidaId by remember { mutableStateOf<String?>(null) }
    var mostrarModalLogout by remember { mutableStateOf(false) }

    TaskAppTheme(darkTheme = isDarkTheme) {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderSection(
                    onThemeToggle = { themeViewModel.toggleTheme() },
                    onLogout = { mostrarModalLogout = true },
                    isDarkTheme = isDarkTheme
                )

                SearchAndFilterSection(viewModel = viewModel, uiState = uiState)

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.tasks, key = { it.id }) { tarefa ->
                            TaskCard(
                                task = tarefa,
                                isExpanded = tarefaExpandidaId == tarefa.id,
                                onExpandToggle = {
                                    tarefaExpandidaId = if (tarefaExpandidaId == tarefa.id) null else tarefa.id
                                },
                                onSubtaskStatusChange = { subtaskId, newStatus ->
                                    viewModel.onSubtaskStatusChanged(tarefa.id, subtaskId, newStatus)
                                },
                                onEditTask = { navController.navigate("editarTarefa/${tarefa.id}") },
                                onEditSubtask = { subtaskId -> navController.navigate("subtaskForm/${tarefa.id}/$subtaskId") },
                                onNewSubtask = { navController.navigate("subtaskForm/${tarefa.id}/new") }
                            )
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate("CadastrarTarefa") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("Nova tarefa", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }

        if (mostrarModalLogout) {
            AlertDialog(
                onDismissRequest = { mostrarModalLogout = false },
                confirmButton = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        mostrarModalLogout = false
                        navController.navigate("login") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                    }) { Text("Sim") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarModalLogout = false }) { Text("Não") }
                },
                title = { Text("Confirmar Saída") },
                text = { Text("Tem certeza que deseja sair?") }
            )
        }
    }
}

@Composable
private fun HeaderSection(onThemeToggle: () -> Unit, onLogout: () -> Unit, isDarkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Minhas tarefas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Alternar Tema"
                )
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(viewModel: TaskViewModel, uiState: TaskListUiState) {
    var filtroPrioridadeAberto by remember { mutableStateOf(false) }
    var filtroStatusAberto by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Buscar tarefa...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { filtroPrioridadeAberto = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape
                ) {
                    Text(uiState.activePriorityFilter?.name?.replaceFirstChar { it.titlecase() } ?: "Prioridade")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = filtroPrioridadeAberto,
                    onDismissRequest = { filtroPrioridadeAberto = false }
                ) {
                    DropdownMenuItem(text = { Text("Todas") }, onClick = {
                        viewModel.onPriorityFilterChanged(null)
                        filtroPrioridadeAberto = false
                    })
                    Priority.values().forEach { priority ->
                        DropdownMenuItem(text = { Text(priority.name.replaceFirstChar { it.titlecase() }) }, onClick = {
                            viewModel.onPriorityFilterChanged(priority)
                            filtroPrioridadeAberto = false
                        })
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { filtroStatusAberto = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape
                ) {
                    Text(uiState.activeStatusFilter?.name?.replaceFirstChar { it.titlecase() } ?: "Status")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = filtroStatusAberto,
                    onDismissRequest = { filtroStatusAberto = false }
                ) {
                    DropdownMenuItem(text = { Text("Todos") }, onClick = {
                        viewModel.onStatusFilterChanged(null)
                        filtroStatusAberto = false
                    })
                    Status.values().forEach { status ->
                        DropdownMenuItem(text = { Text(status.name.replaceFirstChar { it.titlecase() }) }, onClick = {
                            viewModel.onStatusFilterChanged(status)
                            filtroStatusAberto = false
                        })
                    }
                }
            }
        }
    }
}


@Composable
fun TaskCard(
    task: Task,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onSubtaskStatusChange: (subtaskId: String, newStatus: Status) -> Unit,
    onEditTask: () -> Unit,
    onEditSubtask: (subtaskId: String) -> Unit,
    onNewSubtask: () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
    val contentColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(cardColor)
            .clickable(onClick = onExpandToggle)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Expandir/Recolher",
                    tint = contentColor.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = task.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )

                if (!isExpanded) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatarPrazo(task.prazoCalculado),
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    task.subtarefas.forEach { subtarefa ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onEditSubtask(subtarefa.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = subtarefa.status == Status.CONCLUIDA,
                                onCheckedChange = { isChecked ->
                                    onSubtaskStatusChange(subtarefa.id, if (isChecked) Status.CONCLUIDA else Status.INICIADA)
                                }
                            )
                            Text(
                                text = subtarefa.titulo,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = if (subtarefa.status == Status.CONCLUIDA) TextDecoration.LineThrough else null
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatarPrazo(subtarefa.prazo),
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subtarefa.duracao,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onEditTask) {
                        Text("Editar Tarefa")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onNewSubtask,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text("Nova subtarefa")
                    }
                }
            }
        }
    }
}
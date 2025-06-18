package com.example.taskapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskapp.ui.login.LoginScreen
import com.example.taskapp.ui.register.RegisterScreen
import com.example.taskapp.ui.subtaskform.SubtaskFormScreen
import com.example.taskapp.ui.taskform.TaskFormScreen
import com.example.taskapp.ui.tasklist.TaskListScreen
import com.example.taskapp.ui.theme.ThemeViewModel

@Composable
fun TaskApp(themeViewModel: ThemeViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("ListaTarefas") {
            TaskListScreen(navController, themeViewModel)
        }

        composable("CadastrarTarefa") {
            // Rota para criar tarefa nova, não passa ID.
            TaskFormScreen(navController = navController, tarefaId = null)
        }

        // ROTA PARA EDITAR TAREFA (AGORA CORRIGIDA)
        composable(
            route = "editarTarefa/{tarefaId}",
            arguments = listOf(navArgument("tarefaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getString("tarefaId")
            // A parte crucial é passar o tarefaId para a tela
            TaskFormScreen(navController = navController, tarefaId = tarefaId)
        }


        // ROTA DE SUBTAREFA UNIFICADA PARA CRIAÇÃO E EDIÇÃO
        composable(
            route = "subtaskForm/{tarefaId}/{subtarefaId}",
            arguments = listOf(
                navArgument("tarefaId") { type = NavType.StringType },
                navArgument("subtarefaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getString("tarefaId")
            val subtarefaIdArgument = backStackEntry.arguments?.getString("subtarefaId")

            if (tarefaId != null && subtarefaIdArgument != null) {
                SubtaskFormScreen(
                    navController = navController,
                    tarefaId = tarefaId,
                    subtarefaId = if (subtarefaIdArgument == "new") null else subtarefaIdArgument
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}
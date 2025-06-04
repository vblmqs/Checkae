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

@Composable
fun TaskApp() {
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
            TaskListScreen(navController)
        }

        composable("CadastrarTarefa") {
            TaskFormScreen(navController = navController)
        }

        // NOVA SUBTAREFA
        composable(
            route = "subtaskForm/new/{tarefaId}",
            arguments = listOf(navArgument("tarefaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getString("tarefaId") ?: ""
            SubtaskFormScreen(
                navController = navController,
                tarefaId = tarefaId,
                subtarefaId = null // null indica modo de criação
            )
        }

        // EDITAR SUBTAREFA
        composable(
            route = "subtaskForm/edit/{tarefaId}/{subtarefaId}", // Nova rota com ambos os IDs
            arguments = listOf(
                navArgument("tarefaId") { type = NavType.StringType },
                navArgument("subtarefaId") { type = NavType.StringType } // subTarefaId também é String
            )
        ) { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getString("tarefaId") ?: ""
            val subtarefaId =
                backStackEntry.arguments?.getString("subtarefaId") // Será não-nulo se a rota for chamada corretamente

            // Verifica se subtarefaId realmente veio (para segurança, embora a rota o exija)
            if (subtarefaId != null && tarefaId.isNotEmpty()) {
                SubtaskFormScreen(
                    navController = navController,
                    tarefaId = tarefaId,
                    subtarefaId = subtarefaId // Passa o ID da subtarefa, indicando modo de edição
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}

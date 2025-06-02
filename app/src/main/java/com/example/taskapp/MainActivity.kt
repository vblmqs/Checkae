package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskapp.ui.subtaskform.SubtaskFormScreen
import com.example.taskapp.ui.tasklist.TaskListScreen
import com.example.taskapp.ui.taskform.TaskFormScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "ListaTarefas") {

                    composable("ListaTarefas") {
                        TaskListScreen(navController)
                    }

                    // Usando SubtaskFormScreen para criar nova subtarefa: só passa tarefaId
                    composable(
                        "subtaskForm/new/{tarefaId}",
                        arguments = listOf(navArgument("tarefaId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tarefaId = backStackEntry.arguments?.getString("tarefaId") ?: ""
                        SubtaskFormScreen(
                            navController = navController,
                            tarefaId = tarefaId,
                            subtarefaId = null,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("CadastrarTarefa") {
                        TaskFormScreen(
                            navController = navController
                            //fazer tarefaId, etc
                        )
                    }
                }
            }
        }
    }
}


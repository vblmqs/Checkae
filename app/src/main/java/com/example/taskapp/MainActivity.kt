package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskapp.ui.tasklist.TaskListScreen
import com.example.taskapp.ui.taskform.TaskFormScreen
import com.example.taskapp.ui.subtaskform.SubtaskFormScreen

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

                    composable("CadastrarTarefa") {
                        TaskFormScreen()
                    }

                    composable("CadastrarSubtarefa/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId")
                        SubtaskFormScreen(taskId = taskId)
                    }

                    composable("EditarTarefa/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId")
                        TaskFormScreen(taskId = taskId)
                    }

                    composable("EditarSubtarefa/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId")
                        SubtaskFormScreen(taskId = taskId)
                    }
                }
            }
        }
    }
}


/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskappTheme {
        Greeting("Android")
    }
} */
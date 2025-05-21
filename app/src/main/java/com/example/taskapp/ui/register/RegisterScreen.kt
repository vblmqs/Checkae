package com.example.taskapp.ui.register

import com.example.taskapp.ui.components.CustomLabeledInput
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskapp.ui.components.CustomLabeledInput

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {

    val context = LocalContext.current

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.success = false
            // navController.navigate("login")
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(360.dp)
                    .wrapContentHeight()
                    .background(
                        color = Color(0xFFD9D9D9).copy(alpha = 0.33f),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cadastre-se",
                        fontSize = 32.sp,
                        color = Color(0xFF243C5B),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomLabeledInput("Nome", viewModel.name) { viewModel.name = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomLabeledInput("E-mail", viewModel.email) { viewModel.email = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomLabeledInput("Senha", viewModel.password, isPassword = true) { viewModel.password = it }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.registerUser()
                            viewModel.errorMessage?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !viewModel.isLoading,
                        modifier = Modifier
                            .width(120.dp)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC1D5E4)
                        )
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                color = Color.Gray,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Cadastrar",
                                color = Color.Black.copy(alpha = 0.68f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Já possui conta?",
                    color = Color(0xFF3F3F3F).copy(alpha = 0.77f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Entrar",
                    color = Color(0xFF3C4F96).copy(alpha = 0.77f),
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}
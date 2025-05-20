package com.example.taskapp.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//melhorar isso
fun cadastrarUsuario(
    nome: String,
    email: String,
    senha: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, senha)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val dados = hashMapOf(
                        "nome" to nome,
                        "email" to email
                    )
                    firestore.collection("usuarios")
                        .document(uid)
                        .set(dados)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError(e.message ?: "Erro ao salvar no Firestore") }
                } else {
                    onError("Erro ao obter UID do usuário.")
                }
            } else {
                onError(task.exception?.message ?: "Erro ao cadastrar usuário.")
            }
        }
}


@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.64).sp,
                        lineHeight = 44.8.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomLabeledInput("Nome", name) { name = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomLabeledInput("E-mail", email) { email = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomLabeledInput("Senha", password, isPassword = true) { password = it }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            cadastrarUsuario(
                                nome = name,
                                email = email,
                                senha = password,
                                onSuccess = {
                                    println("Cadastro realizado com sucesso!")
                                    // navController.navigate("login")
                                },
                                onError = { erro ->
                                    println("Erro ao cadastrar: $erro")
                                }
                            )
                        },
                        modifier = Modifier
                            .width(120.dp)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC1D5E4)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = "Cadastrar",
                            color = Color.Black.copy(alpha = 0.68f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Cadastre-se",
                    color = Color(0xFF3C4F96).copy(alpha = 0.77f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CustomLabeledInput(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black.copy(alpha = 0.68f),
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(285.dp)
                .height(44.dp),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
            ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFC1D5E4).copy(alpha = 0.66f),
                unfocusedContainerColor = Color(0xFFC1D5E4).copy(alpha = 0.66f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 14.sp)
        )
    }
}
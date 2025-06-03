package com.example.taskapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.taskapp.R

@Composable
fun Header(
    titulo: String,
    onVoltar: () -> Unit,
    onClickIconeDireita: (() -> Unit)? = null
) {
    var iconeAtivo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onVoltar) {
                Image(
                    painter = painterResource(id = R.drawable.ic_left),
                    contentDescription = "Voltar"
                )
            }

            IconButton(
                onClick = {
                    iconeAtivo = !iconeAtivo
                    onClickIconeDireita?.invoke()
                }
            ) {
                Icon(
                    imageVector = if (iconeAtivo) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = if (iconeAtivo) Color(0xFF37643A) else Color.Black
                )
            }
        }

        Text(
            text = titulo,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.4.sp,
                letterSpacing = (-0.32).sp
            )
        )
    }
}
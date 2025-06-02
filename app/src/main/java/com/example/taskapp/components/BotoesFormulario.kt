package com.example.taskapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.example.taskapp.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotoesFormulario(
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            border = BorderStroke(1.dp, Color.Transparent),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFFF5D9DA),
                contentColor = Color.Black.copy(alpha = 0.63f)
            )
        ) {
            Text(
                text = "Excluir tarefa",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    color = Color.Black.copy(alpha = 0.63f)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "Excluir",
                modifier = Modifier.size(18.dp),
                tint = Color.Unspecified
            )
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB6F2C2),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Confirmar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp,
                    color = Color.Black
                )
            )
        }
    }
}

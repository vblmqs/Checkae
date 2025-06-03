package com.example.taskapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CampoFormulario(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(360.dp)
            .background(Color(0xFFE1F1E2), shape = RoundedCornerShape(8.dp))
            .padding(start = 16.dp, end = 9.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${label.uppercase()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .width(90.dp)
                    .padding(end = 12.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Color.Black.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 19.6.sp,
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 7.dp)
            )
        }
    }
}
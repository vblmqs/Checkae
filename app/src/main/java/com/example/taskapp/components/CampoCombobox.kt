package com.example.taskapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CampoCombobox(
    label: String,
    options: List<T>,
    selectedOption: T?, // Opção selecionada, pode ser nula se nada estiver selecionado inicialmente
    onOptionSelected: (T) -> Unit,
    optionToDisplayedString: (T) -> String = { it.toString() }, // Como converter a opção para String para exibição
    placeholder: String = "Selecione...", // Texto para mostrar se selectedOption for nulo
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .width(360.dp) // Largura igual ao CampoFormulario
            .background(Color(0xFFE1F1E2), shape = RoundedCornerShape(8.dp))
            // Padding igual ao CampoFormulario para consistência visual externa
            .padding(start = 16.dp, end = 9.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically // Alinha o label e o campo do dropdown
        ) {
            Text(
                text = label.uppercase(), // Label em maiúsculas, como no CampoFormulario
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.width(90.dp) // Largura do label igual ao CampoFormulario
            )

            ExposedDropdownMenuBox(
                expanded = expanded && enabled, // O menu só expande se o campo estiver habilitado
                onExpandedChange = { if (enabled) expanded = !expanded },
                modifier = Modifier.weight(1f) // Faz o dropdown ocupar o espaço restante
            ) {
                // TextField que exibe a opção selecionada e serve como âncora para o menu
                TextField(
                    value = selectedOption?.let { optionToDisplayedString(it) } ?: placeholder,
                    onValueChange = { /* Campo é somente leitura */ },
                    readOnly = true,
                    enabled = enabled,
                    textStyle = TextStyle( // Estilo do texto similar ao BasicTextField do CampoFormulario
                        color = if (selectedOption != null && enabled) Color.Black.copy(alpha = 0.7f)
                        else if (enabled) Color.Black.copy(alpha = 0.4f) // Cor do placeholder
                        else Color.Black.copy(alpha = 0.3f), // Cor quando desabilitado
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 19.6.sp, // Igual ao BasicTextField
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                    ),
                    trailingIcon = {
                        if (enabled) { // Só mostra o ícone se estiver habilitado
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    },
                    colors = TextFieldDefaults.colors( // Remove o fundo e bordas do TextField
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .menuAnchor() // Necessário para o ExposedDropdownMenuBox
                        .fillMaxWidth()
                )

                // O menu dropdown que aparece
                ExposedDropdownMenu(
                    expanded = expanded && enabled,
                    onDismissRequest = { expanded = false }
                ) {
                    if (options.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Nenhuma opção") },
                            onClick = { expanded = false },
                            enabled = false
                        )
                    } else {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(optionToDisplayedString(option)) },
                                onClick = {
                                    onOptionSelected(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
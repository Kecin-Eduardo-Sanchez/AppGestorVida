package com.example.prueba1

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*



data class NotaUI(
    val id: Int,
    val texto: String,
    var completada: Boolean = false,
    var importante: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorNotas(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { NotasDBHelper(context) }

    var contenido by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(db.obtenerNotas().toMutableStateList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Gestor de Notas", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color1)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color5)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color1,
                    unfocusedBorderColor = Color2,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White,
                    disabledLabelColor = Color.White
                )
            )


            Button(
                onClick = {
                    if (contenido.isNotBlank()) {
                        val calendario = Calendar.getInstance()
                        val formatoDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formatoHora = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val diaActual = formatoDia.format(calendario.time)
                        val horaActual = formatoHora.format(calendario.time)
                        val texto = "$diaActual - $horaActual\n$contenido"

                        db.insertarNota(diaActual, horaActual, contenido.trim())
                        notas = db.obtenerNotas().toMutableStateList()
                        contenido = ""
                    } else {
                        Toast.makeText(context, "Llenar campos para registrar Nota", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color2)
            ) {
                Text("Guardar Nota", color = Color.White)
            }

            Divider(color = Color4, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Notas guardadas",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier.weight(1f).padding(top = 12.dp)
            ) {
                itemsIndexed(notas) { _, notaUI ->
                    val backgroundColor = when {
                        notaUI.importante && notaUI.completada -> Color3
                        notaUI.importante -> Color2
                        notaUI.completada -> Color4
                        else -> Color5
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(notaUI.texto, color = Color.White)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        notaUI.completada = !notaUI.completada
                                        db.actualizarEstadoPorId(notaUI.id, notaUI.completada, notaUI.importante)
                                        notas = db.obtenerNotas().toMutableStateList()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color3)
                                ) {
                                    Text(if (notaUI.completada) "Desmarcar" else "Marcar", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        notaUI.importante = !notaUI.importante
                                        db.actualizarEstadoPorId(notaUI.id, notaUI.completada, notaUI.importante)
                                        notas = db.obtenerNotas().toMutableStateList()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color1)
                                ) {
                                    Text(if (notaUI.importante) "Quitar Importante" else "Importante", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        db.eliminarNotaPorId(notaUI.id)
                                        notas = db.obtenerNotas().toMutableStateList()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                                ) {
                                    Text("Borrar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(containerColor = Color2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Volver al inicio", color = Color.White)
            }
        }
    }
}

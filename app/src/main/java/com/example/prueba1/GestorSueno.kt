package com.example.prueba1

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prueba1.data.SuenoDBHelper
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorSueno(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { SuenoDBHelper(context) }

    // Horas y minutos separados
    var horaInicio by remember { mutableStateOf("") }
    var minutoInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var minutoFin by remember { mutableStateOf("") }

    // Fecha
    val calendar = Calendar.getInstance()
    var fecha by remember { mutableStateOf("") }

    // Tipo de sueño (desplegable)
    val opcionesSueno = listOf("Noche", "Siesta", "Madrugada", "Otro")
    var tipoSueno by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Gestor de Sueño")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4527A0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    label = { Text("Hora inicio") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minutoInicio,
                    onValueChange = { minutoInicio = it },
                    label = { Text("Minuto inicio") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { horaFin = it },
                    label = { Text("Hora fin") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minutoFin,
                    onValueChange = { minutoFin = it },
                    label = { Text("Minuto fin") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Menú desplegable para tipo de sueño
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = tipoSueno,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de sueño") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opcionesSueno.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipoSueno = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selector de fecha
            Button(onClick = {
                val datePicker = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        fecha = "$year-${month + 1}-$dayOfMonth"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (fecha.isNotBlank()) "Fecha: $fecha" else "Seleccionar fecha")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (
                        horaInicio.isNotBlank() &&
                        minutoInicio.isNotBlank() &&
                        horaFin.isNotBlank() &&
                        minutoFin.isNotBlank() &&
                        tipoSueno.isNotBlank() &&
                        fecha.isNotBlank()
                    ) {
                        val horaInicioTotal = "${horaInicio.padStart(2, '0')}:${minutoInicio.padStart(2, '0')}"
                        val horaFinTotal = "${horaFin.padStart(2, '0')}:${minutoFin.padStart(2, '0')}"

                        val id = dbHelper.insertarRegistro(horaInicioTotal, horaFinTotal, tipoSueno, fecha)
                        if (id > 0) {
                            Toast.makeText(context, "Registro guardado", Toast.LENGTH_SHORT).show()
                            horaInicio = ""
                            minutoInicio = ""
                            horaFin = ""
                            minutoFin = ""
                            tipoSueno = ""
                            fecha = ""
                        } else {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4527A0),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("historial_sueno") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E24AA),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver historial de sueño")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("home") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}

package com.example.prueba1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gestorgym.data.GymDBHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorGYM(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { GymDBHelper(context) }

    var rutinaSeleccionada by remember { mutableStateOf<Int?>(null) }

    val rutinas = remember { dbHelper.obtenerRutinas() }
    val ejercicios = rutinaSeleccionada?.let { dbHelper.obtenerEjerciciosDeRutina(it) } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas del Gimnasio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text("Selecciona una rutina:", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            // LISTA DE RUTINAS
            LazyColumn {
                items(rutinas) { (id, nombre, dias) ->
                    Button(
                        onClick = { rutinaSeleccionada = id },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("$nombre  |  $dias días")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // EJERCICIOS DE LA RUTINA
            if (rutinaSeleccionada != null) {
                Text("Ejercicios de la rutina seleccionada:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(ejercicios) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Día: ${item["dia"]}")
                                Text("Ejercicio: ${item["nombre"]}")
                                Text("Series: ${item["series"]}, Reps: ${item["reps"]}")
                            }
                        }
                    }
                }
            }
        }
    }
}

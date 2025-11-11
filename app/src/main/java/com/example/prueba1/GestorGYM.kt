package com.example.prueba1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val VitaePurpleLight = Color(0xFF9B6BB3)
    val VitaePurple = Color(0xFF6C3A8C)
    val VitaePurpleDark = Color(0xFF45215C)
    val VitaeNight = Color(0xFF1A0F24)
    val VitaeGold = Color(0xFFFFFFFF)
    val VitaeTextLight = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas del Gimnasio", color = VitaeGold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = VitaeGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitaePurple
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(VitaeNight)
                .padding(16.dp)
        ) {

            Text(
                "Selecciona una rutina:",
                style = MaterialTheme.typography.titleMedium,
                color = VitaeGold
            )

            Spacer(Modifier.height(8.dp))

            // LISTA DE RUTINAS
            LazyColumn {
                items(rutinas) { (id, nombre, dias) ->

                    Button(
                        onClick = { rutinaSeleccionada = id },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VitaePurpleDark,
                            contentColor = VitaeGold
                        )
                    ) {
                        Text("$nombre  |  $dias días")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // EJERCICIOS DE LA RUTINA
            if (rutinaSeleccionada != null) {
                Text(
                    "Ejercicios de la rutina seleccionada:",
                    style = MaterialTheme.typography.titleMedium,
                    color = VitaePurpleLight
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(ejercicios) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = VitaePurpleDark
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Día: ${item["dia"]}", color = VitaeTextLight)
                                Text("Ejercicio: ${item["nombre"]}", color = VitaeGold)
                                Text("Series: ${item["series"]}, Reps: ${item["reps"]}", color = VitaeTextLight)
                            }
                        }
                    }
                }
            }
        }
    }
}


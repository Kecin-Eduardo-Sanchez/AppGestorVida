package com.example.prueba1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialSueno(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { SuenoDBHelper(context) }
    val registros = remember { mutableStateListOf<Map<String, String>>() }
    val resumen = remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    // Cargar datos
    LaunchedEffect(Unit) {
        registros.clear()
        registros.addAll(dbHelper.obtenerTodosLosRegistros())
        resumen.value = dbHelper.obtenerPromedioSemanal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Sueño") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4527A0), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Promedio semanal: ${"%.2f".format(resumen.value["promedio_horas"] ?: 0f)} h",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4527A0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(registros) { registro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Fecha: ${registro["fecha"]}")
                            Text("Inicio: ${registro["hora_inicio"]}")
                            Text("Fin: ${registro["hora_fin"]}")
                            Text("Duración: ${registro["duracion"]} h")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("sueño") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E24AA),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al registro de sueño")
            }
        }
    }
}

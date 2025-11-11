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
import com.example.prueba1.data.SuenoDBHelper

// Colores base morados y púrpuras
private val Fondo = Color(0xFF1A1528)
private val MoradoOscuro = Color(0xFF311B92)
private val MoradoMedio = Color(0xFF7E57C2)
private val MoradoClaro = Color(0xFFD1C4E9)
private val Acento = Color(0xFFE040FB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialSueno(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { SuenoDBHelper(context) }
    val registros = remember { mutableStateListOf<Map<String, String>>() }
    val resumen = remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    LaunchedEffect(Unit) {
        registros.clear()
        registros.addAll(dbHelper.obtenerTodosLosRegistros())
        resumen.value = dbHelper.obtenerPromedioSemanal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Sueño") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoOscuro,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Fondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // Resumen semanal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MoradoOscuro.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Promedio de horas dormidas esta semana",
                        color = MoradoClaro,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${"%.2f".format(resumen.value["promedio_horas"] ?: 0f)} h",
                        color = Acento,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de registros
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(registros) { registro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MoradoMedio.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Fecha: ${registro["fecha"]}", color = MoradoClaro)
                            Text("Inicio: ${registro["hora_inicio"]}", color = Color.White)
                            Text("Fin: ${registro["hora_fin"]}", color = Color.White)
                            Text("Duración: ${registro["duracion"]} h", color = Acento)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { navController.navigate("sueño") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoMedio,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar nuevo sueño")
            }
        }
    }
}

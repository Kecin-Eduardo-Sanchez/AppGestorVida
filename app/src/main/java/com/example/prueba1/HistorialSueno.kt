package com.example.prueba1

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.prueba1.data.SuenoDBHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Calcula duración entre dos horas (considera si pasa medianoche)
fun calcularDuracionHoras(inicio: String, fin: String): Float {
    val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
    val horaInicio = formato.parse(inicio)
    val horaFin = formato.parse(fin)

    val diferencia = if (horaFin.before(horaInicio)) {
        horaFin.time + TimeUnit.DAYS.toMillis(1) - horaInicio.time
    } else {
        horaFin.time - horaInicio.time
    }

    return TimeUnit.MILLISECONDS.toMinutes(diferencia).toFloat() / 60f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialSueno(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { SuenoDBHelper(context) }
    val registros = remember { mutableStateListOf<Map<String, String>>() }

    // Cargar datos al iniciarse
    LaunchedEffect(Unit) {
        registros.clear()
        registros.addAll(
            dbHelper.obtenerTodosLosRegistros().sortedByDescending {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it["fecha"] ?: "")
            }
        )
    }

    val registrosPorFecha = registros.groupBy { it["fecha"] ?: "Sin fecha" }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Historial de Sueño") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Listado detallado sin gráfica
            LazyColumn(modifier = Modifier.weight(1f)) {
                registrosPorFecha.forEach { (fecha, registrosDelDia) ->
                    item {
                        Text(
                            text = " $fecha",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(registrosDelDia) { registro ->
                        val horasDormidas = calcularDuracionHoras(
                            registro["hora_inicio"] ?: "00:00",
                            registro["hora_fin"] ?: "00:00"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Inicio: ${registro["hora_inicio"]}")
                                Text("Fin: ${registro["hora_fin"]}")
                                Text("Tipo: ${registro["tipo"]}")
                                Text("Horas dormidas: ${"%.2f".format(horasDormidas)}")
                            }
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

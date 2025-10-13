package com.example.prueba1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prueba1.data.SuenoDBHelper
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenSueno(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { SuenoDBHelper(context) }
    val decimalFormat = DecimalFormat("#.##")

    val registros = remember { dbHelper.obtenerTodosLosRegistros() }

    // Cálculos semanales
    var totalHoras = 0f
    var dias = mutableSetOf<String>()
    var maxHoras = 0f
    var minHoras = Float.MAX_VALUE
    var diaMax = ""
    var diaMin = ""

    registros.take(7).forEach { registro ->
        val duracion = registro["duracion"]?.toFloatOrNull() ?: 0f
        val fecha = registro["fecha"] ?: "Desconocida"

        totalHoras += duracion
        dias.add(fecha)

        if (duracion > maxHoras) {
            maxHoras = duracion
            diaMax = fecha
        }
        if (duracion < minHoras && duracion > 0) {
            minHoras = duracion
            diaMin = fecha
        }
    }

    val promedio = if (dias.isNotEmpty()) totalHoras / dias.size else 0f
    val mensaje = when {
        promedio >= 8 -> "😴 ¡Excelente! Estás durmiendo muy bien."
        promedio >= 6 -> "Buen trabajo, podrías descansar un poco más."
        promedio >= 4 -> "Necesitas mejorar tus horas de sueño."
        else -> "😵 Muy poco sueño esta semana."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Sueño") },
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
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Promedio semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${decimalFormat.format(promedio)} h por noche",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF8E24AA),
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total dormido en la semana: ${decimalFormat.format(totalHoras)} h")
                    Text("Día con más sueño: $diaMax (${decimalFormat.format(maxHoras)} h)")
                    Text("Día con menos sueño: $diaMin (${decimalFormat.format(minHoras)} h)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("historial_sueno") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E24AA),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver historial completo")
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

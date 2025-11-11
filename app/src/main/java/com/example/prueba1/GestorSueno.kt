package com.example.prueba1

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import java.text.SimpleDateFormat
import java.util.*

private val Fondo = Color(0xFF1A1528)
private val MoradoOscuro = Color(0xFF311B92)
private val MoradoMedio = Color(0xFF7E57C2)
private val MoradoAcento = Color(0xFFE040FB)
private val TextoSuave = Color(0xFFD1C4E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorSueno(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { SuenoDBHelper(context) }
    val prefs = context.getSharedPreferences("sueño_prefs", Context.MODE_PRIVATE)

    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var durmiendo by remember { mutableStateOf(prefs.getBoolean("durmiendo", false)) }
    var horaInicio by remember { mutableStateOf(prefs.getString("hora_inicio", null)) }
    var fechaInicio by remember { mutableStateOf(prefs.getString("fecha_inicio", null)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Sueño") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (!durmiendo) {
                Button(
                    onClick = {
                        durmiendo = true
                        val ahora = Calendar.getInstance()
                        horaInicio = formatoHora.format(ahora.time)
                        fechaInicio = formatoFecha.format(ahora.time)

                        prefs.edit().apply {
                            putBoolean("durmiendo", true)
                            putString("hora_inicio", horaInicio)
                            putString("fecha_inicio", fechaInicio)
                            apply()
                        }
                        Toast.makeText(context, "Sueño iniciado a las $horaInicio", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MoradoMedio),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Iniciar sueño", color = Color.White) }
            } else {
                Button(
                    onClick = {
                        val ahora = Calendar.getInstance()
                        val horaFin = formatoHora.format(ahora.time)
                        val fechaFin = formatoFecha.format(ahora.time)

                        val inicio = formatoHora.parse(horaInicio!!)
                        val fin = formatoHora.parse(horaFin)
                        var duracion = (fin.time - inicio.time) / (1000f * 60f * 60f)
                        if (duracion < 0) duracion += 24f

                        dbHelper.insertarRegistro(horaInicio!!, horaFin, "Noche", fechaInicio ?: fechaFin, duracion)
                        prefs.edit().clear().apply()

                        Toast.makeText(context, "Sueño registrado: ${"%.2f".format(duracion)} h", Toast.LENGTH_SHORT).show()

                        durmiendo = false
                        horaInicio = null
                        fechaInicio = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MoradoAcento),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Finalizar sueño", color = Color.White) }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate("historial_sueno") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoMedio),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ver historial de sueño", color = Color.White) }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("resumen_sueno") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoOscuro),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ver resumen semanal", color = TextoSuave) }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("home") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4458)),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Volver al inicio", color = Color.White) }
        }
    }
}

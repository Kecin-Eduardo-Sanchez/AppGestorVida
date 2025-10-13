package com.example.prueba1

import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.*

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
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4527A0),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar sueño")
                }
            } else {
                Button(
                    onClick = {
                        val ahora = Calendar.getInstance()
                        val horaFin = formatoHora.format(ahora.time)
                        val fechaFin = formatoFecha.format(ahora.time)

                        val inicio = formatoHora.parse(horaInicio!!)
                        val fin = formatoHora.parse(horaFin)
                        var duracion = (fin.time - inicio.time) / (1000f * 60f * 60f)
                        if (duracion < 0) duracion += 24f // si pasó medianoche

                        dbHelper.insertarRegistro(
                            horaInicio!!,
                            horaFin,
                            "Noche",
                            fechaInicio ?: fechaFin,
                            duracion
                        )


                        prefs.edit().clear().apply()

                        Toast.makeText(
                            context,
                            "Sueño registrado: ${"%.2f".format(duracion)} h",
                            Toast.LENGTH_SHORT
                        ).show()

                        durmiendo = false
                        horaInicio = null
                        fechaInicio = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E24AA),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar sueño")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                onClick = { navController.navigate("resumen_sueno") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(

                ),
                modifier = Modifier.fillMaxWidth()
                ) {
                Text("Ver resumen semanal")

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

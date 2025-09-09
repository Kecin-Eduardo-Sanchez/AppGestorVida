package com.example.prueba1

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiempoTareas(navController: NavHostController) {
    var tiempoRestante by remember { mutableStateOf(25 * 60) } // 25 minutos en segundos
    var enDescanso by remember { mutableStateOf(false) }
    var enEjecucion by remember { mutableStateOf(false) }

    // Objetivo de la sesión
    var objetivoTemp by remember { mutableStateOf(TextFieldValue("")) }
    var objetivoFijado by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Efecto para manejar el temporizador
    LaunchedEffect(enEjecucion, tiempoRestante) {
        if (enEjecucion && tiempoRestante > 0) {
            delay(1000L)
            tiempoRestante--
        } else if (enEjecucion && tiempoRestante == 0) {
            enEjecucion = false
            enDescanso = !enDescanso
            tiempoRestante = if (enDescanso) 5 * 60 else 25 * 60

            // 🚨 Vibrar y sonar al cambiar
            vibrar(context)
            sonar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiempo de Tareas") }, // 👈 aquí falta la coma
                navigationIcon = {                   // 👈 botón flecha
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                // Si no hay objetivo fijado -> mostrar TextField
                if (objetivoFijado == null) {
                    OutlinedTextField(
                        value = objetivoTemp,
                        onValueChange = { objetivoTemp = it },
                        label = { Text("Escribe tu objetivo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        if (objetivoTemp.text.isNotBlank()) {
                            objetivoFijado = objetivoTemp.text
                        }
                    }) {
                        Text("Fijar objetivo")
                    }
                } else {
                    // Si ya hay objetivo -> mostrarlo en grande
                    Text(
                        text = objetivoFijado!!,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mostrar si es trabajo o descanso
                Text(if (enDescanso) "Descanso" else "Trabajo", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar tiempo en formato mm:ss
                val minutos = tiempoRestante / 60
                val segundos = tiempoRestante % 60
                Text(
                    String.format("%02d:%02d", minutos, segundos),
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(onClick = { enEjecucion = true }) {
                        Text("Iniciar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        enEjecucion = false
                        enDescanso = false
                        tiempoRestante = 25 * 60
                        objetivoFijado = null // resetear objetivo
                        objetivoTemp = TextFieldValue("")
                    }) {
                        Text("Cancelar")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { navController.navigate("home") }) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}

// 🔔 Función para vibrar compatible con API 24+
fun vibrar(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // API 26+
        val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        // API 24 y 25
        vibrator.vibrate(500) // vibra 500 ms
    }
}

// 🔊 Función para sonar
fun sonar() {
    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 500)
}

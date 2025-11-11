package com.example.prueba1

import androidx.compose.material3.TextFieldDefaults
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

// Paleta púrpura / morados
private val PrimaryPurple = Color(0xFF6A3265)
private val DarkPurple = Color(0xFF4D2146)
private val AccentPurple = Color(0xFF8E24AA)
private val SurfaceLight = Color(0xFFF3E5F5)
private val OnPrimaryText = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiempoTareas(navController: NavHostController) {
    var tiempoRestante by remember { mutableStateOf(25 * 60) }
    var enDescanso by remember { mutableStateOf(false) }
    var enEjecucion by remember { mutableStateOf(false) }

    var objetivoTemp by remember { mutableStateOf(TextFieldValue("")) }
    var objetivoFijado by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(enEjecucion, tiempoRestante) {
        if (enEjecucion && tiempoRestante > 0) {
            delay(1000L)
            tiempoRestante--
        } else if (enEjecucion && tiempoRestante == 0) {
            enEjecucion = false
            enDescanso = !enDescanso
            tiempoRestante = if (enDescanso) 5 * 60 else 25 * 60

            vibrar(context)
            sonar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiempo de Tareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = OnPrimaryText
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = OnPrimaryText)
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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Método Pomodoro", style = MaterialTheme.typography.titleMedium, color = DarkPurple)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Este método ayuda a mantener la concentración y evitar la fatiga. " +
                                    "Se divide en ciclos de estudio y descanso:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkPurple
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "• 25 minutos de concentración total\n• 5 minutos de descanso\n\n" +
                                    "Cada vez que el contador llega a cero, el tiempo cambia automáticamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo del objetivo
                if (objetivoFijado == null) {
                    OutlinedTextField(
                        value = objetivoTemp,
                        onValueChange = { objetivoTemp = it },
                        label = { Text("Escribe tu objetivo de la sesión") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = AccentPurple,
                            unfocusedIndicatorColor = DarkPurple.copy(alpha = 0.4f),
                            cursorColor = AccentPurple,
                            focusedLabelColor = AccentPurple
                        )

                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { if (objetivoTemp.text.isNotBlank()) objetivoFijado = objetivoTemp.text },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple,
                            contentColor = OnPrimaryText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fijar objetivo")
                    }
                } else {
                    Text(
                        text = objetivoFijado!!,
                        style = MaterialTheme.typography.headlineMedium,
                        color = DarkPurple,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    if (enDescanso) "Descanso" else "Trabajo",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryPurple
                )
                Spacer(modifier = Modifier.height(16.dp))

                val minutos = tiempoRestante / 60
                val segundos = tiempoRestante % 60
                Text(
                    String.format("%02d:%02d", minutos, segundos),
                    style = MaterialTheme.typography.headlineLarge,
                    color = DarkPurple
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(
                        onClick = { enEjecucion = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = OnPrimaryText)
                    ) { Text("Iniciar") }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            enEjecucion = false
                            enDescanso = false
                            tiempoRestante = 25 * 60
                            objetivoFijado = null
                            objetivoTemp = TextFieldValue("")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = OnPrimaryText)
                    ) { Text("Cancelar") }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPurple, contentColor = OnPrimaryText),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}



// Vibración
fun vibrar(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    else vibrator.vibrate(500)
}

// Beep
fun sonar() {
    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 500)
}




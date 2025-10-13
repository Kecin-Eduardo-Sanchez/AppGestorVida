package com.example.prueba1

import GestorFinanzas
import android.R.attr.background
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext
import com.example.prueba1.ui.theme.Prueba1Theme
import com.example.prueba1.GestorFinanzas
import com.example.prueba1.GestorSueno
import com.example.prueba1.GestorGYM
import com.example.prueba1.GestorNotas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*

annotation class GestorFinanzas

//Volando o no volar (juan estubo aqui)
val Color1 = Color(0xFF884385) // #884385
val Color2 = Color(0xFF6A3265) // #6a3265
val Color3 = Color(0xFF4D2146) // #4d2146
val Color4 = Color(0xFF2F1126) // #2f1126
val Color5 = Color(0xFF120007) // #120007
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prueba1Theme {
                val navController = rememberNavController()
                val context = LocalContext.current   // 👈 aquí defines el contexto

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { PantallaInicial(navController) }
                    composable("finanzas") { GestorFinanzas(navController) }
                    composable("sueño") { GestorSueno(navController) }
                    composable("gym") { GestorGYM(navController) }
                    composable("notas") { GestorNotas(navController) }

                    composable("historial_sueno") { HistorialSueno(navController) }
                    composable("espejo") { Espejo(navController) }
                    composable("tiempo_tareas") { TiempoTareas(navController) }
                    composable("resumen_sueno") { ResumenSueno(navController) } //lo coloco juan si no funciona ya sabes que es jajajja

                    composable("historial") {
                        HistorialEspejo(navController, dbHelper = EspejoDBHelper(context))
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaInicial(navController: NavHostController) {
    val fondo = Color5
    val opciones = listOf(
        "Finanzas" to "finanzas",
        "Rutinas de Gimnasio" to "gym",
        "Hábitos de Sueño" to "sueño",
        "Tiempo Tareas" to "tiempo_tareas",
        "Espejo" to "espejo",
        "Notas" to "notas"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Organizador de Vida", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color1)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondo)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // ✅ siempre 2 columnas
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(opciones) { (titulo, ruta) ->
                    Button(
                        onClick = { navController.navigate(ruta) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color2,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // ✅ asegura que cada cuadro sea cuadrado
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = titulo,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}




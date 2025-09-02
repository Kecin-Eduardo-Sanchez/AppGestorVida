package com.example.prueba1

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
import com.example.prueba1.ui.theme.Prueba1Theme
import com.example.prueba1.GestorFinanzas
import com.example.prueba1.GestorSueno
import com.example.prueba1.GestorGYM
import com.example.prueba1.GestorNotas


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prueba1Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { PantallaInicial(navController) }
                    composable("finanzas") { GestorFinanzas(navController) }
                    composable("sueño"){GestorSueno(navController)}
                    composable("gym"){GestorGYM(navController)}
                    composable("notas"){GestorNotas(navController)}

                    composable("historial_sueno") { HistorialSueno(navController) }
                }
            }
        }

    }
}

val Color1 = Color(0xFF884385) // #884385
val Color2 = Color(0xFF6A3265) // #6a3265
val Color3 = Color(0xFF4D2146) // #4d2146
val Color4 = Color(0xFF2F1126) // #2f1126
val Color5 = Color(0xFF120007) // #120007

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicial(navController: NavHostController) {
    // Colores personalizados
    val fondo = Color5
    val colorBoton1 = Color2
    val colorBoton2 = Color3
    val colorTexto = Color1

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color1
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondo)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("finanzas") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorBoton1,
                        contentColor = Color.White
                    )
                ) {
                    Text("Finanzas")
                }

                Button(
                    onClick = { navController.navigate("gym") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorBoton2,
                        contentColor = Color.White
                    )
                ) {
                    Text("Rutinas de Gimnasio")
                }

                Button(
                    onClick = { navController.navigate("sueño") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorBoton1,
                        contentColor = Color.White
                    )
                ) {
                    Text("Hábitos de Sueño")
                }

                Button(
                    onClick = { navController.navigate("notas") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorBoton2,
                        contentColor = Color.White
                    )
                ) {
                    Text("Notas")
                }
            }
        }
    }
}



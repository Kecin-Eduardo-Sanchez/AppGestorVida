package com.example.prueba1

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialEspejo(navController: NavHostController, dbHelper: EspejoDBHelper) {
    var fotos by remember { mutableStateOf(emptyList<FotoEspejo>()) }
    var fotoSeleccionada by remember { mutableStateOf<FotoEspejo?>(null) }

    LaunchedEffect(Unit) {
        fotos = dbHelper.obtenerFotos().sortedByDescending { it.fecha }
    }

    val morado = Color(0xFF4527A0)
    val moradoOscuro = Color(0xFF311B92)
    val fondo = Color(0xFF1A1A1A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Espejo", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("espejo") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = morado
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondo),
            contentAlignment = Alignment.Center
        ) {
            if (fotos.isEmpty()) {
                Text(
                    "No hay fotos guardadas",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(fotos) { foto ->
                        val bitmap = BitmapFactory.decodeByteArray(foto.imagen, 0, foto.imagen.size)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto espejo",
                            modifier = Modifier
                                .size(110.dp)
                                .clickable { fotoSeleccionada = foto }
                                .background(moradoOscuro, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }

    fotoSeleccionada?.let { foto ->
        AlertDialog(
            containerColor = fondo,
            onDismissRequest = { fotoSeleccionada = null },
            confirmButton = {
                TextButton(onClick = { fotoSeleccionada = null }) {
                    Text("Cerrar", color = Color.White)
                }
            },
            title = {
                Text(foto.fecha, color = Color(0xFFE1BEE7), fontWeight = FontWeight.Bold)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val bitmap = BitmapFactory.decodeByteArray(foto.imagen, 0, foto.imagen.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.Black, RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(foto.frase, color = Color.White)
                }
            }
        )
    }
}

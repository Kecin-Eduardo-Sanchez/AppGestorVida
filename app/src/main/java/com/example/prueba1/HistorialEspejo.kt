package com.example.prueba1

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialEspejo(navController: NavHostController, dbHelper: EspejoDBHelper) {
    var fotos by remember { mutableStateOf(emptyList<FotoEspejo>()) }
    var fotoSeleccionada by remember { mutableStateOf<FotoEspejo?>(null) }

    LaunchedEffect(Unit) {
        fotos = dbHelper.obtenerFotos().sortedByDescending { it.fecha } // ordenar por fecha
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Espejo") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("espejo") }) {
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
            if (fotos.isEmpty()) {
                Text("No hay fotos guardadas")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // tres columnas pequeñas
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(fotos) { foto ->
                        val bitmap =
                            BitmapFactory.decodeByteArray(foto.imagen, 0, foto.imagen.size)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto espejo",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { fotoSeleccionada = foto }
                        )
                    }
                }
            }
        }
    }

    // Dialog para mostrar la imagen ampliada con detalles
    fotoSeleccionada?.let { foto ->
        AlertDialog(
            onDismissRequest = { fotoSeleccionada = null },
            confirmButton = {
                TextButton(onClick = { fotoSeleccionada = null }) {
                    Text("Cerrar")
                }
            },
            title = { Text(foto.fecha) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val bitmap = BitmapFactory.decodeByteArray(foto.imagen, 0, foto.imagen.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(foto.frase)
                }
            }
        )
    }
}

package com.example.prueba1

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Espejo(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { EspejoDBHelper(context) }

    var frase by remember { mutableStateOf("") }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap = it
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espejo") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver al inicio")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = frase,
                onValueChange = { frase = it },
                label = { Text("Escribe una frase") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch() }) {
                Text("Tomar Foto")
            }

            bitmap?.let { bmp ->
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Foto tomada",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val stream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val imagenBytes = stream.toByteArray()

                    val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                    CoroutineScope(Dispatchers.IO).launch {
                        dbHelper.insertarFoto(fecha, frase, imagenBytes)
                    }
                }) {
                    Text("Guardar en Base de Datos")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navController.navigate("historial") }) {
                Text("Historial de fotos")
            }
        }
    }
}

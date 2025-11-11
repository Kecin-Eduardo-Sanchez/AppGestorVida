package com.example.prueba1

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    val moradoOscuro = Color(0xFF311B92)
    val morado = Color(0xFF4527A0)
    val rosaMorado = Color(0xFFAB47BC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espejo", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver al inicio", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = morado
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = frase,
                onValueChange = { frase = it },
                label = { Text("Escribe una frase", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = rosaMorado,
                    unfocusedBorderColor = morado,
                    cursorColor = rosaMorado
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { launcher.launch() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = morado)
            ) {
                Text("Tomar Foto", color = Color.White)
            }

            bitmap?.let { bmp ->
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Foto tomada",
                    modifier = Modifier
                        .size(240.dp)
                        .padding(6.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val stream = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val imagenBytes = stream.toByteArray()

                        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                        CoroutineScope(Dispatchers.IO).launch {
                            dbHelper.insertarFoto(fecha, frase, imagenBytes)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = rosaMorado)
                ) {
                    Text("Guardar en Base de Datos", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("historial") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = moradoOscuro),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Historial de Fotos", color = Color.White)
            }
        }
    }
}

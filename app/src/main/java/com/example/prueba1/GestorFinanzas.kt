package com.example.prueba1

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.NumberFormat
import java.util.*

// 🎨 Colores
val FinanzasColor1 = Color(0xFF771D76)
val FinanzasColor2 = Color(0xFF923790)
val FinanzasColor3 = Color(0xFFAD51AA)
val FinanzasColor4 = Color(0xFFC76AC3)
val FinanzasColor5 = Color(0xFFE284DD)
val Color6 = Color(0xFF120007) // fondo general

fun formatoPesosColombianos(valor: Float): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    formato.maximumFractionDigits = 0
    return formato.format(valor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorFinanzas(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { FinanzasBDHelper(context) }

    var categorias by remember { mutableStateOf(listOf<String>()) }
    var saldos by remember { mutableStateOf(mapOf<String, Float>()) }
    var categoriaSeleccionada by remember { mutableStateOf("") }

    var textoMonto by remember { mutableStateOf("") }
    var montoNumerico by remember { mutableStateOf<Float?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var mostrarDialogoModificar by remember { mutableStateOf(false) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf(TextFieldValue("")) }
    var nuevaCategoria by remember { mutableStateOf(TextFieldValue("")) }

    fun actualizarDatos() {
        val dbRead = db.readableDatabase
        val cursor = dbRead.rawQuery("SELECT nombre, saldo FROM categorias", null)
        val nuevaLista = mutableListOf<String>()
        val nuevoMapa = mutableMapOf<String, Float>()

        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val saldo = cursor.getFloat(1)
            nuevaLista.add(nombre)
            nuevoMapa[nombre] = saldo
        }
        cursor.close()
        categorias = nuevaLista
        saldos = nuevoMapa
        if (categorias.isNotEmpty() && categoriaSeleccionada.isEmpty()) {
            categoriaSeleccionada = categorias.first()
        }
    }

    LaunchedEffect(Unit) { actualizarDatos() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor Finanzas") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FinanzasColor1),
                navigationIcon = {
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
                .fillMaxSize()
                .background(Color6)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // --- Resumen ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = FinanzasColor4),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Resumen Actual", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Divider(color = Color.White.copy(alpha = 0.4f))

                        saldos.forEach { (categoria, saldo) ->
                            Text("$categoria: ${formatoPesosColombianos(saldo)}", color = Color.White)
                        }
                    }
                }

                // --- Formulario ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = FinanzasColor3),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Agregar Movimiento", color = Color.White)

                        // Selección categoría
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Categoría:", color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Box {
                                Button(onClick = { expanded = true }) {
                                    Text(categoriaSeleccionada)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    categorias.forEach {
                                        DropdownMenuItem(
                                            text = { Text(it) },
                                            onClick = {
                                                categoriaSeleccionada = it
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Campo monto
                        OutlinedTextField(
                            value = textoMonto,
                            onValueChange = {
                                val limpio = it.replace(Regex("[^\\d]"), "")
                                val valor = limpio.toFloatOrNull()
                                montoNumerico = valor
                                textoMonto = if (valor != null) formatoPesosColombianos(valor) else ""
                            },
                            label = { Text("Monto en COP") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        // Campo descripción
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        // Botones
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (montoNumerico != null && categoriaSeleccionada.isNotEmpty()) {
                                        db.agregarMovimiento(categoriaSeleccionada, "ingreso", montoNumerico!!, descripcion)
                                        actualizarDatos()
                                        textoMonto = ""
                                        descripcion = ""
                                    } else {
                                        Toast.makeText(context, "Monto inválido", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(FinanzasColor2)
                            ) {
                                Text("Ingresar")
                            }

                            Button(
                                onClick = {
                                    if (montoNumerico != null && categoriaSeleccionada.isNotEmpty()) {
                                        db.agregarMovimiento(categoriaSeleccionada, "gasto", montoNumerico!!, descripcion)
                                        actualizarDatos()
                                        textoMonto = ""
                                        descripcion = ""
                                    } else {
                                        Toast.makeText(context, "Monto inválido", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(FinanzasColor1)
                            ) {
                                Text("Gastar")
                            }
                        }

                        // Botones de categoría
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { mostrarDialogoModificar = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FinanzasColor3, contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Modificar Categoría")
                            }
                            Button(
                                onClick = { mostrarDialogoAgregar = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FinanzasColor4, contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Agregar Categoría")
                            }
                        }
                    }
                }

                // Botón volver
                Button(
                    onClick = { navController.navigate("home") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FinanzasColor1, contentColor = Color.White)
                ) {
                    Text("Volver al inicio")
                }
            }
        }
    }

    // --- Diálogo modificar categoría ---
    if (mostrarDialogoModificar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoModificar = false },
            title = { Text("Modificar Categoría") },
            text = {
                Column {
                    Text("Nuevo nombre para: $categoriaSeleccionada")
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nuevo nombre") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val idCat = db.obtenerIdCategoria(categoriaSeleccionada)
                    val exito = db.modificarCategoria(idCat, nuevoNombre.text.trim())
                    if (exito) {
                        Toast.makeText(context, "Categoría modificada", Toast.LENGTH_SHORT).show()
                        actualizarDatos()
                    } else {
                        Toast.makeText(context, "No se puede modificar esta categoría", Toast.LENGTH_SHORT).show()
                    }
                    mostrarDialogoModificar = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogoModificar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- Diálogo agregar categoría ---
    if (mostrarDialogoAgregar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAgregar = false },
            title = { Text("Nueva Categoría") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevaCategoria,
                        onValueChange = { nuevaCategoria = it },
                        label = { Text("Nombre de la categoría") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val nombre = nuevaCategoria.text.trim()
                    if (nombre.isNotEmpty()) {
                        if (db.agregarCategoria(nombre)) {
                            Toast.makeText(context, "Categoría agregada", Toast.LENGTH_SHORT).show()
                            actualizarDatos()
                        } else {
                            Toast.makeText(context, "Error: ya existe", Toast.LENGTH_SHORT).show()
                        }
                    }
                    mostrarDialogoAgregar = false
                    nuevaCategoria = TextFieldValue("")
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogoAgregar = false
                    nuevaCategoria = TextFieldValue("")
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

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
import com.example.prueba1.FinanzasBDHelper
import java.text.NumberFormat
import java.util.*

// --- Tus colores y función de formato (sin cambios) ---
val FinanzasColor1 = Color(0xFF771D76)
val FinanzasColor2 = Color(0xFF923790)
val FinanzasColor3 = Color(0xFFAD51AA)
val FinanzasColor4 = Color(0xFFC76AC3)
val FinanzasColor5 = Color(0xFFE284DD)
val Color6 = Color(0xFF120007)

fun formatoPesosColombianos(valor: Float): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    formato.maximumFractionDigits = 0
    return formato.format(valor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorFinanzas(navController: NavHostController) {
    val context = LocalContext.current
    // --- CORRECCIÓN 1: Usar el patrón Singleton ---
    // Obtenemos la instancia única del helper en lugar de crear una nueva.
    val db = remember { FinanzasBDHelper.getInstance(context) }

    // El estado se mantiene igual
    var categorias by remember { mutableStateOf(listOf<String>()) }
    var saldos by remember { mutableStateOf(mapOf<String, Float>()) }
    var categoriaSeleccionada by remember { mutableStateOf("") }

    // ... el resto de tus variables de estado se mantienen igual
    var textoMonto by remember { mutableStateOf("") }
    var montoNumerico by remember { mutableStateOf<Float?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var mostrarDialogoModificar by remember { mutableStateOf(false) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf(TextFieldValue("")) }
    var nuevaCategoria by remember { mutableStateOf(TextFieldValue("")) }

    // --- CORRECCIÓN 2: Simplificar la carga de datos ---
    // La UI no debe interactuar directamente con la base de datos.
    // Usamos el método que ya creamos en el Helper.
    fun actualizarDatos() {
        val datos = db.obtenerCategoriasConSaldos() // Llama al método del helper
        categorias = datos.map { it.first }
        saldos = datos.toMap()

        // Lógica para asegurar que siempre haya una categoría seleccionada si la lista no está vacía
        if (categorias.isNotEmpty() && (categoriaSeleccionada.isEmpty() || !categorias.contains(
                categoriaSeleccionada
            ))
        ) {
            categoriaSeleccionada = categorias.first()
        }
    }

    // LaunchedEffect se mantiene, carga los datos al iniciar
    LaunchedEffect(Unit) {
        actualizarDatos()
    }

    // El resto de tu UI (Scaffold, Cards, etc.) es mayormente igual.
    // Los cambios están en los `onClick` de los botones para asegurar que todo funcione
    // con la nueva lógica.

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
        // --- El resto del código de la UI es idéntico al tuyo ---
        // Lo incluyo para que sea un bloque completo y funcional.
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
                // ... Tu Card de Resumen (sin cambios)
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
                        Text(
                            "Resumen Actual",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Divider(color = Color.White.copy(alpha = 0.4f))

                        // Usamos los datos ordenados que vienen del helper
                        saldos.entries.toList().forEach { (categoria, saldo) ->
                            Text(
                                "$categoria: ${formatoPesosColombianos(saldo)}",
                                color = Color.White
                            )
                        }
                    }
                }


                // ... Tu Card de Formulario (sin cambios en la apariencia)
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

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Categoría:", color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Box {
                                Button(onClick = { expanded = true }) {
                                    Text(if (categoriaSeleccionada.isNotEmpty()) categoriaSeleccionada else "Seleccionar")
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
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

                        OutlinedTextField(
                            value = textoMonto,
                            onValueChange = {
                                val limpio = it.replace(Regex("[^\\d]"), "")
                                val valor = limpio.toFloatOrNull()
                                montoNumerico = valor
                                textoMonto =
                                    if (valor != null) formatoPesosColombianos(valor) else ""
                            },
                            label = { Text("Monto en COP") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

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

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (montoNumerico != null && montoNumerico!! > 0 && categoriaSeleccionada.isNotEmpty()) {
                                        db.agregarMovimiento(
                                            categoriaSeleccionada,
                                            "ingreso",
                                            montoNumerico!!,
                                            descripcion
                                        )
                                        actualizarDatos()
                                        textoMonto = ""
                                        montoNumerico = null
                                        descripcion = ""
                                        Toast.makeText(
                                            context,
                                            "Ingreso registrado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Monto o categoría inválidos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(FinanzasColor2)
                            ) {
                                Text("Ingresar")
                            }

                            Button(
                                onClick = {
                                    if (montoNumerico != null && montoNumerico!! > 0 && categoriaSeleccionada.isNotEmpty()) {
                                        db.agregarMovimiento(
                                            categoriaSeleccionada,
                                            "gasto",
                                            montoNumerico!!,
                                            descripcion
                                        )
                                        actualizarDatos()
                                        textoMonto = ""
                                        montoNumerico = null
                                        descripcion = ""
                                        Toast.makeText(
                                            context,
                                            "Gasto registrado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Monto o categoría inválidos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(FinanzasColor1)
                            ) {
                                Text("Gastar")
                            }
                        }

                        // Botones de categoría (sin cambios)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { mostrarDialogoModificar = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FinanzasColor3,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Modificar Categoría")
                            }
                            Button(
                                onClick = { mostrarDialogoAgregar = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FinanzasColor4,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Agregar Categoría")
                            }
                        }
                    }
                }

                // ... Tu botón de volver (sin cambios)
                Button(
                    onClick = { navController.navigate("home") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinanzasColor1,
                        contentColor = Color.White
                    )
                ) {
                    Text("Volver al inicio")
                }
            }
        }
    }

    // --- CORRECCIÓN 3: Asegurar que el diálogo de modificar usa el método público del helper ---
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
// En GestorFinanzas.kt, dentro de `if (mostrarDialogoModificar)`

            confirmButton = {
                Button(onClick = {
                    // --- CÓDIGO CORREGIDO ---
                    // Ahora llamamos a la versión pública que devuelve Long.
                    // Ya no es necesario (y es incorrecto) convertir a Int.
                    val idCat = db.obtenerIdCategoria(categoriaSeleccionada)

                    // `modificarCategoria` ahora espera un Long, así que todo coincide.
                    if (idCat > 0) {
                        val exito = db.modificarCategoria(idCat, nuevoNombre.text.trim())
                        if (exito) {
                            Toast.makeText(context, "Categoría modificada", Toast.LENGTH_SHORT)
                                .show()
                            actualizarDatos()
                        } else {
                            Toast.makeText(
                                context,
                                "No se puede modificar. Nombre duplicado o categoría protegida.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error al encontrar la categoría.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    mostrarDialogoModificar = false
                    nuevoNombre = TextFieldValue("")
                }) { Text("Guardar") }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogoModificar = false
                }) { Text("Cancelar") }
            }
        )
    }

    // --- Diálogo agregar categoría (sin cambios de lógica) ---
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
                            Toast.makeText(
                                context,
                                "Error: la categoría ya existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    mostrarDialogoAgregar = false
                    nuevaCategoria = TextFieldValue("")
                }) { Text("Guardar") }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogoAgregar = false
                    nuevaCategoria = TextFieldValue("")
                }) { Text("Cancelar") }
            }
        )
    }
}
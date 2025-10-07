package com.example.prueba1

import FinanzasColor1
import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gestorgym.data.GymDBHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorGYM(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = remember { GymDBHelper(context) }

    var selectedTab by remember { mutableStateOf(0) } // 0=Buscar, 1=Rutina, 2=Ejercicio, 3=Detalle

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor Gym") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FinanzasColor1),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Pestañas
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Buscar") }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Rutinas") }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) { Text("Ejercicios") }
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) { Text("Detalles") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> BuscarRutinaUI(dbHelper)
                1 -> AgregarRutinaUI(dbHelper)
                2 -> AgregarEjercicioUI(dbHelper)
                3 -> AgregarDetalleRutinaUI(dbHelper)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("home") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E24AA),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}

@Composable
fun AgregarDetalleRutinaUI(x0: GymDBHelper) {
    TODO("Not yet implemented")
}

@Composable
fun AgregarEjercicioUI(x0: GymDBHelper) {
    TODO("Not yet implemented")
}

@Composable
fun AgregarRutinaUI(x0: GymDBHelper) {
    TODO("Not yet implemented")
}

/* 🔹 Función auxiliar para obtener listado automático */
fun obtenerListadoRutinas(db: SQLiteDatabase): List<Pair<Int, String>> {
    val listado = mutableListOf<Pair<Int, String>>()
    val cursor = db.rawQuery("SELECT id_rutina, nombre FROM rutinas", null)

    if (cursor.moveToFirst()) {
        do {
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            listado.add(Pair(id, nombre))
        } while (cursor.moveToNext())
    }
    cursor.close()
    return listado
}

/* 🔹 Pantalla Buscar con listado + búsqueda por ID */
@Composable
fun BuscarRutinaUI(dbHelper: GymDBHelper) {
    var idBusqueda by remember { mutableStateOf("") }
    var resultadoRutina by remember { mutableStateOf<Map<String, Any>?>(null) }
    var detallesRutina by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var mensajeError by remember { mutableStateOf("") }

    // Estado del listado automático
    var listadoRutinas by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val db = dbHelper.readableDatabase
        listadoRutinas = obtenerListadoRutinas(db)
        db.close()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("📋 Rutinas registradas", style = MaterialTheme.typography.titleLarge)

        if (listadoRutinas.isEmpty()) {
            Text("No hay rutinas registradas")
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(listadoRutinas) { (id, nombre) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                    ) {
                        Text(
                            "[$id] $nombre",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = idBusqueda,
            onValueChange = { idBusqueda = it },
            label = { Text("ID de la rutina a buscar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (idBusqueda.isNotEmpty()) {
                val rutina = dbHelper.obtenerRutinaPorId(idBusqueda.toInt())
                if (rutina != null) {
                    resultadoRutina = rutina
                    detallesRutina = dbHelper.obtenerRutinaDetalladaPorId(idBusqueda.toInt())
                    mensajeError = ""
                } else {
                    resultadoRutina = null
                    detallesRutina = emptyList()
                    mensajeError = "No se encontró rutina con ID $idBusqueda"
                }
            }
        }) {
            Text("🔍 Buscar rutina")
        }

        Spacer(modifier = Modifier.height(16.dp))

        resultadoRutina?.let { rutina ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📌 ID: ${rutina["id_rutina"]}")
                    Text("🏋 Nombre: ${rutina["nombre"]}")
                    Text("📅 Días: ${rutina["dias"]}")
                    Text("📝 Descripción: ${rutina["descripcion"]}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("📖 Rutina Detallada", style = MaterialTheme.typography.titleMedium)

            val ejerciciosPorDia = detallesRutina.groupBy { it["dia"] as Int }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .weight(1f, false)
            ) {
                ejerciciosPorDia.forEach { (dia, ejercicios) ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📅 Día $dia", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                ejercicios.forEach { detalle ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF))
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("🏋 Ejercicio: ${detalle["ejercicio"]}")
                                            Text("🔁 ${detalle["series"]} x ${detalle["repeticiones"]}")
                                            Text("⚖ Peso: ${detalle["peso_usado"]} kg")
                                            Text("⏱ Descanso: ${detalle["descanso_segundos"]} seg")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mensajeError.isNotEmpty()) {
            Text(mensajeError, color = Color.Red)
        }
    }
}

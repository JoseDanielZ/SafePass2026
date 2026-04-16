package com.tuempresa.safepass.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tuempresa.safepass.logic.validarAsistente
import com.tuempresa.safepass.state.RegistroState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    // Guardamos lo que el usuario escribe en cada campo
    var nombre      by remember { mutableStateOf("") }
    var edad        by remember { mutableStateOf("") }
    var tipoEntrada by remember { mutableStateOf("") }

    // Estado de la pantalla: empieza en Idle
    var estado by remember { mutableStateOf<RegistroState>(RegistroState.Idle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SafePass 2026") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Campo: Nombre
            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text("Nombre") },
                modifier      = Modifier.fillMaxWidth()
            )

            // Campo: Edad
            OutlinedTextField(
                value           = edad,
                onValueChange   = { edad = it },
                label           = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier.fillMaxWidth()
            )

            // Campo: Tipo de entrada
            OutlinedTextField(
                value         = tipoEntrada,
                onValueChange = { tipoEntrada = it },
                label         = { Text("Tipo de entrada (General / VIP / Staff)") },
                modifier      = Modifier.fillMaxWidth()
            )

            // Botón Registrar
            Button(
                onClick = {
                    // toIntOrNull() devuelve null si el texto no es un número válido
                    // Así evitamos que la app se cierre con un crash
                    val edadNumero: Int? = edad.trim().toIntOrNull()

                    // Llamamos a la función de validación con los tres datos
                    estado = validarAsistente(
                        nombre      = nombre,
                        edad        = edadNumero,
                        tipoEntrada = tipoEntrada
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }

            // Mostramos algo distinto dependiendo del estado actual
            when (val s = estado) {

                // Pantalla inicial: solo un mensaje de ayuda
                is RegistroState.Idle -> {
                    Text("Ingrese los datos del asistente y presione Registrar.")
                }

                // Registro exitoso: mostramos los datos con plantillas ${}
                is RegistroState.Success -> {
                    Text("Registro exitoso", fontWeight = FontWeight.Bold)
                    Text("Nombre: ${s.asistente.nombre}")
                    Text("Edad: ${s.asistente.edad ?: "No especificada"}")
                    Text("Tipo de entrada: ${s.asistente.tipoEntrada}")
                }

                // Error: mostramos el mensaje del problema
                is RegistroState.Error -> {
                    Text("Error: ${s.mensaje}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
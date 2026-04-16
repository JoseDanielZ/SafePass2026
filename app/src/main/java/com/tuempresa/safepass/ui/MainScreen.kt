package com.tuempresa.safepass.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.safepass.logic.procesarAsistente
import com.tuempresa.safepass.state.RegistroState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    // ─── Estado de los campos de texto ───────────────────────────────────────
    var nombreInput    by remember { mutableStateOf("") }
    var edadInput      by remember { mutableStateOf("") }
    var tipoEntradaInput by remember { mutableStateOf("") }

    // ─── Estado de la UI (Idle por defecto) ──────────────────────────────────
    var registroState: RegistroState by remember { mutableStateOf(RegistroState.Idle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SafePass 2026 — TechEvent",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement   = Arrangement.spacedBy(16.dp),
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            // ─── Formulario ──────────────────────────────────────────────────
            Text(
                text  = "Registro de Asistente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            // Campo: Nombre
            OutlinedTextField(
                value         = nombreInput,
                onValueChange = { nombreInput = it },
                label         = { Text("Nombre completo") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            // Campo: Edad — usa toIntOrNull() + Elvis ?: para nunca crashear
            OutlinedTextField(
                value         = edadInput,
                onValueChange = { edadInput = it },
                label         = { Text("Edad") },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier      = Modifier.fillMaxWidth()
            )

            // Campo: Tipo de Entrada
            OutlinedTextField(
                value         = tipoEntradaInput,
                onValueChange = { tipoEntradaInput = it },
                label         = { Text("Tipo de entrada (General / VIP / Staff)") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            // ─── Botón Registrar ─────────────────────────────────────────────
            Button(
                onClick = {
                    // toIntOrNull() + Elvis: si la edad no es número válido → null
                    val edadParseada: Int? = edadInput.trim().toIntOrNull()

                    // procesarAsistente() es la Higher-Order Function de Kathe;
                    // recibe los inputs y una lambda de validación de prioridad.
                    registroState = procesarAsistente(
                        nombreRaw      = nombreInput.trim(),
                        edadRaw        = edadParseada,
                        tipoEntradaRaw = tipoEntradaInput.trim()
                    ) { asistente ->
                        // Lambda de validación de prioridad:
                        // VIP tienen acceso prioritario sin restricción adicional.
                        asistente.tipoEntrada.equals("VIP", ignoreCase = true)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = "Registrar", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ─── when exhaustivo sobre RegistroState ─────────────────────────
            when (val state = registroState) {

                // Estado inicial: no muestra nada extra
                is RegistroState.Idle -> {
                    Text(
                        text  = "Complete los campos y presione Registrar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Estado éxito: muestra resumen con plantillas de cadena ${}
                is RegistroState.Success -> {
                    val asistente = state.asistente
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text       = "✅ Registro Exitoso",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 18.sp,
                                color      = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Divider()
                            Text(text = "👤 Nombre      : ${asistente.nombre}")
                            Text(text = "🎂 Edad        : ${asistente.edad ?: "No especificada"}")
                            Text(text = "🎟️ Tipo entrada: ${asistente.tipoEntrada}")
                        }
                    }
                }

                // Estado error: muestra el mensaje descriptivo
                is RegistroState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text       = "❌ Error en el registro",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 18.sp,
                                color      = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text  = state.mensaje,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
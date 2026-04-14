package com.tuempresa.safepass.state

import com.tuempresa.safepass.model.Asistente

/**
 * Sealed class que representa los posibles estados del registro de un asistente.
 * Al ser sellada, garantiza que solo existan estos tres estados,
 * permitiendo un manejo con 'when' en la UI.
 */
sealed class RegistroState {

    /**
     * Estado inicial: la app está esperando que el usuario ingrese datos.
     */
    object Idle : RegistroState()

    /**
     * Estado de éxito: los datos del asistente fueron validados correctamente.
     * Contiene el objeto Asistente con la información procesada.
     */
    data class Success(val asistente: Asistente) : RegistroState()

    /**
     * Estado de error: los datos ingresados son inválidos o están incompletos.
     * Contiene un mensaje descriptivo del problema encontrado.
     */
    data class Error(val mensaje: String) : RegistroState()
}
package com.tuempresa.safepass.logic

import com.tuempresa.safepass.model.Asistente
import com.tuempresa.safepass.state.RegistroState

fun Int.esMayorDeEdad(): Boolean = this >= 18

fun procesarAsistente(
    asistente: Asistente,
    validacionExtra: (Asistente) -> Boolean
): Boolean = validacionExtra(asistente)

fun validarAsistente(
    nombre: String?,
    edad: Int?,
    tipoEntrada: String?
): RegistroState {

    return nombre?.trim()?.let { nombreSeguro ->

        if (nombreSeguro.isBlank()) {
            RegistroState.Error("El nombre no puede estar vacío")

        } else if (edad == null) {
            RegistroState.Error("La edad es inválida o está vacía")

        } else if (!edad.esMayorDeEdad()) {
            RegistroState.Error("El asistente es menor de edad")

        } else {

            val asistenteBase = Asistente(
                nombre = "",
                edad = null,
                tipoEntrada = ""
            )

            val asistente = asistenteBase
                .copy(
                    nombre = nombreSeguro,
                    edad = edad,
                    tipoEntrada = tipoEntrada ?: "General"
                )
                .apply {

                }

            val valido = procesarAsistente(asistente) {
                it.tipoEntrada.isNotBlank()
            }

            if (valido) {
                RegistroState.Success(asistente)
            } else {
                RegistroState.Error("Tipo de entrada inválido")
            }
        }

    } ?: RegistroState.Error("Faltan datos del asistente")
}
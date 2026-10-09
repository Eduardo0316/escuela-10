package com.eduardo.escuela.enums;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.utils.StringCustomUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DiaSemana {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado"),
    DOMINGO("Domingo");

    private final String descripcion;

    public static DiaSemana obtenerPorDescripcion(String descripcion) {
        StringCustomUtils.validarNoVacio(descripcion, "El día es requerido");

        String normalizada = StringCustomUtils.normalizarTexto(descripcion.trim());

        for (DiaSemana dia : values()) {
            if (StringCustomUtils.normalizarTexto(dia.descripcion).equalsIgnoreCase(normalizada))
                return dia;
        }

        throw new DatoInvalidoException("No existe un día con la descripción: " + descripcion);
    }
}
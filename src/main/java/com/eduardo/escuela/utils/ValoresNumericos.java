package com.eduardo.escuela.utils;
import java.math.BigDecimal;
import com.eduardo.escuela.exceptions.DatoInvalidoException;

public class ValoresNumericos {
    public static <N> void validarNumeroRequerido(N numero, String mensaje){
        if(numero == null)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarEnteroPositivo(Integer numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if (numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarBigDecimalPositivo(BigDecimal numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if(numero.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException("El decimal debe ser mayor a cero");
    }
}
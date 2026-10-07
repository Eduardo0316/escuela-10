package com.eduardo.escuela.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter 
public class LocalTimeStringConverter implements AttributeConverter<LocalTime, String> {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String convertToDatabaseColumn(LocalTime hora) {
        return hora == null ? null : hora.format(FORMATO);
    }

    @Override
    public LocalTime convertToEntityAttribute(String texto) {
        return texto == null ? null : LocalTime.parse(texto, FORMATO);
    }
}
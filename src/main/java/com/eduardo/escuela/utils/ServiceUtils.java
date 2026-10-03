package com.eduardo.escuela.utils;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class ServiceUtils {
    public static <E, ID>E obtenerEntidadOException(
        JpaRepository<E, ID> repository,
        ID id,
        Class<E> clase
    ){
        String nombreEntidad = clase.getSimpleName();
        log.info("Buscando {} con id: {}", nombreEntidad, id);

        return repository.findById(id).orElseThrow(() ->
            new RecursoNoEncontradoException(nombreEntidad + " no encontrado con id: " + id)
        );
    }
}

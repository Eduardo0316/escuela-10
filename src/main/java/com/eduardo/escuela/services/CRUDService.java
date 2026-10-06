package com.eduardo.escuela.services;

import java.util.List;

public interface CRUDService<RQ, RS>{

    List<RS> listar();

    RS obtenerPorId(Long id);

    RS registrar(RQ request);

    RS actualizar(RQ request, Long id);

    void eliminar(Long id);
}
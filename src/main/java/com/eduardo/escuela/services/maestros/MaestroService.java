package com.eduardo.escuela.services.maestros;

import java.util.List;

import com.eduardo.escuela.dto.datos.DatosCurso;
import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.services.CRUDService;

public interface MaestroService extends CRUDService<MaestroRequest, MaestroResponse> {
    // List<DatosCurso> obtenerCursosDeUnMaestroConId(Long id);
}
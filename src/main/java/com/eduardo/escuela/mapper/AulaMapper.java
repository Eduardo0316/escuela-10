package com.eduardo.escuela.mapper;

import com.eduardo.escuela.dto.aulas.AulaRequest;
import com.eduardo.escuela.dto.aulas.AulaResponse;
import com.eduardo.escuela.entities.Aula;

public class AulaMapper implements CommonMapper<AulaRequest, AulaResponse, Aula>{

    @Override
    public AulaResponse entidadAResponse(Aula entidad) {
        return new AulaResponse(
            entidad.getId(), 
            entidad.getNombre(), 
            entidad.getCapacidad());
    }

    @Override
    public Aula requestAEntidad(AulaRequest request) {
        return Aula.crear(
            request.nombre(), 
            request.capacidad());
    }
    
}

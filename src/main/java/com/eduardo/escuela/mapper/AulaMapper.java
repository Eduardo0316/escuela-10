package com.eduardo.escuela.mapper;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.aulas.AulaRequest;
import com.eduardo.escuela.dto.aulas.AulaResponse;
import com.eduardo.escuela.dto.datos.DatosAula;
import com.eduardo.escuela.entities.Aula;

@Component 
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
    
    public DatosAula entidadADatosAula(Aula entidad){
        return entidad == null
            ? null
            : new DatosAula(
                entidad.getNombre(), 
                entidad.getCapacidad());
    }
}

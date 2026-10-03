package com.eduardo.escuela.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosCurso;
import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Maestro;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class MaestroMapper implements CommonMapper<MaestroRequest, MaestroResponse, Maestro>{
    private final CursoMapper cursoMapper;
    @Override
    public Maestro requestAEntidad(MaestroRequest request) {
        return request == null
        ? null
            : Maestro.crear(
                request.nombre(), 
                request.apellidoPaterno(), 
                request.apellidoMaterno(), 
                request.email(), 
                request.telefono());
    }
    
    @Override
    public MaestroResponse entidadAResponse(Maestro entidad) {
        return entidad == null
        ? null
        : new MaestroResponse(
            entidad.getId(), 
            String.join(" ", 
                entidad.getNombre(), 
                entidad.getApellidoPaterno(), 
                entidad.getApellidoMaterno()),
            entidad.getEmail(), 
            entidad.getTelefono(),
            entidadADatosCurso(entidad));
    }

    private List<DatosCurso> entidadADatosCurso(Maestro entidad){
        return entidad == null
            ? List.of()
            : entidad.getGrupos().stream()
                .map(Grupo::getCurso)
                .map(cursoMapper::entidadADatosCurso)
                .toList();
    }
}

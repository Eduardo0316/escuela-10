package com.eduardo.escuela.mapper;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.cursos.CursoRequest;
import com.eduardo.escuela.dto.cursos.CursoResponse;
import com.eduardo.escuela.dto.datos.DatosCurso;
import com.eduardo.escuela.entities.Curso;

@Component 
public class CursoMapper implements CommonMapper<CursoRequest, CursoResponse, Curso>{

    @Override
    public CursoResponse entidadAResponse(Curso entidad) {
        return new CursoResponse(
            entidad.getId(), 
            entidad.getNombre().trim(), 
            entidad.getDescripcion(), 
            entidad.getCreditos()
        );
    }

    @Override
    public Curso requestAEntidad(CursoRequest request) {
        return Curso.builder()
            .nombre(request.nombre())
            .descripcion(request.descripcion())
            .creditos(request.creditos())
            .build();
    }
    
    public DatosCurso entidadADatosCurso(Curso entidad){
        return entidad == null
            ? null
            : new DatosCurso(
                entidad.getNombre(), 
                entidad.getDescripcion(), 
                entidad.getCreditos());
    }
}

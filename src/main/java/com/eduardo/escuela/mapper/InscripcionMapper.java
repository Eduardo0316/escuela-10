package com.eduardo.escuela.mapper;


import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosAlumno;
import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.inscripciones.InscripcionRequest;
import com.eduardo.escuela.dto.inscripciones.InscripcionResponse;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Inscripcion;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class InscripcionMapper{
    private final AlumnoMapper alumnoMapper;
    private final GrupoMapper grupoMapper;
    public InscripcionResponse entidadAResponse(Inscripcion entidad) {
        return entidad == null
            ? null
            : new InscripcionResponse(
                entidad.getId(), 
                alumnoMapper.entidadADatosAlumno(entidad.getAlumno()), 
                grupoMapper.entidadADatosGrupo(entidad.getGrupo()), 
                entidad.getCalificacion() != null ? entidad.getCalificacion().getCalificacion() : null, 
                entidad.getFechaInscripcion().toString());
    }

    public Inscripcion requestAEntidad(InscripcionRequest request, Alumno alumno, Grupo grupo) {
        return request == null
            ? null 
            : Inscripcion.crear(alumno, grupo);
    }
}

package com.eduardo.escuela.mapper;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.calificaciones.CalificacionRequest;
import com.eduardo.escuela.dto.calificaciones.CalificacionResponse;
import com.eduardo.escuela.dto.datos.DatosInscripcion;
import com.eduardo.escuela.entities.Calificacion;
import com.eduardo.escuela.entities.Inscripcion;
import com.eduardo.escuela.utils.StringCustomUtils;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class CalificacionMapper{
    private final AlumnoMapper alumnoMapper;
    private final GrupoMapper grupoMapper;
    public CalificacionResponse entidadAResponse(Calificacion entidad) {
        return entidad == null
            ? null
            : new CalificacionResponse(
                entidad.getId(), 
                entidadADatosInscripcion(entidad.getInscripcion()), 
                entidad.getCalificacion(), 
                StringCustomUtils.localDateAString(entidad.getFechaRegistro()));
    }

    public Calificacion requestAEntidad(CalificacionRequest request, Inscripcion inscripcion){
        return request == null
            ? null
            : Calificacion.crear(inscripcion, request.calificacion());
    }

    private DatosInscripcion entidadADatosInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) return null;
        return new DatosInscripcion(
            alumnoMapper.entidadADatosAlumno(inscripcion.getAlumno()),
            grupoMapper.entidadADatosGrupo(inscripcion.getGrupo()),
            StringCustomUtils.localDateAString(inscripcion.getFechaInscripcion())
        );
    } 
}

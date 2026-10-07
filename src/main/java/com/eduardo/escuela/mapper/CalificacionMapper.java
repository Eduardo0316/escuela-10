package com.eduardo.escuela.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.calificaciones.CalificacionResponse;
import com.eduardo.escuela.dto.datos.DatosAlumno;
import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.datos.DatosInscripcion;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.entities.Calificacion;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Inscripcion;

@Component 
public class CalificacionMapper{
    public CalificacionResponse entidadAResponse(Calificacion entidad) {
        return entidad == null
            ? null
            : new CalificacionResponse(
                entidad.getId(), 
                entidadADatosInscripcion(entidad.getInscripcion()), 
                entidad.getCalificacion(), 
                entidad.getFechaRegistro());
    }

    private DatosInscripcion entidadADatosInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) return null;
        return new DatosInscripcion(
            entidadADatosAlumno(inscripcion.getAlumno()),
            entidadADatosGrupo(inscripcion.getGrupo()),
            formatearFecha(inscripcion.getFechaInscripcion())
        );
    }

    private DatosAlumno entidadADatosAlumno(Alumno alumno) {
        if (alumno == null) return null;
        return new DatosAlumno(
            String.join( 
                " ", 
                alumno.getNombre(), 
                alumno.getApellidoPaterno(), 
                alumno.getApellidoMaterno()),
            alumno.getMatricula(),
            alumno.getEmail(),
            formatearFecha(alumno.getFechaIngreso())
        );
    }

    private DatosGrupo entidadADatosGrupo(Grupo grupo) {
        String nombreCompletoMaestro = grupo.getMaestro().getNombre() + " " + 
            grupo.getMaestro().getApellidoPaterno() + " " + 
            grupo.getMaestro().getApellidoMaterno();
        

        return grupo == null
            ? null
            : new DatosGrupo(
            grupo.getCurso().getNombre(),
            nombreCompletoMaestro,
            grupo.getAula().getNombre(),
            grupo.getPeriodo()
        );
    }

    private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String formatearFecha(LocalDate fecha) {
        return fecha == null ? null : fecha.format(FORMATO_FECHA);
    }    
}

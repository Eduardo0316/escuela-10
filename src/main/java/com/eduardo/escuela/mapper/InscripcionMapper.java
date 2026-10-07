package com.eduardo.escuela.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosAlumno;
import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.inscripciones.InscripcionResponse;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Inscripcion;

@Component 
public class InscripcionMapper{
    public InscripcionResponse entidadAResponse(Inscripcion entidad) {
        BigDecimal calificacion = (entidad.getCalificacion() != null) 
            ? entidad.getCalificacion().getCalificacion()
            : null;
        return entidad == null
            ? null
            : new InscripcionResponse(
                entidad.getId(), 
                entidadADatosAlumno(entidad.getAlumno()), 
                entidadADatosGrupo(entidad.getGrupo()), 
                calificacion, 
                entidad.getFechaInscripcion().toString());
    }

    private DatosAlumno entidadADatosAlumno(Alumno alumno){
        return alumno == null 
            ? null 
            : new DatosAlumno(
                String.join( 
                    " ", 
                    alumno.getNombre(), 
                    alumno.getApellidoPaterno(), 
                    alumno.getApellidoMaterno()), 
                alumno.getMatricula(), 
                alumno.getEmail(), 
                alumno.getFechaIngreso().toString());
    }

    private DatosGrupo entidadADatosGrupo(Grupo grupo){
        String nombreCompletoMaestro = grupo.getMaestro().getNombre() + " " + 
            grupo.getMaestro().getApellidoPaterno() + " " + 
            grupo.getMaestro().getApellidoMaterno();

        return grupo == null 
            ? null 
            : new DatosGrupo(
                grupo.getCurso().getNombre(), 
                nombreCompletoMaestro, 
                grupo.getAula().getNombre(), 
                grupo.getPeriodo());
    }
}

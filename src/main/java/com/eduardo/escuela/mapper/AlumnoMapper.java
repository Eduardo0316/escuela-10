package com.eduardo.escuela.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.alumnos.AlumnoRequest;
import com.eduardo.escuela.dto.alumnos.AlumnoResponse;
import com.eduardo.escuela.dto.datos.DatosAlumno;
import com.eduardo.escuela.dto.datos.DatosCalificacion;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.utils.StringCustomUtils;

@Component  
public class AlumnoMapper implements CommonMapper<AlumnoRequest, AlumnoResponse, Alumno>{
    @Override
    public AlumnoResponse entidadAResponse(Alumno entidad) {
        return entidad == null
            ? null
            : new AlumnoResponse(
                entidad.getId(), 
                String.join(" ",
                    entidad.getNombre(), 
                    entidad.getApellidoPaterno(),
                    entidad.getApellidoMaterno()), 
                entidad.getEmail(), 
                entidad.getMatricula(), 
                StringCustomUtils.localDateAString(
                    entidad.getFechaIngreso()),
                entidadADatosCalificacions(entidad), 
                entidad.calcularPromedio());
    }

    private List<DatosCalificacion> entidadADatosCalificacions(Alumno entidad){
        if (entidad == null || entidad.getInscripciones() == null || entidad.getInscripciones().isEmpty()){
            return List.of();
        }

        return entidad.getInscripciones().stream()
            .map(inscripcion -> new DatosCalificacion(
                inscripcion.getGrupo().getCurso().getNombre(), 
                inscripcion.getGrupo().getPeriodo(), 
                inscripcion.getCalificacion() != null
                    ? inscripcion.getCalificacion().getCalificacion()
                    : null
                )).toList();
    }

    @Override
    public Alumno requestAEntidad(AlumnoRequest request) {
        return request == null
            ? null
            : Alumno.crear(
                request.nombre(), 
                request.apellidoPaterno(), 
                request.apellidoMaterno());
    }
    
    public Alumno requestAEntidad(AlumnoRequest request, String email, String matricula) {
        if (request == null) return null;

        Alumno alumno = requestAEntidad(request);
        alumno.asignarDatosAcademicos(email, matricula);
        return alumno;
    }

    public DatosAlumno entidadADatosAlumno(Alumno alumno) {
        if (alumno == null) return null;
        return new DatosAlumno(
            String.join( 
                " ", 
                alumno.getNombre(), 
                alumno.getApellidoPaterno(), 
                alumno.getApellidoMaterno()),
            alumno.getMatricula(),
            alumno.getEmail(),
            StringCustomUtils.localDateAString(alumno.getFechaIngreso())
        );
    }
}

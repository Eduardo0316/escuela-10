package com.eduardo.escuela.services.alumnos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.alumnos.AlumnoRequest;
import com.eduardo.escuela.dto.alumnos.AlumnoResponse;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.mapper.AlumnoMapper;
import com.eduardo.escuela.repositories.AlumnoRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Transactional 
@Slf4j 
public class AlumnoServiceImpl implements AlumnoService{
    private final AlumnoRepository alumnoRepository;
    private final AlumnoMapper alumnoMapper;

    private final InscripcionRepository inscripcionRepository;
    
    @Override
    public AlumnoResponse actualizar(AlumnoRequest request, Long id) {
        Alumno alumno = obtenerAlumno(id);

        if(alumno.cambioEnDatos(request.nombre(), request.apellidoPaterno(), request.apellidoMaterno())){
            alumno.actualizar(
                request.nombre(), 
                request.apellidoPaterno(), 
                request.apellidoMaterno(), 
                generarEmail(request), 
                generarMatricula(request));
            
            alumnoRepository.save(alumno);
            alumnoRepository.flush();
            log.info("Datos del alumno {} actualizados correctamente", alumno.getNombre());
        }
        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public void eliminar(Long id) {
        Alumno alumno = obtenerAlumno(id);

        log.info("Eliminando alumno con id: {}", id);

        if(inscripcionRepository.existsByAlumnoId(id))
            throw new EntidadRelacionadaException(
        "No se puede eliminar un alumno que tiene inscripciones asignadas");

        alumnoRepository.delete(alumno);
        alumnoRepository.flush();
        
        log.info("Alumno con id {} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar() {
        return alumnoRepository.findAll().stream()
            .map(alumnoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {
        return alumnoMapper.entidadAResponse(obtenerAlumno(id));
    }

    @Override
    public AlumnoResponse registrar(AlumnoRequest request) {
        log.info("Registrando nuevo alumno");
        Alumno alumno = alumnoMapper.requestAEntidad(
            request,
            generarEmail(request),
            generarMatricula(request));

        alumnoRepository.save(alumno);
        alumnoRepository.flush();

        log.info("Nuevo alumno {} registrado correctamente", alumno.getNombre());
        return alumnoMapper.entidadAResponse(alumno);
    }
    
    private Alumno obtenerAlumno(Long id){
        return ServiceUtils.obtenerEntidadOException(alumnoRepository, id, Alumno.class);
    }

    private String generarEmail(AlumnoRequest request){
        log.info("Generando email");

        return alumnoRepository.generarEmail(
            request.nombre().trim(), 
            request.apellidoPaterno().trim(), 
            request.apellidoMaterno().trim());
    }

    private String generarMatricula(AlumnoRequest request){
        log.info("Generando matricula");

        return alumnoRepository.generarMatricula(
            request.nombre().trim(), 
            request.apellidoPaterno().trim(), 
            request.apellidoMaterno().trim());
    }
}

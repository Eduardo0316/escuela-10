package com.eduardo.escuela.services.inscripciones;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.inscripciones.InscripcionRequest;
import com.eduardo.escuela.dto.inscripciones.InscripcionResponse;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Inscripcion;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.mapper.InscripcionMapper;
import com.eduardo.escuela.repositories.AlumnoRepository;
import com.eduardo.escuela.repositories.CalificacionRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@Transactional 
@RequiredArgsConstructor 
public class InscripcionServiceImpl implements InscripcionService{
    private final AlumnoRepository alumnoRepository;
    private final GrupoRepository grupoRepository;
    private final CalificacionRepository calificacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final InscripcionMapper inscripcionMapper;

    @Override
    public InscripcionResponse actualizar(InscripcionRequest request, Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        Inscripcion inscripcionActualizada = Inscripcion.crear(alumno, grupo);

        if(inscripcionRepository.existsByAlumnoIdAndGrupoId(request.idAlumno(), request.idGrupo()))
            throw new EntidadRelacionadaException("El alumno ya se encuentra inscrito en este curso");

        inscripcion.actualizar(inscripcionActualizada.getAlumno(), inscripcionActualizada.getGrupo());
        log.info("Inscripcion con id {} actualizada", id);
        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public void eliminar(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);

        if(calificacionRepository.existsByInscripcionId(id))
            throw new EntidadRelacionadaException("No se pueden eliminar inscripciones con calificaciones asociadas");

        inscripcionRepository.delete(inscripcion);
        inscripcionRepository.flush();

        log.info("Inscripcion con id {} eliminada", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> listar() {
        log.info("Listando inscripciones");
        return inscripcionRepository.findAll().stream()
            .map(inscripcionMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse obtenerPorId(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        log.info("Obteniendo inscripcion con id {}", id);
        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public InscripcionResponse registrar(InscripcionRequest request) {
        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        if(inscripcionRepository.existsByAlumnoIdAndGrupoId(request.idAlumno(), request.idGrupo()))
            throw new ConflictoException("El alumno ya se encuentra inscrito en este grupo");

        inscripcionRepository.save(inscripcion);
        log.info("Inscribiendo alumno con id {} al grupo con id {} en inscripcion id {}", request.idAlumno(), request.idGrupo(), inscripcion.getId());
        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    private Alumno obtenerAlumno(Long id){
        return ServiceUtils.obtenerEntidadOException(alumnoRepository, id, Alumno.class);
    }
    
    private Grupo obtenerGrupo(Long id){
        return ServiceUtils.obtenerEntidadOException(grupoRepository, id, Grupo.class);
    }
    
    private Inscripcion obtenerInscripcion(Long id){
        return ServiceUtils.obtenerEntidadOException(inscripcionRepository, id, Inscripcion.class);
    }
}

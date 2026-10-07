package com.eduardo.escuela.services.calificaciones;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.calificaciones.CalificacionRequest;
import com.eduardo.escuela.dto.calificaciones.CalificacionResponse;
import com.eduardo.escuela.entities.Calificacion;
import com.eduardo.escuela.entities.Inscripcion;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.mapper.CalificacionMapper;
import com.eduardo.escuela.repositories.CalificacionRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Transactional 
@Slf4j 
@RequiredArgsConstructor 
public class CalificacionServiceImpl implements CalificacionService{
    private final InscripcionRepository inscripcionRepository;
    private final CalificacionRepository calificacionRepository;
    private final CalificacionMapper calificacionMapper;

    @Override
    public CalificacionResponse actualizar(CalificacionRequest request, Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        Inscripcion inscripcion = obtenerInscripcion(request.idInscripcion());

        Calificacion calificacionActualizada = Calificacion.crear(inscripcion, request.calificacion());
        // if(calificacionRepository.existsByInscripcionId(request.idInscripcion()))
        //     throw new ConflictoException("Esta inscripcion ya tiene calificacion");

        calificacion.actualizar(
            calificacionActualizada.getInscripcion(), 
            calificacionActualizada.getCalificacion());

        log.info("Calificación con id {} acualizada", calificacion.getId());

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public void eliminar(Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        log.info("Eliminando calificacion con id {}", calificacion.getId());

        calificacionRepository.delete(calificacion);
        calificacionRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponse> listar() {
        return calificacionRepository.findAll().stream()
            .map(calificacionMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Long id) {        
        return calificacionMapper.entidadAResponse(obtenerCalificacion(id));
    }

    @Override
    public CalificacionResponse registrar(CalificacionRequest request) {
        Inscripcion inscripcion = obtenerInscripcion(request.idInscripcion());
        Calificacion calificacion = Calificacion.crear(inscripcion, request.calificacion());

        if(calificacionRepository.existsByInscripcionId(request.idInscripcion()))
            throw new ConflictoException("Esta inscripcion ya tiene calificacion");
        
        calificacionRepository.save(calificacion);
        log.info("Guardando calificacion con id {}", calificacion.getId());
        
        return calificacionMapper.entidadAResponse(calificacion);
    }
    
    private Inscripcion obtenerInscripcion(Long id){
        return ServiceUtils.obtenerEntidadOException(inscripcionRepository, id, Inscripcion.class);
    }
    
    private Calificacion obtenerCalificacion(Long id){
        return ServiceUtils.obtenerEntidadOException(calificacionRepository, id, Calificacion.class);
    }
}

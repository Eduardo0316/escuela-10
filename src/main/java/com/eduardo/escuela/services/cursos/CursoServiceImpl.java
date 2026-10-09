package com.eduardo.escuela.services.cursos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.cursos.CursoRequest;
import com.eduardo.escuela.dto.cursos.CursoResponse;
import com.eduardo.escuela.entities.Curso;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.mapper.CursoMapper;
import com.eduardo.escuela.repositories.CursoRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
@Transactional 
public class CursoServiceImpl implements CursoService {
    private final CursoMapper cursoMapper;
    private final CursoRepository cursoRepository;

    private final GrupoRepository grupoRepository;

    @Override
    public CursoResponse actualizar(CursoRequest request, Long id) {
        validarDatosUnicos(request.nombre(), id);

        Curso curso = obtenerCurso(id);
        curso.actualizar(
            request.nombre(), 
            request.descripcion(), 
            request.creditos());
        cursoRepository.save(curso);
        cursoRepository.flush();

        log.info("Curso con id {} actualizado", curso.getId());
        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public void eliminar(Long id) {
        Curso curso = obtenerCurso(id);

        if(grupoRepository.existsByCursoId(id))
            throw new EntidadRelacionadaException("No se pueden eliminar cursos con grupos relacionados");

        cursoRepository.delete(curso);
        cursoRepository.flush();

        log.info("Curso con id {} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        log.info("Listando aulas");

        return cursoRepository.findAll().stream()
            .map(cursoMapper::entidadAResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return cursoMapper.entidadAResponse(obtenerCurso(id));
    }

    @Override
    public CursoResponse registrar(CursoRequest request) {
        Curso curso = cursoMapper.requestAEntidad(request);
        validarNombreUnico(request.nombre());
        cursoRepository.save(curso);
        cursoRepository.flush();

        log.info("Curso con id {} registrado", curso.getId());
        return cursoMapper.entidadAResponse(curso);
    }

    private Curso obtenerCurso(Long id){
        return ServiceUtils.obtenerEntidadOException(cursoRepository, id, Curso.class);
    }

    private void validarDatosUnicos(String nombre, Long id){
        if(cursoRepository.existsByNombreAndIdNot(nombre, id))
            throw new ConflictoException("Ya existe un curso con ese nombre");
    }
    
    private void validarNombreUnico(String nombre){
        if(cursoRepository.existsByNombre(nombre))
            throw new ConflictoException("Ya existe un curso con ese nombre");
    }
}

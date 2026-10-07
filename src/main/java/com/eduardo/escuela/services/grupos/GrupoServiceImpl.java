package com.eduardo.escuela.services.grupos;

import com.eduardo.escuela.mapper.GrupoMapper;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.grupo.GrupoRequest;
import com.eduardo.escuela.dto.grupo.GrupoResponse;
import com.eduardo.escuela.entities.Aula;
import com.eduardo.escuela.entities.Curso;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Maestro;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.repositories.AulaRepository;
import com.eduardo.escuela.repositories.CursoRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.repositories.HorarioRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.repositories.MaestroRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
@Transactional 
public class GrupoServiceImpl implements GrupoService {
    private final GrupoMapper grupoMapper;
    private final AulaRepository aulaRepository;
    private final MaestroRepository maestroRepository;
    private final CursoRepository cursoRepository;
    private final GrupoRepository grupoRepository;
    private final HorarioRepository horarioRepository;
    private final InscripcionRepository inscripcionRepository;
    
    @Override
    public GrupoResponse actualizar(GrupoRequest request, Long id) {
        Grupo grupo = obtenerGrupo(id);
        Curso curso = obtenerCurso(request.idCurso());
        Maestro maestro = obtenerMaestro(request.idMaestro());
        Aula aula = obtenerAula(request.idAula());

        Grupo grupoActualizado = Grupo.crear(curso, maestro, aula, request.periodo());

        if(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(request.idCurso(), request.idMaestro(), request.idAula(), request.periodo()))
            throw new ConflictoException("Ese grupo ya existe");

        grupo.actualizar(
            grupoActualizado.getCurso(), 
            grupoActualizado.getMaestro(), 
            grupoActualizado.getAula(), 
            grupoActualizado.getPeriodo());

        log.info("Actualizando grupo con id {}", grupo.getId());
        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    public void eliminar(Long id) {
        Grupo grupo = obtenerGrupo(id);

        if(horarioRepository.existsByGrupoId(id) || inscripcionRepository.existsByGrupoId(id))
            throw new EntidadRelacionadaException("No se puede eliminar un grupo con horarios o inscripciones asociadas");

        grupoRepository.delete(grupo);
        grupoRepository.flush();

        log.info("Grupo con Id {} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrupoResponse> listar() {
        log.info("Listando grupos");
        return grupoRepository.findAll().stream()
            .map(grupoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GrupoResponse obtenerPorId(Long id) {
        return grupoMapper.entidadAResponse(obtenerGrupo(id));
    }

    @Override
    public GrupoResponse registrar(GrupoRequest request) {
        Curso curso = obtenerCurso(request.idCurso());
        Maestro maestro = obtenerMaestro(request.idMaestro());
        Aula aula = obtenerAula(request.idAula());
        Grupo grupo = Grupo.crear(curso, maestro, aula, request.periodo());

        if(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(request.idCurso(), request.idMaestro(), request.idAula(), request.periodo()))
            throw new ConflictoException("Ese grupo ya existe");

        grupoRepository.save(grupo);
        log.info("Grupo con id {} guardado", grupo.getId());
        return grupoMapper.entidadAResponse(grupo);
    }

    private Curso obtenerCurso(Long id){
        return ServiceUtils.obtenerEntidadOException(cursoRepository, id, Curso.class);
    }
    
    private Maestro obtenerMaestro(Long id){
        return ServiceUtils.obtenerEntidadOException(maestroRepository, id, Maestro.class);
    }
    
    private Aula obtenerAula(Long id){
        return ServiceUtils.obtenerEntidadOException(aulaRepository, id, Aula.class);
    }

    private Grupo obtenerGrupo(Long id){
        return ServiceUtils.obtenerEntidadOException(grupoRepository, id, Grupo.class);
    }
}

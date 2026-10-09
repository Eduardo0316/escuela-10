package com.eduardo.escuela.services.aulas;

import com.eduardo.escuela.utils.ServiceUtils;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.aulas.AulaRequest;
import com.eduardo.escuela.dto.aulas.AulaResponse;
import com.eduardo.escuela.entities.Aula;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.mapper.AulaMapper;
import com.eduardo.escuela.repositories.AulaRepository;
import com.eduardo.escuela.repositories.GrupoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor
@Slf4j 
@Transactional 
public class AulasServiceImpl implements AulasService {

    private final GrupoRepository grupoRepository;

    private final AulaRepository aulaRepository;
    private final AulaMapper aulaMapper;

    @Override
    public AulaResponse actualizar(AulaRequest request, Long id) {
        validarDatosUnicos(request.nombre());

        Aula aula = obtenerAula(id);
        aula.actualizar(request.nombre(), request.capacidad());

        aulaRepository.save(aula);
        aulaRepository.flush();
        
        log.info("Aula con id {} actualizada", aula.getId());

        return aulaMapper.entidadAResponse(aula);
    }

    @Override
    public void eliminar(Long id) {
        Aula aula = obtenerAula(id);

        if(grupoRepository.existsByAulaId(id))
            throw new EntidadRelacionadaException("No se pueden eliminar aulas con grupos asignados");

        aulaRepository.delete(aula);
        aulaRepository.flush();

        log.info("Aula con id {} eliminada", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AulaResponse> listar() {
        log.info("Listando aulas");
        
        return aulaRepository.findAll().stream()
            .map(aulaMapper::entidadAResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AulaResponse obtenerPorId(Long id) {
        return aulaMapper.entidadAResponse(obtenerAula(id));
    }

    @Override
    public AulaResponse registrar(AulaRequest request) {
        Aula aula = aulaMapper.requestAEntidad(request);
        validarDatosUnicos(aula.getNombre());
        aulaRepository.save(aula);
        aulaRepository.flush();
        log.info("Aula agregada con id: {}", aula.getId());
        return aulaMapper.entidadAResponse(aula);
    }

    private Aula obtenerAula(Long id){
        return ServiceUtils.obtenerEntidadOException(aulaRepository, id, Aula.class);
    }

    private void validarDatosUnicos(String nombre){
        if (aulaRepository.existsByNombre(nombre))
            throw new ConflictoException("Nombre de aula ya existente");
    }
}

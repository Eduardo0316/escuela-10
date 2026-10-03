package com.eduardo.escuela.services.maestros;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.entities.Maestro;
import com.eduardo.escuela.mapper.MaestroMapper;
import com.eduardo.escuela.repositories.MaestroRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Transactional 
@Slf4j 
public class MaestroServiceImpl implements MaestroService{
    private final MaestroRepository maestroRepository;
    private final MaestroMapper maestroMapper;

    @Override
    public MaestroResponse actualizar(MaestroRequest request, Long id) {
        return null;
    }

    @Override
    public void eliminar(Long id) {
        
    }

    @Override
    public List<MaestroResponse> listar() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public MaestroResponse obtenerPorID(Long id) {
        return maestroMapper.entidadAResponse(obtenerMaestro(id));
    }

    @Override
    public MaestroResponse registrar(MaestroRequest request) {
        return null;
    }

    private Maestro obtenerMaestro(Long id){
        return ServiceUtils.obtenerEntidadOException(maestroRepository, id, Maestro.class);
    }
}

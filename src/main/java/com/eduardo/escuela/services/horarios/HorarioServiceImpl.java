package com.eduardo.escuela.services.horarios;

import com.eduardo.escuela.mapper.HorarioMapper;
import com.eduardo.escuela.repositories.HorarioRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eduardo.escuela.dto.horarios.HorarioRequest;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.enums.DiaSemana;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.repositories.GrupoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Transactional 
@Slf4j 
@RequiredArgsConstructor 
public class HorarioServiceImpl implements HorarioService{
    private final HorarioMapper horarioMapper;
    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;

    @Override
    public HorarioResponse actualizar(HorarioRequest request, Long id) {
        Grupo grupo = obtenerGrupo(request.idGrupo());
        DiaSemana dia = DiaSemana.obtenerPorDescripcion(request.dia());
        Horario horario = obtenerHorario(id);

        Horario horarioActualizado = Horario.crear(
            grupo, 
            dia, 
            request.horaInicio(), 
            request.horaFin());
        
        if(horarioRepository.existeTraslape(request.idGrupo(), dia, request.horaInicio(), request.horaFin()))
            throw new ConflictoException("El horario se traslapa con otro del mismo grupo");
    
        horario.actualizar(
            horarioActualizado.getGrupo(), 
            horarioActualizado.getDia(), 
            horarioActualizado.getHoraInicio(), 
            horarioActualizado.getHoraFin());

        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public void eliminar(Long id) {
        Horario horario = obtenerHorario(id);
        horarioRepository.delete(horario);
        horarioRepository.flush();
        log.info("Horario con id {} eliminado", id);
    }

    @Override
    public List<HorarioResponse> listar() {
        return horarioRepository.findAll().stream()
            .map(horarioMapper::entidadAResponse)
            .toList();
    }

    @Override
    public HorarioResponse obtenerPorId(Long id) {
        return horarioMapper.entidadAResponse(obtenerHorario(id));
    }

    @Override
    public HorarioResponse registrar(HorarioRequest request) {
        Grupo grupo = obtenerGrupo(request.idGrupo());

        DiaSemana dia = DiaSemana.obtenerPorDescripcion(request.dia());

        Horario horario = Horario.crear(
            grupo, 
            dia, 
            request.horaInicio(), 
            request.horaFin());
        
        if(horarioRepository.existeTraslape(request.idGrupo(), dia, request.horaInicio(), request.horaFin()))
            throw new ConflictoException("El horario se traslapa con otro del mismo grupo");
    
        horarioRepository.save(horario);

        return horarioMapper.entidadAResponse(horario);
    }

    private Horario obtenerHorario(Long id){
        return ServiceUtils.obtenerEntidadOException(horarioRepository, id, Horario.class);
    }

    private Grupo obtenerGrupo(Long id){
        return ServiceUtils.obtenerEntidadOException(grupoRepository, id, Grupo.class);
    }
}

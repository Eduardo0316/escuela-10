package com.eduardo.escuela.services.horarios;

import com.eduardo.escuela.mapper.GrupoMapper;
import com.eduardo.escuela.mapper.HorarioMapper;
import com.eduardo.escuela.repositories.HorarioRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.horarios.HorarioRequest;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.enums.DiaSemana;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.repositories.GrupoRepository;

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
    private final GrupoMapper grupoMapper;

    @Override
    public HorarioResponse actualizar(HorarioRequest request, Long id) {
        Horario horario = obtenerHorario(id);
        Grupo grupo = obtenerGrupo(request.idGrupo());
        DiaSemana dia = DiaSemana.obtenerPorDescripcion(request.dia());

        if (horarioRepository.existeTraslapeExcepto(
                request.idGrupo(), dia, request.horaInicio(), request.horaFin(), id))
            throw new ConflictoException("El horario se traslapa con otro del mismo grupo");

        horario.actualizar(grupo, dia, request.horaInicio(), request.horaFin());

        horarioRepository.save(horario);
        horarioRepository.flush();

        log.info("Horario con id {} actualizado", id);

        return horarioMapper.entidadAResponse(
                horario,
                grupoMapper.entidadADatosGrupo(grupo));
    }

    @Override
    public void eliminar(Long id) {
        Horario horario = obtenerHorario(id);
        horarioRepository.delete(horario);
        horarioRepository.flush();

        log.info("Horario con id {} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponse> listar() {
        return horarioRepository.findAll().stream()
            .map(horario -> horarioMapper.entidadAResponse(
                horario, 
                grupoMapper.entidadADatosGrupo(horario.getGrupo())
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioResponse obtenerPorId(Long id) {
        Horario horario = obtenerHorario(id);
        DatosGrupo datosGrupo = grupoMapper.entidadADatosGrupo(horario.getGrupo());
        return horarioMapper.entidadAResponse(horario, datosGrupo);
    }

    @Override
    public HorarioResponse registrar(HorarioRequest request) {
        Grupo grupo = obtenerGrupo(request.idGrupo());
        DiaSemana dia = DiaSemana.obtenerPorDescripcion(request.dia());

        if (!request.horaInicio().isBefore(request.horaFin()))
            throw new ConflictoException(
                "La hora de inicio debe ser anterior a la hora de fin");

        if (horarioRepository.existeTraslape(
                request.idGrupo(), dia, request.horaInicio(), request.horaFin()))
            throw new ConflictoException("El horario se traslapa con otro del mismo grupo");

        Horario horario = horarioMapper.requestAEntidad(request, grupo);

        horarioRepository.save(horario);
        horarioRepository.flush();
        log.info("Horario con id {} registrado", horario.getId());

        return horarioMapper.entidadAResponse(
                horario,
                grupoMapper.entidadADatosGrupo(grupo));
    }

    private Horario obtenerHorario(Long id){
        return ServiceUtils.obtenerEntidadOException(horarioRepository, id, Horario.class);
    }

    private Grupo obtenerGrupo(Long id){
        return ServiceUtils.obtenerEntidadOException(grupoRepository, id, Grupo.class);
    }
}

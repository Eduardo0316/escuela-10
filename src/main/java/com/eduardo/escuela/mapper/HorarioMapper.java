package com.eduardo.escuela.mapper;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.datos.DatosHorario;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Horario;

@Component
public class HorarioMapper {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public HorarioResponse entidadAResponse(Horario horario) {
        if (horario == null) return null;
        return new HorarioResponse(
            horario.getId(),
            entidadADatosGrupo(horario.getGrupo()),
            formatear(horario)
        );
    }

    public DatosHorario entidadADatosHorario(Horario horario) {
        return horario == null ? null : new DatosHorario(formatear(horario));
    }

    private DatosGrupo entidadADatosGrupo(Grupo grupo) {
        if (grupo == null) return null;
        return new DatosGrupo(
            grupo.getCurso().getNombre(),
            grupo.getMaestro().getNombre(),
            grupo.getAula().getNombre(),
            grupo.getPeriodo()
        );
    }

    private String formatear(Horario h) {
        return h.getDia().getDescripcion() + " "
            + h.getHoraInicio().format(HORA) + " - " + h.getHoraFin().format(HORA);
    }
}
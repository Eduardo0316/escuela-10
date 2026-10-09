package com.eduardo.escuela.mapper;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.horarios.HorarioRequest;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.enums.DiaSemana;

@Component
public class HorarioMapper {
    public HorarioResponse entidadAResponse(Horario horario, DatosGrupo datosGrupo) {
        if (horario == null) return null;
        return new HorarioResponse(
            horario.getId(),
            datosGrupo,
            horario.entidadAHorarioFormateado(horario)
        );
    }

    public Horario requestAEntidad(HorarioRequest request, Grupo grupo){
        return request == null
            ? null
            : Horario.crear(
                grupo, 
                DiaSemana.obtenerPorDescripcion(request.dia()), 
                request.horaInicio(), 
                request.horaFin());
    }
}
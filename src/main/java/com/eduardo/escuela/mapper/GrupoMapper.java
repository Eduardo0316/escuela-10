package com.eduardo.escuela.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.grupo.GrupoResponse;
import com.eduardo.escuela.entities.Grupo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoMapper {
    private final CursoMapper cursoMapper;
    private final MaestroMapper maestroMapper;
    private final AulaMapper aulaMapper;
    private final HorarioMapper horarioMapper;

    public GrupoResponse entidadAResponse(Grupo grupo) {
        if (grupo == null) return null;

        return new GrupoResponse(
            grupo.getId(),
            cursoMapper.entidadADatosCurso(grupo.getCurso()),
            maestroMapper.entidadADatosMaestro(grupo.getMaestro()),
            aulaMapper.entidadADatosAula(grupo.getAula()),
            grupo.getHorarios().stream().map(horarioMapper::entidadADatosHorario).toList(),
            grupo.getPeriodo()
        );
    }
}
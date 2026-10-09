package com.eduardo.escuela.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.grupo.GrupoRequest;
import com.eduardo.escuela.dto.grupo.GrupoResponse;
import com.eduardo.escuela.entities.Aula;
import com.eduardo.escuela.entities.Curso;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.entities.Maestro;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoMapper {
    private final CursoMapper cursoMapper;
    private final MaestroMapper maestroMapper;
    private final AulaMapper aulaMapper;

    public GrupoResponse entidadAResponse(Grupo grupo) {
        return grupo == null
            ? null
            : new GrupoResponse(
                grupo.getId(),
                cursoMapper.entidadADatosCurso(grupo.getCurso()),
                maestroMapper.entidadADatosMaestro(grupo.getMaestro()),
                aulaMapper.entidadADatosAula(grupo.getAula()),
                grupo.getHorarios() == null ? List.of() : grupo.getHorarios().stream()
                    .map(Horario::entidadAHorarioFormateado)
                    .toList(),
                grupo.getPeriodo()
        );
    }

    public Grupo requestAEntidad(GrupoRequest request, Curso curso, Maestro maestro, Aula aula) {
        return request == null
            ? null
            : Grupo.crear(curso, maestro, aula, request.periodo());
    }

    public DatosGrupo entidadADatosGrupo(Grupo grupo) {
        if (grupo == null) return null;

        return new DatosGrupo(
            grupo.getCurso().getNombre(),
            grupo.getMaestro().obtenerNombreCompletoMaestro(),
            grupo.getAula().getNombre(),
            grupo.getPeriodo()
        );
    }
}
package com.eduardo.escuela.dto.grupo;

import java.util.List;

import com.eduardo.escuela.dto.datos.DatosAula;
import com.eduardo.escuela.dto.datos.DatosCurso;
import com.eduardo.escuela.dto.datos.DatosMaestro;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del grupo")
public record GrupoResponse(
    @Schema(description = "Id del grupo", example = "1")
    Long id,
    
    @Schema(description = "Datos del curso del grupo")
    DatosCurso curso,
    
    @Schema(description = "Datos del maestro del grupo")
    DatosMaestro maestro,
    
    @Schema(description = "Datos del aula del grupo")
    DatosAula aula,
    
    @Schema(description = "Datos de los horarios del grupo")
    List<String> horarios,
    
    @Schema(description = "Periodo del grupo")
    String periodo
) {

}

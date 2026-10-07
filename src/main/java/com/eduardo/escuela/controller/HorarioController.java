package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.horarios.HorarioRequest;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.services.horarios.HorarioService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping("/api/horario")
@Tag(name = "API Horarios", description = "Métodos para gestion de horarios")
public class HorarioController extends CRUDController<HorarioRequest, HorarioResponse, HorarioService> {
    public HorarioController(HorarioService service){
        super(service);
    }
}

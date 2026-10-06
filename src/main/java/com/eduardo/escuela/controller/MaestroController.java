package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.services.maestros.MaestroService;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController 
@RequestMapping("/api/maestros")
@Tag(name = "API Maestros", description = "Métodos para gestión de maestros")
public class MaestroController extends CRUDController<MaestroRequest, MaestroResponse, MaestroService>{
    public MaestroController(MaestroService service) {
        super(service);
    }
}

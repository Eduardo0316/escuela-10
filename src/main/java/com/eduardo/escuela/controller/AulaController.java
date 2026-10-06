package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.aulas.AulaRequest;
import com.eduardo.escuela.dto.aulas.AulaResponse;
import com.eduardo.escuela.services.aulas.AulasService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping("/api/aula")
@Tag(name = "API Aulas", description = "Métodos para gestión de aulas")
public class AulaController extends CRUDController<AulaRequest, AulaResponse, AulasService> {
    public AulaController(AulasService service){
        super(service);
    }
}

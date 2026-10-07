package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.inscripciones.InscripcionRequest;
import com.eduardo.escuela.dto.inscripciones.InscripcionResponse;
import com.eduardo.escuela.services.inscripciones.InscripcionService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping ("/api/inscripcion")
@Tag (name = "API Inscripciones", description = "Métodos para gestion de inscripciones")
public class InscripcionController extends CRUDController<InscripcionRequest, InscripcionResponse, InscripcionService> {
    public InscripcionController(InscripcionService service){
        super(service);
    }
}

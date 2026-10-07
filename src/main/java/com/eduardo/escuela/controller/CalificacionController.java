package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.calificaciones.CalificacionRequest;
import com.eduardo.escuela.dto.calificaciones.CalificacionResponse;
import com.eduardo.escuela.services.calificaciones.CalificacionService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping ("/api/calificaciones")
@Tag (name = "API Calificaciones", description = "Métodos para gestión de calificaciones")
public class CalificacionController extends CRUDController<CalificacionRequest, CalificacionResponse, CalificacionService>{
    public CalificacionController(CalificacionService service){
        super(service);
    }
}

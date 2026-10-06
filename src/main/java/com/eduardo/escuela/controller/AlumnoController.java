package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.alumnos.AlumnoRequest;
import com.eduardo.escuela.dto.alumnos.AlumnoResponse;
import com.eduardo.escuela.services.alumnos.AlumnoService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping("/api/alumnos")
@Tag(name = "API Alumnos", description = "Métodos para gestion de alumnos")
public class AlumnoController extends CRUDController<AlumnoRequest, AlumnoResponse, AlumnoService>{
    public AlumnoController(AlumnoService service){
        super(service);
    }
}

package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.cursos.CursoRequest;
import com.eduardo.escuela.dto.cursos.CursoResponse;
import com.eduardo.escuela.services.cursos.CursoService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping("/api/curso")
@Tag(name = "API curso", description = "Métodos para gestion de cursos")
public class CursoController extends CRUDController<CursoRequest, CursoResponse, CursoService> {
    public CursoController(CursoService service){
        super(service);
    }
}

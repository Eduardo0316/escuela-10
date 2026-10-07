package com.eduardo.escuela.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.escuela.dto.grupo.GrupoRequest;
import com.eduardo.escuela.dto.grupo.GrupoResponse;
import com.eduardo.escuela.services.grupos.GrupoService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping ("/api/grupo")
@Tag (name = "API Grupos", description = "Métodos para gestion de grupos")
public class GrupoController extends CRUDController<GrupoRequest, GrupoResponse, GrupoService> {
    public GrupoController(GrupoService service){
        super(service);
    }
}

package com.eduardo.escuela.controller;

import com.eduardo.escuela.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.eduardo.escuela.dto.datos.DatosAula;
import com.eduardo.escuela.dto.datos.DatosCurso;
import com.eduardo.escuela.dto.datos.DatosMaestro;
import com.eduardo.escuela.dto.grupo.GrupoRequest;
import com.eduardo.escuela.dto.grupo.GrupoResponse;
import com.eduardo.escuela.services.grupos.GrupoService;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(GrupoController.class)
@DisplayName("Pruebas unitarias de GrupoController")
class GrupoControllerTest {

    private static final String BASE_URL = "/api/grupo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GrupoService service;

    // ====== Datos de prueba ======
    private GrupoRequest request() {
        return new GrupoRequest(1L, 2L, 3L, "2026-01");
    }

    private GrupoResponse response(Long id) {
        return new GrupoResponse(
                id,
                new DatosCurso("Matemáticas II", "Curso de cálculo integral", 5),
                new DatosMaestro("Mauricio", "test@test.com", "2223334455"),
                new DatosAula("Aula 101", 30),
                List.of("Lunes 08:00 - 10:00", "Miércoles 08:00 - 10:00"),
                "2026-01"
        );
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/grupo -> 200 con la lista de grupos")
        void listar_devuelve200() throws Exception {
            when(service.listar()).thenReturn(List.of(response(1L), response(2L)));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2));

            verify(service).listar();
        }

        @Test
        @DisplayName("GET /api/grupo/{id} -> 200 con el grupo")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.periodo").value("2026-01"))
                    .andExpect(jsonPath("$.curso.nombre").value("Matemáticas II"))
                    .andExpect(jsonPath("$.maestro.email").value("test@test.com"))
                    .andExpect(jsonPath("$.aula.capacidad").value(30))
                    .andExpect(jsonPath("$.horarios.length()").value(2));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/grupo -> 201 con el grupo creado")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(GrupoRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.periodo").value("2026-01"));

            verify(service).registrar(any(GrupoRequest.class));
        }

        @Test
        @DisplayName("PUT /api/grupo/{id} -> 200 con el grupo actualizado")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(GrupoRequest.class), eq(1L))).thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.periodo").value("2026-01"));

            verify(service).actualizar(any(GrupoRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/grupo/{id} -> 204 sin contenido")
        void eliminar_devuelve204() throws Exception {
            doNothing().when(service).eliminar(1L);

            mockMvc.perform(delete(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(service).eliminar(1L);
        }
    }

    // =========================== UNHAPPY PATH ===========================
    @Nested
    @DisplayName("Unhappy path")
    class UnhappyPath {

        @Test
        @DisplayName("GET /api/grupo/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/grupo con body vacío -> 400 y no llama al service")
        void registrar_bodyVacio_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/grupo con periodo con formato inválido -> 400 y no llama al service")
        void registrar_periodoInvalido_devuelve400() throws Exception {
            GrupoRequest requestInvalido = new GrupoRequest(1L, 2L, 3L, "2026-13");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/grupo con idCurso negativo -> 400 y no llama al service")
        void registrar_idCursoNegativo_devuelve400() throws Exception {
            GrupoRequest requestInvalido = new GrupoRequest(-1L, 2L, 3L, "2026-01");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("PUT /api/grupo/{id} con ID negativo -> 400 y no llama al service")
        void actualizar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(put(BASE_URL + "/{id}", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("DELETE /api/grupo/{id} con ID negativo -> 400 y no llama al service")
        void eliminar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
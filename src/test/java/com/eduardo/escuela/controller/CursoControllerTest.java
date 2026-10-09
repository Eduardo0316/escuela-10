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

import com.eduardo.escuela.dto.cursos.CursoRequest;
import com.eduardo.escuela.dto.cursos.CursoResponse;
import com.eduardo.escuela.services.cursos.CursoService;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(CursoController.class)
@DisplayName("Pruebas unitarias de CursoController")
class CursoControllerTest {

    private static final String BASE_URL = "/api/curso";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CursoService service;

    // ====== Datos de prueba ======
    private CursoRequest request() {
        return new CursoRequest(
                "Inglés 2",
                "Curso para obtener A2 de inglés",
                6
        );
    }

    private CursoResponse response(Long id) {
        return new CursoResponse(
                id,
                "Inglés 2",
                "Curso para obtener A2 de inglés",
                6
        );
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/curso -> 200 con la lista de cursos")
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
        @DisplayName("GET /api/curso/{id} -> 200 con el curso")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Inglés 2"));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/curso -> 201 con el curso creado")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(CursoRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Inglés 2"));

            verify(service).registrar(any(CursoRequest.class));
        }

        @Test
        @DisplayName("PUT /api/curso/{id} -> 200 con el curso actualizado")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(CursoRequest.class), eq(1L))).thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Inglés 2"));

            verify(service).actualizar(any(CursoRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/curso/{id} -> 204 sin contenido")
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
        @DisplayName("GET /api/curso/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/curso con body inválido -> 400 y no llama al service")
        void registrar_bodyInvalido_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/curso con nombre vacío -> 400 y no llama al service")
        void registrar_nombreVacio_devuelve400() throws Exception {
            CursoRequest requestInvalido = new CursoRequest(
                    "", "Curso sin nombre", 6);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/curso con créditos negativos -> 400 y no llama al service")
        void registrar_creditosNegativos_devuelve400() throws Exception {
            CursoRequest requestInvalido = new CursoRequest(
                    "Inglés 2", "Curso para obtener A2 de inglés", -5);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("PUT /api/curso/{id} con ID negativo -> 400 y no llama al service")
        void actualizar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(put(BASE_URL + "/{id}", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("DELETE /api/curso/{id} con ID negativo -> 400 y no llama al service")
        void eliminar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
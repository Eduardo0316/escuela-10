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

import java.math.BigDecimal;
import java.util.List;

import com.eduardo.escuela.dto.alumnos.AlumnoRequest;
import com.eduardo.escuela.dto.alumnos.AlumnoResponse;
import com.eduardo.escuela.services.alumnos.AlumnoService;

// Si tienes un @RestControllerAdvice, agrégalo para que los errores se mapeen a 400/404:
@Import(GlobalExceptionHandler.class)
@WebMvcTest(AlumnoController.class)
class AlumnoControllerTest {

    private static final String BASE_URL = "/api/alumnos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Spring Boot < 3.4: usa @MockBean (org.springframework.boot.test.mock.mockito.MockBean)
    @MockitoBean
    private AlumnoService service;

    // ====== Datos de prueba: AJUSTA a los campos reales de tus DTOs ======
    private AlumnoRequest request() {
        return new AlumnoRequest("Juan", "Pérez", "juan.perez@correo.com");
    }

    private AlumnoResponse response(Long id) {
        return new AlumnoResponse(
                id,
                "Juanito Pérez López",
                "test@test.com",
                "1A2B3C4D5E",
                "12/09/2026",
                List.of(),
                new BigDecimal("9.9")
        );
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/alumnos -> 200 con la lista de alumnos")
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
        @DisplayName("GET /api/alumnos/{id} -> 200 con el alumno")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/alumnos -> 201 con el alumno creado")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(AlumnoRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));

            verify(service).registrar(any(AlumnoRequest.class));
        }

        @Test
        @DisplayName("PUT /api/alumnos/{id} -> 200 con el alumno actualizado")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(AlumnoRequest.class), eq(1L))).thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(service).actualizar(any(AlumnoRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/alumnos/{id} -> 204 sin contenido")
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
        @DisplayName("GET /api/alumnos/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/alumnos con body inválido -> 400 y no llama al service")
        void registrar_bodyInvalido_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
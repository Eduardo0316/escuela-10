package com.eduardo.escuela.controller;

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

import com.eduardo.escuela.dto.datos.DatosInscripcion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.escuela.dto.calificaciones.CalificacionRequest;
import com.eduardo.escuela.dto.calificaciones.CalificacionResponse;
import com.eduardo.escuela.exceptions.GlobalExceptionHandler;
import com.eduardo.escuela.services.calificaciones.CalificacionService;

import tools.jackson.databind.ObjectMapper;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(CalificacionController.class)
class CalificacionControllerTest {

    private static final String BASE_URL = "/api/calificaciones";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalificacionService service;

    // ============================ DATOS DE PRUEBA ============================
    private CalificacionRequest request() {
        return new CalificacionRequest(2L, new BigDecimal("6.7"));
    }

    private CalificacionResponse response(Long id) {
        DatosInscripcion inscripcion = new DatosInscripcion(null, null, "07/10/2026");
        return new CalificacionResponse(id, inscripcion, new BigDecimal("6.7"), "07/10/2026");
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/calificaciones -> 200 con la lista de calificaciones")
        void listar_devuelve200() throws Exception {
            when(service.listar()).thenReturn(List.of(response(1L), response(2L)));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[0].calificacion").value(6.7))
                    .andExpect(jsonPath("$[0].fechaRegistro").value("07/10/2026"));

            verify(service).listar();
        }

        @Test
        @DisplayName("GET /api/calificaciones/{id} -> 200 con la calificación")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.calificacion").value(6.7))
                    .andExpect(jsonPath("$.fechaRegistro").value("07/10/2026"));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/calificaciones -> 201 con la calificación creada")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(CalificacionRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.calificacion").value(6.7));

            verify(service).registrar(any(CalificacionRequest.class));
        }

        @Test
        @DisplayName("PUT /api/calificaciones/{id} -> 200 con la calificación actualizada")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(CalificacionRequest.class), eq(1L))).thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.calificacion").value(6.7));

            verify(service).actualizar(any(CalificacionRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/calificaciones/{id} -> 204 sin contenido")
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
        @DisplayName("GET /api/calificaciones/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/calificaciones con body vacío (campos nulos) -> 400 y no llama al service")
        void registrar_bodyVacio_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/calificaciones con calificación negativa -> 400 y no llama al service")
        void registrar_calificacionNegativa_devuelve400() throws Exception {
            CalificacionRequest invalido = new CalificacionRequest(2L, new BigDecimal("-1"));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("PUT /api/calificaciones/{id} con idInscripcion negativo -> 400 y no llama al service")
        void actualizar_idInscripcionNegativo_devuelve400() throws Exception {
            CalificacionRequest invalido = new CalificacionRequest(-2L, new BigDecimal("6.7"));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
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

import com.eduardo.escuela.dto.datos.DatosAlumno;
import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.inscripciones.InscripcionRequest;
import com.eduardo.escuela.dto.inscripciones.InscripcionResponse;
import com.eduardo.escuela.services.inscripciones.InscripcionService;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(InscripcionController.class)
@DisplayName("Pruebas unitarias de InscripcionController")
class InscripcionControllerTest {

    private static final String BASE_URL = "/api/inscripcion";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InscripcionService service;

    // ====== Datos de prueba ======
    private InscripcionRequest request() {
        return new InscripcionRequest(6L, 7L);
    }

    private InscripcionResponse response(Long id) {
        return new InscripcionResponse(
                id,
                new DatosAlumno(
                        "Juanito Pérez López",
                        "1A2B3C4D5E",
                        "test@test.com",
                        "12/09/2026"
                ),
                new DatosGrupo(
                        "Matemáticas I",
                        "Laura Martínez Martínez",
                        "Aula 101",
                        "2025-01"
                ),
                new BigDecimal("8.8"),
                "07/10/2026"
        );
    }

    private InscripcionResponse responseSinCalificacion(Long id) {
        return new InscripcionResponse(
                id,
                new DatosAlumno("Juanito Pérez López", "1A2B3C4D5E",
                        "test@test.com", "12/09/2026"),
                new DatosGrupo("Matemáticas I", "Laura Martínez Martínez",
                        "Aula 101", "2025-01"),
                null,                   // <- sin calificación
                "07/10/2026"
        );
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/inscripcion -> 200 con la lista de inscripciones")
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
        @DisplayName("GET /api/inscripcion/{id} -> 200 con la inscripción")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.alumno.nombre").value("Juanito Pérez López"))
                    .andExpect(jsonPath("$.alumno.matricula").value("1A2B3C4D5E"))
                    .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                    .andExpect(jsonPath("$.grupo.periodo").value("2025-01"))
                    .andExpect(jsonPath("$.calificacion").value(8.8))
                    .andExpect(jsonPath("$.fechaInscripcion").value("07/10/2026"));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("GET /api/inscripcion/{id} sin calificación -> 200 con calificacion null")
        void obtenerPorId_sinCalificacion_devuelve200ConNull() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(responseSinCalificacion(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.calificacion").doesNotExist()); // Jackson omite null por defecto

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/inscripcion -> 201 con la inscripción creada")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(InscripcionRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));

            verify(service).registrar(any(InscripcionRequest.class));
        }

        @Test
        @DisplayName("PUT /api/inscripcion/{id} -> 200 con la inscripción actualizada")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(InscripcionRequest.class), eq(1L)))
                    .thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(service).actualizar(any(InscripcionRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/inscripcion/{id} -> 204 sin contenido")
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
        @DisplayName("GET /api/inscripcion/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/inscripcion con body vacío -> 400 y no llama al service")
        void registrar_bodyVacio_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/inscripcion con idAlumno negativo -> 400 y no llama al service")
        void registrar_idAlumnoNegativo_devuelve400() throws Exception {
            InscripcionRequest requestInvalido = new InscripcionRequest(-1L, 7L);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/inscripcion con idGrupo negativo -> 400 y no llama al service")
        void registrar_idGrupoNegativo_devuelve400() throws Exception {
            InscripcionRequest requestInvalido = new InscripcionRequest(6L, -1L);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("PUT /api/inscripcion/{id} con ID negativo -> 400 y no llama al service")
        void actualizar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(put(BASE_URL + "/{id}", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("DELETE /api/inscripcion/{id} con ID negativo -> 400 y no llama al service")
        void eliminar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
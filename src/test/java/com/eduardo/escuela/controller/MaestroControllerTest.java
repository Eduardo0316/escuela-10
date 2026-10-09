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

import com.eduardo.escuela.dto.datos.DatosCurso;
import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.services.maestros.MaestroService;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(MaestroController.class)
@DisplayName("Pruebas unitarias de MaestroController")
class MaestroControllerTest {

    private static final String BASE_URL = "/api/maestros";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaestroService service;

    // ====== Datos de prueba ======
    private MaestroRequest request() {
        return new MaestroRequest(
                "Máximo",
                "Juárez",
                "Pérez",
                "test@test.com",
                "2223334455"
        );
    }

    private MaestroResponse response(Long id) {
        return new MaestroResponse(
                id,
                "Máximo Juárez Pérez",
                "test@test.com",
                "2223334455",
                List.of(
                        new DatosCurso("Matemáticas I", "Curso de cálculo", 5),
                        new DatosCurso("Física I", "Curso de mecánica", 4)
                )
        );
    }

    private MaestroResponse responseSinCursos(Long id) {
        return new MaestroResponse(
                id,
                "Máximo Juárez Pérez",
                "test@test.com",
                "2223334455",
                List.of()  // <- sin cursos
        );
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/maestros -> 200 con la lista de maestros")
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
        @DisplayName("GET /api/maestros/{id} -> 200 con el maestro")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreCompleto").value("Máximo Juárez Pérez"))
                    .andExpect(jsonPath("$.email").value("test@test.com"))
                    .andExpect(jsonPath("$.telefono").value("2223334455"))
                    .andExpect(jsonPath("$.cursos.length()").value(2))
                    .andExpect(jsonPath("$.cursos[0].nombre").value("Matemáticas I"));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("GET /api/maestros/{id} sin cursos -> 200 con lista vacía")
        void obtenerPorId_sinCursos_devuelve200ConListaVacia() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(responseSinCursos(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.cursos.length()").value(0));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/maestros -> 201 con el maestro creado")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(MaestroRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreCompleto").value("Máximo Juárez Pérez"));

            verify(service).registrar(any(MaestroRequest.class));
        }

        @Test
        @DisplayName("PUT /api/maestros/{id} -> 200 con el maestro actualizado")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(MaestroRequest.class), eq(1L)))
                    .thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreCompleto").value("Máximo Juárez Pérez"));

            verify(service).actualizar(any(MaestroRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/maestros/{id} -> 204 sin contenido")
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
        @DisplayName("GET /api/maestros/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/maestros con body vacío -> 400 y no llama al service")
        void registrar_bodyVacio_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/maestros con email inválido -> 400 y no llama al service")
        void registrar_emailInvalido_devuelve400() throws Exception {
            MaestroRequest requestInvalido = new MaestroRequest(
                    "Máximo", "Juárez", "Pérez", "no-es-un-email", "2223334455");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/maestros con teléfono inválido -> 400 y no llama al service")
        void registrar_telefonoInvalido_devuelve400() throws Exception {
            MaestroRequest requestInvalido = new MaestroRequest(
                    "Máximo", "Juárez", "Pérez", "test@test.com", "123");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/maestros con nombre vacío -> 400 y no llama al service")
        void registrar_nombreVacio_devuelve400() throws Exception {
            MaestroRequest requestInvalido = new MaestroRequest(
                    "", "Juárez", "Pérez", "test@test.com", "2223334455");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("PUT /api/maestros/{id} con ID negativo -> 400 y no llama al service")
        void actualizar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(put(BASE_URL + "/{id}", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("DELETE /api/maestros/{id} con ID negativo -> 400 y no llama al service")
        void eliminar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
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

import java.time.LocalTime;
import java.util.List;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.horarios.HorarioRequest;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.services.horarios.HorarioService;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(HorarioController.class)
@DisplayName("Pruebas unitarias de HorarioController")
class HorarioControllerTest {

    private static final String BASE_URL = "/api/horario";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HorarioService service;

    // ====== Datos de prueba ======
    private HorarioRequest request() {
        return new HorarioRequest(
                1L,
                "Lunes",
                LocalTime.of(8, 0),
                LocalTime.of(10, 0)
        );
    }

    private HorarioResponse response(Long id) {
        return new HorarioResponse(
                id,
                new DatosGrupo(
                        "Matemáticas I",
                        "Laura Martínez Martínez",
                        "Aula 101",
                        "2025-01"
                ),
                "Lunes 08:00 - 10:00"
        );
    }

    // ============================ HAPPY PATH ============================
    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("GET /api/horario -> 200 con la lista de horarios")
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
        @DisplayName("GET /api/horario/{id} -> 200 con el horario")
        void obtenerPorId_devuelve200() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"))
                    .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                    .andExpect(jsonPath("$.grupo.maestro").value("Laura Martínez Martínez"))
                    .andExpect(jsonPath("$.grupo.aula").value("Aula 101"))
                    .andExpect(jsonPath("$.grupo.periodo").value("2025-01"));

            verify(service).obtenerPorId(1L);
        }

        @Test
        @DisplayName("POST /api/horario -> 201 con el horario creado")
        void registrar_devuelve201() throws Exception {
            when(service.registrar(any(HorarioRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));

            verify(service).registrar(any(HorarioRequest.class));
        }

        @Test
        @DisplayName("POST /api/horario con JSON manual de horas -> 201 (verifica deserialización de LocalTime)")
        void registrar_conJsonDeHoras_debeDeserializarCorrectamente() throws Exception {
            // Este test valida que el ObjectMapper convierte "08:00" a LocalTime
            String json = """
                    {
                      "idGrupo": 1,
                      "dia": "Lunes",
                      "horaInicio": "08:00",
                      "horaFin": "10:00"
                    }
                    """;

            when(service.registrar(any(HorarioRequest.class))).thenReturn(response(1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));

            verify(service).registrar(any(HorarioRequest.class));
        }

        @Test
        @DisplayName("PUT /api/horario/{id} -> 200 con el horario actualizado")
        void actualizar_devuelve200() throws Exception {
            when(service.actualizar(any(HorarioRequest.class), eq(1L)))
                    .thenReturn(response(1L));

            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));

            verify(service).actualizar(any(HorarioRequest.class), eq(1L));
        }

        @Test
        @DisplayName("DELETE /api/horario/{id} -> 204 sin contenido")
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
        @DisplayName("GET /api/horario/{id} con ID negativo -> 400 y no llama al service")
        void obtenerPorId_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/horario con body vacío -> 400 y no llama al service")
        void registrar_bodyVacio_devuelve400() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/horario con idGrupo negativo -> 400 y no llama al service")
        void registrar_idGrupoNegativo_devuelve400() throws Exception {
            HorarioRequest requestInvalido = new HorarioRequest(
                    -1L, "Lunes", LocalTime.of(8, 0), LocalTime.of(10, 0));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/horario con día vacío -> 400 y no llama al service")
        void registrar_diaVacio_devuelve400() throws Exception {
            HorarioRequest requestInvalido = new HorarioRequest(
                    1L, "", LocalTime.of(8, 0), LocalTime.of(10, 0));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("POST /api/horario con horaInicio null -> 400 y no llama al service")
        void registrar_horaInicioNull_devuelve400() throws Exception {
            HorarioRequest requestInvalido = new HorarioRequest(
                    1L, "Lunes", null, LocalTime.of(10, 0));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("PUT /api/horario/{id} con ID negativo -> 400 y no llama al service")
        void actualizar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(put(BASE_URL + "/{id}", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request())))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("DELETE /api/horario/{id} con ID negativo -> 400 y no llama al service")
        void eliminar_idNegativo_devuelve400() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}
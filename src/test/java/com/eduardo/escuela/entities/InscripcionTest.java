package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InscripcionTest {

    private Alumno alumnoMock;
    private Grupo grupoMock;

    @BeforeEach
    void setUp() {
        alumnoMock = Mockito.mock(Alumno.class);
        grupoMock = Mockito.mock(Grupo.class);
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea una inscripción válida con alumno y grupo correctos")
        void crearInscripcion_Exitoso() {
            Inscripcion inscripcion = Inscripcion.crear(alumnoMock, grupoMock);

            assertNotNull(inscripcion);
            assertEquals(alumnoMock, inscripcion.getAlumno());
            assertEquals(grupoMock, inscripcion.getGrupo());
            assertNull(inscripcion.getCalificacion());
            assertEquals(LocalDate.now(), inscripcion.getFechaInscripcion());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el alumno es nulo")
        void crearInscripcion_AlumnoNulo_LanzaRecursoNoEncontradoException() {
            RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () ->
                    Inscripcion.crear(null, grupoMock)
            );

            assertEquals("El curso, el maestro y el aula son requeridos", ex.getMessage());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el grupo es nulo")
        void crearInscripcion_GrupoNulo_LanzaRecursoNoEncontradoException() {
            RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () ->
                    Inscripcion.crear(alumnoMock, null)
            );

            assertEquals("El curso, el maestro y el aula son requeridos", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza correctamente el alumno y el grupo de la inscripción")
        void actualizarInscripcion_Exitoso() {
            Inscripcion inscripcion = Inscripcion.crear(alumnoMock, grupoMock);

            Alumno nuevoAlumnoMock = Mockito.mock(Alumno.class);
            Grupo nuevoGrupoMock = Mockito.mock(Grupo.class);

            inscripcion.actualizar(nuevoAlumnoMock, nuevoGrupoMock);

            assertEquals(nuevoAlumnoMock, inscripcion.getAlumno());
            assertEquals(nuevoGrupoMock, inscripcion.getGrupo());
        }

        @Test
        @DisplayName("Unhappy Path: Falla al intentar actualizar enviando alguna relación nula")
        void actualizarInscripcion_ParametroNulo_LanzaRecursoNoEncontradoException() {
            Inscripcion inscripcion = Inscripcion.crear(alumnoMock, grupoMock);

            assertThrows(RecursoNoEncontradoException.class, () ->
                    inscripcion.actualizar(alumnoMock, null)
            );

            // Verifica que el estado previo de la entidad permanezca intacto
            assertEquals(grupoMock, inscripcion.getGrupo());
        }
    }
}
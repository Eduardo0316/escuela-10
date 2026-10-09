package com.eduardo.escuela.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AlumnoTest {

    private Alumno alumnoBase;

    @BeforeEach
    void setUp() {
        alumnoBase = Alumno.crear("Juan", "Pérez", "Gómez");
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea un alumno correctamente con datos válidos")
        void crearAlumno_Exitoso() {
            Alumno alumno = Alumno.crear("  Carlos ", "López ", "Martínez ");

            assertNotNull(alumno);
            assertEquals("Carlos", alumno.getNombre());
            assertEquals("López", alumno.getApellidoPaterno());
            assertEquals("Martínez", alumno.getApellidoMaterno());
            assertNotNull(alumno.getFechaIngreso());
            assertNotNull(alumno.getInscripciones());
            assertTrue(alumno.getInscripciones().isEmpty());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si un nombre excede el tamaño permitido o es nulo/vacío")
        void crearAlumno_NombreInvalido_LanzaExcepcion() {
            String nombreInvalido = "A".repeat(51);

            assertThrows(Exception.class, () -> 
                Alumno.crear(nombreInvalido, "Pérez", "Gómez")
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para asignarDatosAcademicos()")
    class AsignarDatosAcademicosTests {

        @Test
        @DisplayName("Happy Path: Asigna email en minúsculas y matrícula formateada")
        void asignarDatosAcademicos_Exitoso() {
            alumnoBase.asignarDatosAcademicos(" JUAN.PEREZ@EMAIL.COM ", "1234567890");

            assertEquals("juan.perez@email.com", alumnoBase.getEmail());
            assertEquals("1234567890", alumnoBase.getMatricula());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si la matrícula no tiene exactamente 10 caracteres")
        void asignarDatosAcademicos_MatriculaInvalida_LanzaExcepcion() {
            assertThrows(Exception.class, () -> 
                alumnoBase.asignarDatosAcademicos("juan@email.com", "12345")
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para cambioEnDatos()")
    class CambioEnDatosTests {

        @Test
        @DisplayName("Happy Path: Retorna true si al menos un dato difiere")
        void cambioEnDatos_Difiere_RetornaTrue() {
            boolean cambio = alumnoBase.cambioEnDatos("Juan", "Pérez", "López");

            assertTrue(cambio);
        }

        @Test
        @DisplayName("Happy Path: Retorna false si todos los datos son idénticos")
        void cambioEnDatos_Identicos_RetornaFalse() {
            boolean cambio = alumnoBase.cambioEnDatos("Juan", "Pérez", "Gómez");

            assertFalse(cambio);
        }
    }

    @Nested
    @DisplayName("Pruebas para actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza todos los campos de la entidad de forma correcta")
        void actualizar_Exitoso() {
            alumnoBase.actualizar(" Mario ", " Silva ", " Díaz ", " MARIO@TEST.COM ", " 0987654321 ");

            assertEquals("Mario", alumnoBase.getNombre());
            assertEquals("Silva", alumnoBase.getApellidoPaterno());
            assertEquals("Díaz", alumnoBase.getApellidoMaterno());
            assertEquals("mario@test.com", alumnoBase.getEmail());
            assertEquals("0987654321", alumnoBase.getMatricula());
        }
    }

    @Nested
    @DisplayName("Pruebas para calcularPromedio()")
    class CalcularPromedioTests {

        @Test
        @DisplayName("Happy Path: Calcula el promedio correctamente redondeando HALF_UP")
        void calcularPromedio_ConCalificaciones_CalculaPromedioCorrecto() {
            // Mock de Calificaciones
            Calificacion cal1 = Mockito.mock(Calificacion.class);
            when(cal1.getCalificacion()).thenReturn(new BigDecimal("8.50"));

            Calificacion cal2 = Mockito.mock(Calificacion.class);
            when(cal2.getCalificacion()).thenReturn(new BigDecimal("9.00"));

            // Mock de Inscripciones
            Inscripcion ins1 = Mockito.mock(Inscripcion.class);
            when(ins1.getCalificacion()).thenReturn(cal1);

            Inscripcion ins2 = Mockito.mock(Inscripcion.class);
            when(ins2.getCalificacion()).thenReturn(cal2);

            List<Inscripcion> inscripcionesMock = List.of(ins1, ins2);

            Alumno alumnoConInscripciones = Alumno.builder()
                    .nombre("Ana")
                    .apellidoPaterno("Ruiz")
                    .apellidoMaterno("Soto")
                    .inscripciones(inscripcionesMock)
                    .build();

            BigDecimal promedio = alumnoConInscripciones.calcularPromedio();

            assertEquals(new BigDecimal("8.75"), promedio);
        }

        @Test
        @DisplayName("Unhappy Path / Edge Case: Retorna BigDecimal.ZERO si no existen inscripciones o calificaciones")
        void calcularPromedio_SinCalificaciones_RetornaCero() {
            BigDecimal promedio = alumnoBase.calcularPromedio();

            assertEquals(BigDecimal.ZERO, promedio);
        }
    }
}
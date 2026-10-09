package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoTest {

    @Mock
    private Inscripcion inscripcion1;

    @Mock
    private Inscripcion inscripcion2;

    @Mock
    private Calificacion calificacion1;

    @Mock
    private Calificacion calificacion2;

    private Alumno alumno;

    @BeforeEach
    void setUp() {
        alumno = Alumno.crear("Juan", "Pérez", "Gómez");
    }

    @Nested
    @DisplayName("Método Factory: crear")
    class CrearTest {

        @Test
        @DisplayName("Happy Path: Crea un alumno correctamente recortando espacios")
        void crearAlumno_HappyPath() {
            Alumno nuevoAlumno = Alumno.crear("  Carlos ", " López ", " Martínez ");

            assertThat(nuevoAlumno.getNombre()).isEqualTo("Carlos");
            assertThat(nuevoAlumno.getApellidoPaterno()).isEqualTo("López");
            assertThat(nuevoAlumno.getApellidoMaterno()).isEqualTo("Martínez");
            assertThat(nuevoAlumno.getFechaIngreso()).isNotNull();
            assertThat(nuevoAlumno.getInscripciones()).isEmpty();
        }

        @Test
        @DisplayName("Unhappy Path: Falla al crear si el nombre es nulo o inválido")
        void crearAlumno_NombreInvalido_ThrowsException() {
            assertThatThrownBy(() -> Alumno.crear("", "Pérez", "Gómez"))
                    .isInstanceOf(DatoInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("Método: cambioEnDatos")
    class CambioEnDatosTest {

        @Test
        @DisplayName("Happy Path: Detecta si hay cambios en los datos personales")
        void cambioEnDatos_DetectaCambios_HappyPath() {
            boolean hayCambio = alumno.cambioEnDatos("Juan", "Pérez", "Mendoza");

            assertThat(hayCambio).isTrue();
        }

        @Test
        @DisplayName("Happy Path: Devuelve false cuando los datos son exactamente iguales")
        void cambioEnDatos_SinCambios_HappyPath() {
            boolean hayCambio = alumno.cambioEnDatos("Juan", "Pérez", "Gómez");

            assertThat(hayCambio).isFalse();
        }
    }

    @Nested
    @DisplayName("Método: asignarDatosAcademicos")
    class AsignarDatosAcademicosTest {

        @Test
        @DisplayName("Happy Path: Asigna email y matrícula normalizando texto")
        void asignarDatosAcademicos_HappyPath() {
            alumno.asignarDatosAcademicos(" JUAN.PEREZ@EMAIL.COM ", "1234567890");

            assertThat(alumno.getEmail()).isEqualTo("juan.perez@email.com");
            assertThat(alumno.getMatricula()).isEqualTo("1234567890");
        }

        @Test
        @DisplayName("Unhappy Path: Falla si la matrícula no tiene exactamente 10 caracteres")
        void asignarDatosAcademicos_MatriculaLongitudInvalida_ThrowsException() {
            assertThatThrownBy(() -> alumno.asignarDatosAcademicos("juan@email.com", "12345"))
                    .isInstanceOf(DatoInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("Método: calcularPromedio")
    class CalcularPromedioTest {

        @Test
        @DisplayName("Happy Path: Calcula el promedio correctamente con redondeo HALF_UP")
        void calcularPromedio_ConCalificaciones_HappyPath() {
            // Configurar mocks de calificaciones
            when(calificacion1.getCalificacion()).thenReturn(new BigDecimal("8.5"));
            when(calificacion2.getCalificacion()).thenReturn(new BigDecimal("9.0"));

            when(inscripcion1.getCalificacion()).thenReturn(calificacion1);
            when(inscripcion2.getCalificacion()).thenReturn(calificacion2);

            alumno.getInscripciones().addAll(List.of(inscripcion1, inscripcion2));

            BigDecimal promedio = alumno.calcularPromedio();

            // (8.5 + 9.0) / 2 = 8.75 -> HALF_UP = 8.8
            assertThat(promedio).isEqualTo(new BigDecimal("8.8"));
        }

        @Test
        @DisplayName("Happy Path / Edge Case: Retorna BigDecimal.ZERO si no hay inscripciones")
        void calcularPromedio_SinInscripciones_ReturnsZero() {
            BigDecimal promedio = alumno.calcularPromedio();

            assertThat(promedio).isEqualTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Método: obtenerNombreCompletoAlumno")
    class NombreCompletoTest {

        @Test
        @DisplayName("Happy Path: Retorna el nombre completo concatenado por espacios")
        void obtenerNombreCompletoAlumno_HappyPath() {
            String nombreCompleto = alumno.obtenerNombreCompletoAlumno();

            assertThat(nombreCompleto).isEqualTo("Juan Pérez Gómez");
        }
    }
}
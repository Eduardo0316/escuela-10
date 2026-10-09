package com.eduardo.escuela.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias de la entidad Maestro")
class MaestroTest {

    private String nombreValido;
    private String apellidoPaternoValido;
    private String apellidoMaternoValido;
    private String emailValido;
    private String telefonoValido;

    @BeforeEach
    void setUp() {
        nombreValido = "Eduardo";
        apellidoPaternoValido = "García";
        apellidoMaternoValido = "López";
        emailValido = "eduardo@escuela.com";
        telefonoValido = "5512345678";
    }

    // ============================================================
    // HAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe construir un Maestro correctamente con datos válidos")
    void crear_conDatosValidos_debeConstruirMaestro() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                emailValido, telefonoValido);

        assertNotNull(maestro);
        assertEquals("Eduardo", maestro.getNombre());
        assertEquals("García", maestro.getApellidoPaterno());
        assertEquals("López", maestro.getApellidoMaterno());
        assertEquals("eduardo@escuela.com", maestro.getEmail());
        assertEquals("5512345678", maestro.getTelefono());
    }

    @Test
    @DisplayName("crear: debe recortar espacios en blanco de todos los campos")
    void crear_conEspaciosEnBlanco_debeRecortarlos() {
        Maestro maestro = Maestro.crear(
                "  Eduardo  ", "  García  ", "  López  ",
                "  eduardo@escuela.com  ", "  5512345678  ");

        assertEquals("Eduardo", maestro.getNombre());
        assertEquals("García", maestro.getApellidoPaterno());
        assertEquals("López", maestro.getApellidoMaterno());
        assertEquals("eduardo@escuela.com", maestro.getEmail());
        assertEquals("5512345678", maestro.getTelefono());
    }

    @Test
    @DisplayName("crear: debe convertir el email a minúsculas")
    void crear_conEmailEnMayusculas_debeConvertirloAMinusculas() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                "EDUARDO@ESCUELA.COM", telefonoValido);

        assertEquals("eduardo@escuela.com", maestro.getEmail());
    }

    @Test
    @DisplayName("crear: debe inicializar la lista de grupos vacía")
    void crear_debeInicializarListaDeGruposVacia() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                emailValido, telefonoValido);

        assertNotNull(maestro.getGrupos());
        assertTrue(maestro.getGrupos().isEmpty());
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe modificar todos los campos correctamente")
    void actualizar_conDatosValidos_debeActualizarCampos() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                emailValido, telefonoValido);

        maestro.actualizar("Ana", "Martínez", "Ruiz",
                "ANA@ESCUELA.COM", "5598765432");

        assertEquals("Ana", maestro.getNombre());
        assertEquals("Martínez", maestro.getApellidoPaterno());
        assertEquals("Ruiz", maestro.getApellidoMaterno());
        assertEquals("ana@escuela.com", maestro.getEmail());
        assertEquals("5598765432", maestro.getTelefono());
    }

    // ============================================================
    // HAPPY PATHS - obtenerNombreCompletoMaestro()
    // ============================================================

    @Test
    @DisplayName("obtenerNombreCompletoMaestro: debe concatenar nombre y apellidos")
    void obtenerNombreCompletoMaestro_debeConcatenarCampos() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                emailValido, telefonoValido);

        String nombreCompleto = maestro.obtenerNombreCompletoMaestro();

        assertNotNull(nombreCompleto);
        assertEquals("Eduardo García López", nombreCompleto);
    }

    // ============================================================
    // UNHAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe lanzar excepción si el nombre es null")
    void crear_conNombreNull_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Maestro.crear(null, apellidoPaternoValido, apellidoMaternoValido,
                        emailValido, telefonoValido)
        );
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el email es demasiado corto")
    void crear_conEmailDemasiadoCorto_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Maestro.crear(nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                        "a@b.com", telefonoValido) // 7 caracteres
        );
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el teléfono no tiene exactamente 10 dígitos")
    void crear_conTelefonoLongitudInvalida_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Maestro.crear(nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                        emailValido, "551234567") // 9 dígitos
        );
        assertThrows(
                DatoInvalidoException.class,
                () -> Maestro.crear(nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                        emailValido, "55123456789") // 11 dígitos
        );
    }

    // ============================================================
    // UNHAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el apellido paterno es null")
    void actualizar_conApellidoPaternoNull_debeLanzarExcepcion() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                emailValido, telefonoValido);

        assertThrows(
                DatoInvalidoException.class,
                () -> maestro.actualizar(nombreValido, null, apellidoMaternoValido,
                        emailValido, telefonoValido)
        );
    }

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el teléfono no tiene 10 dígitos")
    void actualizar_conTelefonoInvalido_debeLanzarExcepcion() {
        Maestro maestro = Maestro.crear(
                nombreValido, apellidoPaternoValido, apellidoMaternoValido,
                emailValido, telefonoValido);

        assertThrows(
                DatoInvalidoException.class,
                () -> maestro.actualizar("Ana", "Martínez", "Ruiz",
                        "ana@escuela.com", "123")
        );
    }
}
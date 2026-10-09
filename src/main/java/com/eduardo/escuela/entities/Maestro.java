package com.eduardo.escuela.entities;

import java.util.ArrayList;
import java.util.List;

import com.eduardo.escuela.utils.StringCustomUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "MAESTROS")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Builder  
public class Maestro {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MAESTRO") 
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;
    
    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;
    
    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;
    
    @Column(name = "TELEFONO", nullable = false, length = 10, unique = true)
    private String telefono;

    @Builder.Default
    @OneToMany(mappedBy = "maestro", fetch = FetchType.LAZY)
    private List<Grupo> grupos = new ArrayList<>();

    private static void validarDatos(String nombre, String apellidoPaterno, 
        String apellidoMaterno, String email, String telefono){
        
        StringCustomUtils.validarTamanio(nombre, 1, 50, 
            "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50, 
            "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50, 
            "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        
        StringCustomUtils.validarTamanio(email, 8, 100, 
            "El email es requerido y debe tener entre 8 y 100 caracteres");
        
        StringCustomUtils.validarTamanio(telefono, 10, 10, 
            "El telefono es requerido y debe tener exactamente 10 digitos");
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                        String email, String telefono) {

        String nombreL = nombre == null ? null : nombre.trim();
        String apPaternoL = apellidoPaterno == null ? null : apellidoPaterno.trim();
        String apMaternoL = apellidoMaterno == null ? null : apellidoMaterno.trim();
        String emailL = email == null ? null : email.trim().toLowerCase();
        String telefonoL = telefono == null ? null : telefono.trim();

        validarDatos(nombreL, apPaternoL, apMaternoL, emailL, telefonoL);

        this.nombre = nombreL;
        this.apellidoPaterno = apPaternoL;
        this.apellidoMaterno = apMaternoL;
        this.email = emailL;
        this.telefono = telefonoL;
    }

    public static Maestro crear(String nombre, String apellidoPaterno, String apellidoMaterno,
                                String email, String telefono) {

        String nombreL = nombre == null ? null : nombre.trim();
        String apPaternoL = apellidoPaterno == null ? null : apellidoPaterno.trim();
        String apMaternoL = apellidoMaterno == null ? null : apellidoMaterno.trim();
        String emailL = email == null ? null : email.trim().toLowerCase();
        String telefonoL = telefono == null ? null : telefono.trim();

        validarDatos(nombreL, apPaternoL, apMaternoL, emailL, telefonoL);

        return Maestro.builder()
                .nombre(nombreL)
                .apellidoPaterno(apPaternoL)
                .apellidoMaterno(apMaternoL)
                .email(emailL)
                .telefono(telefonoL)
                .build();
    }

    public String obtenerNombreCompletoMaestro(){
        return String.join(" ", nombre, apellidoPaterno, apellidoMaterno);
    }
}

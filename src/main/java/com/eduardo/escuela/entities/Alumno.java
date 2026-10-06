package com.eduardo.escuela.entities;

import java.time.LocalDate;
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
@Table(name = "ALUMNOS")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Builder 
public class Alumno {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALUMNO")
    private Long id;
    
    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;
    
    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;
    
    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;
    
    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;
    
    @Column(name = "MATRICULA", nullable = false, length = 10, unique = true)
    private String matricula;

    @Builder.Default
    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso = LocalDate.now();

    @Builder.Default
    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String matricula) {
        StringCustomUtils.validarTamanio(nombre, 1, 50, 
            "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50, 
            "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50, 
            "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        
        StringCustomUtils.validarTamanio(email, 1, 100, 
            "El email es requerido y debe tener entre 1 y 100 caracteres");
        
        StringCustomUtils.validarTamanio(matricula, 1, 100, 
            "La matricula es requerida y debe tener entre 1 y 10 caracteres");
    }
    
    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String matricula) {
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, email, matricula);

        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.email = email;
        this.matricula = matricula;
    }

    public static Alumno crear(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String matricula){
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, email, matricula);

        return Alumno.builder()
            .nombre(nombre.trim())
            .apellidoPaterno(apellidoPaterno.trim())
            .apellidoMaterno(apellidoMaterno.trim())
            .email(email.toLowerCase())
            .matricula(matricula)
            .fechaIngreso(LocalDate.now())
            .build();
    }
}

package com.eduardo.escuela.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.eduardo.escuela.exceptions.DatoInvalidoException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(
    name = "INSCRIPCIONES",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "INSCRIPCION_ALU_GRU_UK", // Nombre de la restricción en la BD
            columnNames = {"ID_ALUMNO", "ID_GRUPO"} // Columnas que componen la clave única
        )
    }
)
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Builder 
public class Inscripcion {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSCRIPCION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALUMNO", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Builder.Default
    @Column(name = "FECHA_INSCRIPCION")
    private LocalDate fechaInscripcion = LocalDate.now();

    @OneToOne(mappedBy = "inscripcion")
    private Calificacion calificacion;

    public static Inscripcion crear(){
        return Inscripcion.builder()
                .fechaInscripcion(LocalDate.now())
                .build();
    }

    public void asignarGrupo(Grupo grupo){
        if(grupo == null)
            throw new DatoInvalidoException("El grupo es requerido");

        this.grupo = grupo;
    }

    public void asignarAlumno(Alumno alumno){
        if(alumno == null)
            throw new DatoInvalidoException("El alumno es requerido");
        
        this.alumno = alumno;
    }
}

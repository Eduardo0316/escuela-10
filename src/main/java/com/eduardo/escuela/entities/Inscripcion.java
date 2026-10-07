package com.eduardo.escuela.entities;

import java.time.LocalDate;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;

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
    @OneToOne(mappedBy = "inscripcion")
    private Calificacion calificacion = null;

    @Builder.Default
    @Column(name = "FECHA_INSCRIPCION")
    private LocalDate fechaInscripcion = LocalDate.now();

    public static Inscripcion crear(Alumno alumno, Grupo grupo){
        validarRelaciones(alumno, grupo);
        return Inscripcion.builder()
                .alumno(alumno)
                .grupo(grupo)
                .build();
    }

    public void actualizar(Alumno alumno, Grupo grupo){
        validarRelaciones(alumno, grupo);
        this.alumno = alumno;
        this.grupo = grupo;
    }

    private static void validarRelaciones(Alumno alumno, Grupo grupo){
        if(alumno == null || grupo == null)
            throw new RecursoNoEncontradoException("El curso, el maestro y el aula son requeridos");
    }
}

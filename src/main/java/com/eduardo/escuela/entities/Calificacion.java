package com.eduardo.escuela.entities;

import java.math.BigDecimal;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "CALIFICACIONES")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Builder 
public class Calificacion {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CALIFICACION")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSCRIPCION", unique = true, nullable = false)
    private Inscripcion inscripcion;

    @Column(name = "CALIFICACION", nullable = false)
    private BigDecimal calificacion;

    @Builder.Default 
    @Column(name = "FECHA_REGISTRO")
    private LocalDate fechaRegistro = LocalDate.now();

    public static Calificacion crear(Inscripcion inscripcion, BigDecimal calificacion){
        validarDatos(inscripcion, calificacion);
        return Calificacion.builder()
            .inscripcion(inscripcion)
            .calificacion(calificacion)
            .build();
    }

    public void actualizar(Inscripcion inscripcion, BigDecimal calificacion){
        validarDatos(inscripcion, calificacion);
        this.inscripcion = inscripcion;
        this.calificacion = calificacion;
    }

    private static void validarDatos(Inscripcion inscripcion, BigDecimal calificacion){
        if(
            calificacion.compareTo(BigDecimal.TEN) > 0 ||
            calificacion.compareTo(BigDecimal.ZERO) < 0
        )
            throw new DatoInvalidoException("La calificacion debe ser positiva y estar entre 0 y 10");

        if(inscripcion == null)
            throw new RecursoNoEncontradoException("La inscripcion es necesaria");
    }
}

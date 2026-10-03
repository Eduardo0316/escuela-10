package com.eduardo.escuela.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(
    name = "GRUPOS",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "GRUPO_CU_MA_AU_PE_UK", // Nombre de la restricción en la BD
            columnNames = {"ID_CURSO", "ID_MAESTRO", "ID_AULA", "PERIODO"} // Columnas que componen la clave única
        )
    }
)
@AllArgsConstructor 
@NoArgsConstructor 
@Builder @Getter 
public class Grupo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSO", nullable = false)
    private Curso curso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAESTRO", nullable = false)
    private Maestro maestro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AULA", nullable = false)
    private Aula aula;

    @Column(name = "PERIODO", length = 20, nullable = false)
    private String periodo;
}

package com.eduardo.escuela.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "HORARIOS")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Builder 
public class Horario {
    @Id 
    @Column(name = "ID_HORARIO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Column(name = "DIA", nullable = false, length = 15)
    private String dia;
    
    @Column(name = "HORA_INICIO", nullable = false, length = 5)
    private String horaInicio;
    
    @Column(name = "HORA_FIN", nullable = false, length = 5)
    private String horaFin;
}

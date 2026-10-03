package com.eduardo.escuela.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "CURSOS")
@AllArgsConstructor 
@NoArgsConstructor 
@Builder @Getter 
public class Curso {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CURSO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100, unique = true)
    private String nombre;
    
    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
    
    @Column(name = "CREDITOS", nullable = false)
    private Integer creditos;
}

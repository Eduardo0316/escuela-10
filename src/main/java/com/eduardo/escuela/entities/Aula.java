package com.eduardo.escuela.entities;

import com.eduardo.escuela.utils.StringCustomUtils;
import com.eduardo.escuela.utils.ValoresNumericos;

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
@Table(name = "AULAS")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Builder 
public class Aula {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AULA")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    
    public static void validarDatos(String nombre, Integer capacidad) {
        StringCustomUtils.validarTamanio(nombre, 1, 100, "El nombre de aula es requerido y debe tener entre 1 y 100 caracteres");
        
        ValoresNumericos.validarEnteroPositivo(capacidad, "La capacidad es requerida y debe ser positiva");
    }


    public void actualizar(String nombre, Integer capacidad) {
        validarDatos(nombre, capacidad);

        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public static Aula crear(String nombre, Integer capacidad){
        validarDatos(nombre, capacidad);

        return Aula.builder()
            .nombre(nombre.trim())
            .capacidad(capacidad)
            .build();
    }
}

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

    private static void validarDatos(String nombre, Integer creditos){
        StringCustomUtils.validarTamanio(nombre, 1, 100, "En nombre debe tener entre 1 y 100 caracteres");
        ValoresNumericos.validarEnteroPositivo(creditos, "Los créditos son requeridos y deben ser positivos");
    }

    public static Curso crear(String nombre, String descripcion, Integer creditos){
        validarDatos(nombre, creditos);

        return Curso.builder()
            .nombre(nombre.trim())
            .descripcion(descripcion == null ? null : descripcion.trim())
            .creditos(creditos)
            .build();
    }

    public void actualizar(String nombre, String descripcion, Integer creditos){
        validarDatos(nombre, creditos);

        this.nombre = nombre;
        this.descripcion = descripcion == null ? descripcion : descripcion;
        this.creditos = creditos;
    }
}

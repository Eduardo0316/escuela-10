package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.utils.StringCustomUtils;

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

    private static void validarDatos(String periodo){
        StringCustomUtils.validarTamanio(periodo, 10, 20,
        "El periodo es requerido y debe tener entre 10 y 20 caracteres"
        );
    }

    public static Grupo crear(String periodo){
        validarDatos(periodo);
        return Grupo.builder()
                .periodo(periodo)
                .build();
    }

    public void asignarMaestro(Maestro maestro){
        if (maestro == null)
            throw new DatoInvalidoException("El maestro es requerido");

        this.maestro = maestro;
    }

    public void asignarAula(Aula aula){
        if (aula == null)
            throw new DatoInvalidoException("El aula es requerida");

        this.aula = aula;
    }

    public void asignarCurso(Curso curso){
        if (curso == null)
            throw new DatoInvalidoException("El curso es requerido");

        this.curso = curso;
    }
}

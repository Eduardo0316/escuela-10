package com.eduardo.escuela.entities;

import java.util.ArrayList;
import java.util.List;

import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.utils.StringCustomUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "grupo")
    @Builder.Default
    private List<Horario> horarios = new ArrayList<>();

    public static Grupo crear(Curso curso, Maestro maestro, Aula aula, String periodo) {
        validarRelaciones(curso, maestro, aula);
        validarPeriodo(periodo);
        return Grupo.builder()
                .curso(curso)
                .maestro(maestro)
                .aula(aula)
                .periodo(periodo)
                .build();
    }

    public void actualizar(Curso curso, Maestro maestro, Aula aula, String periodo) {
        validarRelaciones(curso, maestro, aula);
        validarPeriodo(periodo);
        this.curso = curso;
        this.maestro = maestro;
        this.aula = aula;
        this.periodo = periodo;
    }

    private static void validarPeriodo(String periodo) {
        StringCustomUtils.validarTamanio(periodo, 1, 20,
            "El periodo es requerido y debe tener entre 1 y 20 caracteres");
    }

    private static void validarRelaciones(Curso curso, Maestro maestro, Aula aula) {
        if (curso == null)
            throw new RecursoNoEncontradoException("El curso es requerido");
        if (maestro == null)
            throw new RecursoNoEncontradoException("El maestro es requerido");
        if (aula == null)
            throw new RecursoNoEncontradoException("El alua es requerida");
    }
}

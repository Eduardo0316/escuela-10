package com.eduardo.escuela.entities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.eduardo.escuela.converter.LocalTimeStringConverter;
import com.eduardo.escuela.enums.DiaSemana;
import com.eduardo.escuela.exceptions.DatoInvalidoException;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Enumerated(EnumType.STRING)
    @Column(name = "DIA", nullable = false, length = 15)
    private DiaSemana dia;
    
    @Convert(converter = LocalTimeStringConverter.class)
    @Column(name = "HORA_INICIO", length = 5, nullable = false)
    private LocalTime horaInicio;

    @Convert(converter = LocalTimeStringConverter.class)
    @Column(name = "HORA_FIN", length = 5, nullable = false)
    private LocalTime horaFin;

    public static Horario crear(Grupo grupo, DiaSemana dia, LocalTime horaInicio, LocalTime horaFin) {
        validarDatos(grupo, dia, horaInicio, horaFin);
        return Horario.builder()
                .grupo(grupo).dia(dia)
                .horaInicio(horaInicio).horaFin(horaFin)
                .build();
    }

    public void actualizar(Grupo grupo, DiaSemana dia, LocalTime horaInicio, LocalTime horaFin) {
        validarDatos(grupo, dia, horaInicio, horaFin);
        this.grupo = grupo;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    private static void validarDatos(Grupo grupo, DiaSemana dia, LocalTime inicio, LocalTime fin) {
        if (grupo == null)
            throw new DatoInvalidoException("El grupo es requerido");
        if(dia == null)
            throw new DatoInvalidoException("El día es requerido");
        if(inicio == null)
            throw new DatoInvalidoException("La hora de inicio es requerida");
        if(fin == null)
            throw new DatoInvalidoException("La hora de fin es requerida");
        if (!fin.isAfter(inicio))
            throw new DatoInvalidoException("La hora de fin debe ser posterior a la hora de inicio");
    }

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static String entidadAHorarioFormateado(Horario horario) {
        if (horario == null) return null;
        return horario.getDia().getDescripcion() + " "
            + horario.getHoraInicio().format(HORA) + " - " + horario.getHoraFin().format(HORA);
    }
}

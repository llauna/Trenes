package com.david.trenes.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoHayPlazasException extends RuntimeException {
    private final String clase;
    private final String horarioActualId;

    private final String nextHorarioId;
    private final String nextCodigoServicio;
    private final LocalDateTime nextFechaSalida;

    public NoHayPlazasException(String message,
                                String clase,
                                String horarioActualId,
                                String nextHorarioId,
                                String nextCodigoServicio,
                                LocalDateTime nextFechaSalida) {
        super(message);
        this.clase = clase;
        this.horarioActualId = horarioActualId;
        this.nextHorarioId = nextHorarioId;
        this.nextCodigoServicio = nextCodigoServicio;
        this.nextFechaSalida = nextFechaSalida;
    }
}

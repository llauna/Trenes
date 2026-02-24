package com.david.trenes.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String SPANISH_DATE_FORMAT = "dd/MM/yyyy";
    public static final String SPANISH_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final DateTimeFormatter isoDateTimeFormatter = DateTimeFormatter.ofPattern(ISO_DATETIME_FORMAT);
    private static final DateTimeFormatter spanishDateFormatter = DateTimeFormatter.ofPattern(SPANISH_DATE_FORMAT);
    private static final DateTimeFormatter spanishDateTimeFormatter = DateTimeFormatter.ofPattern(SPANISH_DATETIME_FORMAT);

    /**
     * Parsea un parámetro de fecha/hora admitiendo:
     * - dd/MM/yyyy
     * - dd/MM/yyyy HH:mm:ss
     * - ISO: yyyy-MM-ddTHH:mm:ss
     * - ISO solo fecha: yyyy-MM-dd (-> 00:00:00)
     */
    public static LocalDateTime parseFlexibleDateTimeParam(String text) {
        if (text == null) return null;

        String value = text.trim();
        if (value.isEmpty()) return null;

        // Español flexible: dd/MM/yyyy [HH:mm[:ss]] (si no hay hora => 00:00:00)
        try {
            return LocalDateTime.parse(value, SPANISH_FLEX_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            // seguir probando
        }

        // ISO LocalDateTime (acepta segundos/fracciones opcionales según ISO)
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // seguir probando
        }

        // ISO solo fecha
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // seguir probando
        }

        // Compatibilidad con tus métodos existentes (por si alguien los usa directamente en otros sitios)
        LocalDateTime result = parseSpanishDateTime(value);
        if (result != null) return result;

        result = parseSpanishDate(value);
        if (result != null) return result;

        throw new IllegalArgumentException(
                "Parámetro fecha inválido: '" + text + "'. Formatos aceptados: dd/MM/yyyy, dd/MM/yyyy HH:mm[:ss], yyyy-MM-ddTHH:mm[:ss], yyyy-MM-dd"
        );
    }

    // NUEVO: Español flexible (STRICT) con hora opcional
    private static final DateTimeFormatter SPANISH_FLEX_DATE_TIME_FORMATTER =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd/MM/uuuu")
                    .optionalStart()
                    .appendLiteral(' ')
                    .appendPattern("HH:mm")
                    .optionalStart()
                    .appendLiteral(':')
                    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                    .optionalEnd()
                    .optionalEnd()
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter(new Locale("es", "ES"))
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Obtiene la fecha y hora actual en la zona horaria del sistema
     */
    public static LocalDateTime ahora() {
        return LocalDateTime.now();
    }

    /**
     * Convierte un string a LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
        } catch (Exception e) {
            log.error("Error parseando fecha y hora: {}", dateTimeStr, e);
            return null;
        }
    }

    /**
     * Convierte un string en formato ISO a LocalDateTime
     */
    public static LocalDateTime parseISODateTime(String isoDateTimeStr) {
        try {
            return LocalDateTime.parse(isoDateTimeStr, isoDateTimeFormatter);
        } catch (Exception e) {
            log.error("Error parseando fecha y hora ISO: {}", isoDateTimeStr, e);
            return null;
        }
    }

    /**
     * Formatea un LocalDateTime a string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(dateTimeFormatter);
    }

    /**
     * Formatea un LocalDateTime a string ISO
     */
    public static String formatISODateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(isoDateTimeFormatter);
    }

    /**
     * Formatea un LocalDateTime a solo fecha
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(dateFormatter);
    }

    /**
     * Formatea un LocalDateTime a solo hora
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(timeFormatter);
    }

    /**
     * Convierte un string en formato español (dd/MM/yyyy) a LocalDateTime
     */
    public static LocalDateTime parseSpanishDate(String spanishDateStr) {
        try {
            LocalDate date = LocalDate.parse(spanishDateStr, spanishDateFormatter);
            return date.atStartOfDay();
        } catch (Exception e) {
            // Intentar formato ISO como fallback
            try {
                return LocalDateTime.parse(spanishDateStr);
            } catch (Exception ex) {
                log.error("Error parseando fecha española: {}", spanishDateStr, e);
                return null;
            }
        }
    }

    /**
     * Convierte un string en formato español con hora (dd/MM/yyyy HH:mm:ss) a LocalDateTime
     */
    public static LocalDateTime parseSpanishDateTime(String spanishDateTimeStr) {
        try {
            return LocalDateTime.parse(spanishDateTimeStr, spanishDateTimeFormatter);
        } catch (Exception e) {
            // Intentar formato ISO como fallback
            try {
                return LocalDateTime.parse(spanishDateTimeStr);
            } catch (Exception ex) {
                log.error("Error parseando fecha y hora española: {}", spanishDateTimeStr, e);
                return null;
            }
        }
    }

    /**
     * Formatea un LocalDateTime a string en formato español (dd/MM/yyyy)
     */
    public static String formatSpanishDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(spanishDateFormatter);
    }

    /**
     * Formatea un LocalDateTime a string en formato español con hora (dd/MM/yyyy HH:mm:ss)
     */
    public static String formatSpanishDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(spanishDateTimeFormatter);
    }

    /**
     * Calcula la diferencia en minutos entre dos fechas
     */
    public static long diferenciaMinutos(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) return 0;
        return ChronoUnit.MINUTES.between(inicio, fin);
    }

    /**
     * Calcula la diferencia en horas entre dos fechas
     */
    public static long diferenciaHoras(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) return 0;
        return ChronoUnit.HOURS.between(inicio, fin);
    }

    /**
     * Calcula la diferencia en días entre dos fechas
     */
    public static long diferenciaDias(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) return 0;
        return ChronoUnit.DAYS.between(inicio, fin);
    }

    /**
     * Suma minutos a una fecha
     */
    public static LocalDateTime sumarMinutos(LocalDateTime fecha, int minutos) {
        if (fecha == null) return null;
        return fecha.plusMinutes(minutos);
    }

    /**
     * Suma horas a una fecha
     */
    public static LocalDateTime sumarHoras(LocalDateTime fecha, int horas) {
        if (fecha == null) return null;
        return fecha.plusHours(horas);
    }

    /**
     * Suma días a una fecha
     */
    public static LocalDateTime sumarDias(LocalDateTime fecha, int dias) {
        if (fecha == null) return null;
        return fecha.plusDays(dias);
    }

    /**
     * Resta minutos a una fecha
     */
    public static LocalDateTime restarMinutos(LocalDateTime fecha, int minutos) {
        if (fecha == null) return null;
        return fecha.minusMinutes(minutos);
    }

    /**
     * Resta horas a una fecha
     */
    public static LocalDateTime restarHoras(LocalDateTime fecha, int horas) {
        if (fecha == null) return null;
        return fecha.minusHours(horas);
    }

    /**
     * Resta días a una fecha
     */
    public static LocalDateTime restarDias(LocalDateTime fecha, int dias) {
        if (fecha == null) return null;
        return fecha.minusDays(dias);
    }

    /**
     * Verifica si una fecha está entre otras dos fechas (inclusive)
     */
    public static boolean estaEntre(LocalDateTime fecha, LocalDateTime inicio, LocalDateTime fin) {
        if (fecha == null || inicio == null || fin == null) return false;
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    /**
     * Verifica si una fecha es hoy
     */
    public static boolean esHoy(LocalDateTime fecha) {
        if (fecha == null) return false;
        return fecha.toLocalDate().equals(LocalDate.now());
    }

    /**
     * Verifica si una fecha es mañana
     */
    public static boolean esMañana(LocalDateTime fecha) {
        if (fecha == null) return false;
        return fecha.toLocalDate().equals(LocalDate.now().plusDays(1));
    }

    /**
     * Verifica si una fecha es ayer
     */
    public static boolean esAyer(LocalDateTime fecha) {
        if (fecha == null) return false;
        return fecha.toLocalDate().equals(LocalDate.now().minusDays(1));
    }

    /**
     * Obtiene el inicio del día (00:00:00)
     */
    public static LocalDateTime inicioDelDia(LocalDateTime fecha) {
        if (fecha == null) return null;
        return fecha.toLocalDate().atStartOfDay();
    }

    /**
     * Obtiene el fin del día (23:59:59)
     */
    public static LocalDateTime finDelDia(LocalDateTime fecha) {
        if (fecha == null) return null;
        return fecha.toLocalDate().atTime(23, 59, 59);
    }

    /**
     * Obtiene el inicio de la semana (lunes 00:00:00)
     */
    public static LocalDateTime inicioDeLaSemana(LocalDateTime fecha) {
        if (fecha == null) return null;
        return fecha.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
    }

    /**
     * Obtiene el fin de la semana (domingo 23:59:59)
     */
    public static LocalDateTime finDeLaSemana(LocalDateTime fecha) {
        if (fecha == null) return null;
        return fecha.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);
    }

    /**
     * Obtiene el inicio del mes (día 1 00:00:00)
     */
    public static LocalDateTime inicioDelMes(LocalDateTime fecha) {
        if (fecha == null) return null;
        return fecha.withDayOfMonth(1).toLocalDate().atStartOfDay();
    }

    /**
     * Obtiene el fin del mes (último día 23:59:59)
     */
    public static LocalDateTime finDelMes(LocalDateTime fecha) {
        if (fecha == null) return null;
        return fecha.withDayOfMonth(fecha.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59);
    }

    /**
     * Verifica si una fecha es fin de semana
     */
    public static boolean esFinDeSemana(LocalDateTime fecha) {
        if (fecha == null) return false;
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    /**
     * Verifica si una fecha es día laborable
     */
    public static boolean esDiaLaborable(LocalDateTime fecha) {
        if (fecha == null) return false;
        return !esFinDeSemana(fecha);
    }

    /**
     * Convierte una lista de LocalDateTime a strings formateados
     */
    public static List<String> formatDateTimeList(List<LocalDateTime> fechas) {
        if (fechas == null) return null;
        return fechas.stream()
                .map(DateUtils::formatDateTime)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de strings a LocalDateTime
     */
    public static List<LocalDateTime> parseDateTimeList(List<String> fechasStr) {
        if (fechasStr == null) return null;
        return fechasStr.stream()
                .map(DateUtils::parseDateTime)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la edad en años a partir de una fecha de nacimiento
     */
    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /**
     * Redondea una fecha al minuto más cercano
     */
    public static LocalDateTime redondearAlMinuto(LocalDateTime fecha) {
        if (fecha == null) return null;
        int segundos = fecha.getSecond();
        if (segundos >= 30) {
            return fecha.plusMinutes(1).withSecond(0).withNano(0);
        } else {
            return fecha.withSecond(0).withNano(0);
        }
    }
}

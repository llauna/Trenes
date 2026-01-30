package com.david.trenes.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class GeoUtils {
    
    private static final double RADIO_TIERRA_KM = 6371.0;
    private static final double RADIO_TIERRA_MILLAS = 3958.8;
    
    /**
     * Calcula la distancia entre dos coordenadas usando la fórmula de Haversine
     * @param lat1 Latitud del punto 1 en grados
     * @param lon1 Longitud del punto 1 en grados
     * @param lat2 Latitud del punto 2 en grados
     * @param lon2 Longitud del punto 2 en grados
     * @return Distancia en kilómetros
     */
    public static double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        return calcularDistancia(lat1, lon1, lat2, lon2, RADIO_TIERRA_KM);
    }
    
    /**
     * Calcula la distancia entre dos coordenadas usando la fórmula de Haversine
     * @param lat1 Latitud del punto 1 en grados
     * @param lon1 Longitud del punto 1 en grados
     * @param lat2 Latitud del punto 2 en grados
     * @param lon2 Longitud del punto 2 en grados
     * @return Distancia en millas
     */
    public static double calcularDistanciaMillas(double lat1, double lon1, double lat2, double lon2) {
        return calcularDistancia(lat1, lon1, lat2, lon2, RADIO_TIERRA_MILLAS);
    }
    
    private static double calcularDistancia(double lat1, double lon1, double lat2, double lon2, double radioTierra) {
        // Convertir grados a radianes
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Diferencias
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        // Fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return radioTierra * c;
    }
    
    /**
     * Verifica si una coordenada está dentro de un radio específico de otra coordenada
     * @param lat1 Latitud del punto central en grados
     * @param lon1 Longitud del punto central en grados
     * @param lat2 Latitud del punto a verificar en grados
     * @param lon2 Longitud del punto a verificar en grados
     * @param radioKm Radio en kilómetros
     * @return true si está dentro del radio, false en caso contrario
     */
    public static boolean estaDentroDelRadio(double lat1, double lon1, double lat2, double lon2, double radioKm) {
        double distancia = calcularDistanciaKm(lat1, lon1, lat2, lon2);
        return distancia <= radioKm;
    }
    
    /**
     * Calcula el punto medio entre dos coordenadas
     * @param lat1 Latitud del punto 1 en grados
     * @param lon1 Longitud del punto 1 en grados
     * @param lat2 Latitud del punto 2 en grados
     * @param lon2 Longitud del punto 2 en grados
     * @return Coordenada del punto medio [latitud, longitud]
     */
    public static double[] calcularPuntoMedio(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        double bx = Math.cos(lat2Rad) * Math.cos(lon2Rad - lon1Rad);
        double by = Math.cos(lat2Rad) * Math.sin(lon2Rad - lon1Rad);
        
        double latMedioRad = Math.atan2(Math.sin(lat1Rad) + Math.sin(lat2Rad),
                                       Math.sqrt(Math.pow(Math.cos(lat1Rad) + bx, 2) + Math.pow(by, 2)));
        double lonMedioRad = lon1Rad + Math.atan2(by, Math.cos(lat1Rad) + bx);
        
        return new double[]{
            Math.toDegrees(latMedioRad),
            Math.toDegrees(lonMedioRad)
        };
    }
    
    /**
     * Calcula el bearing (rumbo) desde el punto 1 hacia el punto 2
     * @param lat1 Latitud del punto 1 en grados
     * @param lon1 Longitud del punto 1 en grados
     * @param lat2 Latitud del punto 2 en grados
     * @param lon2 Longitud del punto 2 en grados
     * @return Bearing en grados (0-360)
     */
    public static double calcularBearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        double deltaLon = lon2Rad - lon1Rad;
        
        double y = Math.sin(deltaLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                   Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLon);
        
        double bearing = Math.atan2(y, x);
        double bearingDeg = Math.toDegrees(bearing);
        
        // Normalizar a 0-360 grados
        return (bearingDeg + 360) % 360;
    }
    
    /**
     * Redondea un valor decimal a un número específico de decimales
     * @param valor Valor a redondear
     * @param decimales Número de decimales
     * @return Valor redondeado
     */
    public static double redondear(double valor, int decimales) {
        if (decimales < 0) throw new IllegalArgumentException();
        
        BigDecimal bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    /**
     * Valida si una coordenada es válida
     * @param latitud Latitud en grados
     * @param longitud Longitud en grados
     * @return true si la coordenada es válida
     */
    public static boolean esCoordenadaValida(double latitud, double longitud) {
        return latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180;
    }
    
    /**
     * Convierte coordenadas de grados decimales a grados-minutos-segundos
     * @param decimal Grados decimales
     * @return Array con [grados, minutos, segundos]
     */
    public static double[] decimalAGMS(double decimal) {
        double grados = Math.floor(decimal);
        double minutosDecimal = (decimal - grados) * 60;
        double minutos = Math.floor(minutosDecimal);
        double segundos = (minutosDecimal - minutos) * 60;
        
        return new double[]{grados, minutos, segundos};
    }
    
    /**
     * Convierte coordenadas de grados-minutos-segundos a grados decimales
     * @param grados Grados
     * @param minutos Minutos
     * @param segundos Segundos
     * @return Grados decimales
     */
    public static double gmsADecimal(double grados, double minutos, double segundos) {
        return grados + minutos / 60.0 + segundos / 3600.0;
    }
}

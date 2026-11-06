package com.tecnomovil;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Se generan los datos simulados
        List<Registro> registros = GeneradorDatos.generar(50000);
        int tamanoInicial = registros.size();
        System.out.println("=== TecnoMovil Data ===");
        System.out.println("Total de registros: " + tamanoInicial);

        // a) Afluencia por estación
        System.out.println("\n--- a) Afluencia por estacion ---");
        Map<String, Long> afluencia = ProcesadorTransporte.afluenciaPorEstacion(registros);
        // forEach con lambda: (clave, valor) -> imprimir
        afluencia.forEach((estacion, total) -> System.out.println(estacion + ": " + total));

        // b) Horas pico
        System.out.println("\n--- b) Top 3 horas pico ---");
        ProcesadorTransporte.horasPico(registros, 3)
                .forEach(e -> System.out.println(e.getKey() + ":00 -> " + e.getValue() + " registros"));

        // c) Rutas más utilizadas
        System.out.println("\n--- c) Rutas mas utilizadas ---");
        ProcesadorTransporte.rutasMasUtilizadas(registros, 3)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        // d) Patrones de viaje (solo se muestran 5 usuarios)
        System.out.println("\n--- d) Patrones de viaje (5 usuarios) ---");
        Map<String, List<String>> patrones = ProcesadorTransporte.patronesDeViaje(registros);
        patrones.entrySet().stream()
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));

        // e) Tiempo promedio entre estaciones (solo 5 tramos)
        System.out.println("\n--- e) Tiempo promedio entre estaciones (5 tramos) ---");
        ProcesadorTransporte.tiempoPromedioEntreEstaciones(registros).entrySet().stream()
                .limit(5)
                .forEach(e -> System.out.printf("%s: %.2f min%n", e.getKey(), e.getValue()));

        // f) Sobrecarga en rutas
        long umbral = 14300;
        System.out.println("\n--- f) Estado de rutas (umbral = " + umbral + ") ---");
        ProcesadorTransporte.estadoDeRutas(registros, umbral)
                .forEach((ruta, estado) -> System.out.println(ruta + ": " + estado));

        // Comprobación de inmutabilidad
        System.out.println("\n--- Inmutabilidad ---");
        System.out.println("La lista original sigue con " + registros.size()
                + " registros");
    }
}

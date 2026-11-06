package com.tecnomovil;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Aquí están todas las operaciones funcionales.
// Todos los métodos son FUNCIONES PURAS:

public class ProcesadorTransporte {

    // FUNCIÓN DE ORDEN SUPERIOR: recibe otras funciones como parámetros
    public static <K> Map<K, Long> contarPor(List<Registro> registros,
                                             Predicate<Registro> filtro,
                                             Function<Registro, K> agrupador) {
        Map<K, Long> resultado = registros.stream()        // 1. se crea el stream
                .filter(filtro)                             // 2. se dejan solo los registros que cumplen
                .collect(Collectors.groupingBy(agrupador,   // 3. se agrupan por la clave
                        Collectors.counting()));            //    y se cuenta cuántos hay en cada grupo
        return Collections.unmodifiableMap(resultado);      // 4. se devuelve un mapa que no se puede modificar
    }

    // a) Afluencia por estación: cuántos usuarios entran a cada estación
    public static Map<String, Long> afluenciaPorEstacion(List<Registro> registros) {
        return contarPor(registros, Registro::esEntrada, Registro::getEstacion);
    }

    // b) Horas pico: se agrupa por hora del día y se toman las de mayor flujo
    public static List<Map.Entry<Integer, Long>> horasPico(List<Registro> registros, int cuantas) {
        // Lambda: r -> true (acepta todos los registros)  y  r -> r.getTimestamp().getHour() (saca la hora)
        Map<Integer, Long> flujoPorHora = contarPor(registros,
                r -> true,
                r -> r.getTimestamp().getHour());

        return flujoPorHora.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue())) // de mayor a menor
                .limit(cuantas)                                             // solo las primeras
                .collect(Collectors.toList());
    }

    // c) Rutas más utilizadas: se cuentan las entradas por ruta y se ordenan de mayor a menor
    public static List<Map.Entry<String, Long>> rutasMasUtilizadas(List<Registro> registros, int cuantas) {
        return contarPor(registros, Registro::esEntrada, Registro::getRuta)
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(cuantas)
                .collect(Collectors.toList());
    }

    // d) Patrones de viaje: por cada usuario, la lista de estaciones en el orden de los registros
    public static Map<String, List<String>> patronesDeViaje(List<Registro> registros) {
        Map<String, List<String>> patrones = registros.stream()
                .collect(Collectors.groupingBy(Registro::getIdUsuario,      // agrupa por usuario
                        Collectors.mapping(Registro::getEstacion,           // de cada registro toma solo la estación
                                Collectors.toList())));                     // las guarda en una lista
        return Collections.unmodifiableMap(patrones);
    }

    // e) Tiempo promedio (en minutos) entre estaciones consecutivas
    // Se compara cada registro de entrada con el registro siguiente del mismo usuario.
    public static Map<String, Double> tiempoPromedioEntreEstaciones(List<Registro> registros) {
        // Primero se agrupan los registros por usuario
        Map<String, List<Registro>> porUsuario = registros.stream()
                .collect(Collectors.groupingBy(Registro::getIdUsuario));

        Map<String, Double> promedios = porUsuario.values().stream()
                .flatMap(registrosDelUsuario -> {
                    // ordena una copia por hora, la lista original no cambia
                    List<Registro> ordenados = registrosDelUsuario.stream()
                            .sorted(Comparator.comparing(Registro::getTimestamp))
                            .collect(Collectors.toList());

                    // Se recorren los pares consecutivos
                    return IntStream.range(1, ordenados.size())
                            .filter(i -> ordenados.get(i - 1).esEntrada()          // el anterior es entrada
                                    && !ordenados.get(i).esEntrada())              // y el actual es salida
                            .mapToObj(i -> {
                                Registro entrada = ordenados.get(i - 1);
                                Registro salida = ordenados.get(i);
                                String tramo = entrada.getEstacion() + " -> " + salida.getEstacion();
                                double minutos = java.time.Duration
                                        .between(entrada.getTimestamp(), salida.getTimestamp())
                                        .toMinutes();
                                return new AbstractMap.SimpleEntry<>(tramo, minutos);
                            });
                })
                // Se agrupan por tramo y se saca el promedio de los minutos
                .collect(Collectors.groupingBy(Map.Entry::getKey, TreeMap::new,
                        Collectors.averagingDouble(Map.Entry::getValue)));

        return Collections.unmodifiableMap(promedios);
    }

    // f) Sobrecarga: si las entradas de una ruta superan el umbral, la ruta es "CRITICA"
    public static Map<String, String> estadoDeRutas(List<Registro> registros, long umbral) {
        Map<String, String> estados = contarPor(registros, Registro::esEntrada, Registro::getRuta)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,                                          // la clave es el nombre de la ruta
                        e -> e.getValue() > umbral ? "CRITICA" : "NORMAL",          // lambda que decide el estado
                        (a, b) -> a,
                        TreeMap::new));
        return Collections.unmodifiableMap(estados);
    }
}

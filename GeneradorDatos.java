package com.tecnomovil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Esta clase solo sirve para SIMULAR datos.
public class GeneradorDatos {

    private static final String[] RUTAS = {"R1", "R2", "R3", "R4", "R5", "A1", "A2"};
    private static final String[] ESTACIONES = {"Central", "Norte", "Sur", "Universidad",
            "Hospital", "Terminal", "Parque", "Centro"};

    public static List<Registro> generar(int cantidadUsuarios) {
        Random random = new Random(42); // semilla fija: siempre salen los mismos datos
        List<Registro> lista = new ArrayList<>();

        for (int i = 0; i < cantidadUsuarios; i++) {
            String idUsuario = "U" + i;
            int viajes = 1 + random.nextInt(3); // cada usuario hace entre 1 y 3 viajes

            for (int v = 0; v < viajes; v++) {
                String ruta = RUTAS[random.nextInt(RUTAS.length)];
                String origen = ESTACIONES[random.nextInt(ESTACIONES.length)];
                String destino = ESTACIONES[random.nextInt(ESTACIONES.length)];

                // Hora entre las 5 y las 22
                LocalDateTime horaEntrada = LocalDateTime.of(2026, 11, 3,
                        5 + random.nextInt(18), random.nextInt(60));
                // El viaje dura entre 5 y 45 minutos
                LocalDateTime horaSalida = horaEntrada.plusMinutes(5 + random.nextInt(40));

                lista.add(new Registro(idUsuario, ruta, origen, "entrada", horaEntrada));
                lista.add(new Registro(idUsuario, ruta, destino, "salida", horaSalida));
            }
        }
        // Devolvemos la lista INMUTABLE
        return Collections.unmodifiableList(lista);
    }
}

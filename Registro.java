package com.tecnomovil;

import java.time.LocalDateTime;

// Esta clase representa un registro del sistema de transporte.
// Todos los atributos son "final": (inmutabilidad).
public class Registro {

    private final String idUsuario;
    private final String ruta;
    private final String estacion;
    private final String accion;          // "entrada" o "salida"
    private final LocalDateTime timestamp;

    public Registro(String idUsuario,
                    String ruta,
                    String estacion,
                    String accion,
                    LocalDateTime timestamp) {
        this.idUsuario = idUsuario;
        this.ruta = ruta;
        this.estacion = estacion;
        this.accion = accion;
        this.timestamp = timestamp;
    }

    // Solo hay getters, por eso el objeto no se puede modificar
    public String getIdUsuario() { return idUsuario; }
    public String getRuta() { return ruta; }
    public String getEstacion() { return estacion; }
    public String getAccion() { return accion; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Metodo auxiliar para saber si el registro es una entrada
    public boolean esEntrada() {
        return accion.equals("entrada");
    }

    @Override
    public String toString() {
        return idUsuario + " | " + ruta + " | " + estacion + " | " + accion + " | " + timestamp;
    }
}

package proyecto1;

class Registro {
    private String ensamblador;  // Nombre del registro en ensamblador ($t0, $t1...)
    private String etiqueta;      // Etiqueta temporal asociada (t1, t2...) o id
    private boolean disponible;   // Estado de disponibilidad

    public Registro(String ensamblador) {
        this.ensamblador = ensamblador;
        this.etiqueta = null;     // Por defecto, sin etiqueta asignada
        this.disponible = true;   // Inicialmente disponible
    }

    // Métodos getter y setter
    public String getEnsamblador() { return ensamblador; }
    public String getEtiqueta() { return etiqueta; }
    public boolean isDisponible() { return disponible; }

    public void asignarEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
        this.disponible = false;
    }

    public void liberar() {
        this.etiqueta = null;
        this.disponible = true;
    }

    @Override
    public String toString() {
        return ensamblador + " -> Etiqueta: " + (etiqueta != null ? etiqueta : "Ninguna") +
               " | Disponible: " + disponible;
    }
}


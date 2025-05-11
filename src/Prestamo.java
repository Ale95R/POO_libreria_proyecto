import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Representa un préstamo de material con fechas y control de mora.
 */
public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;

    /* --------------- atributos --------------- */
    private final String usuario;
    private final String tituloMaterial;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaDevolucion;
    private boolean devuelto;

    /* --------------- constructor --------------- */
    public Prestamo(String usuario,
                    String tituloMaterial,
                    LocalDate fechaPrestamo,
                    LocalDate fechaDevolucion) {
        this.usuario          = usuario;
        this.tituloMaterial   = tituloMaterial;
        this.fechaPrestamo    = fechaPrestamo;
        this.fechaDevolucion  = fechaDevolucion;
        this.devuelto         = false;
    }

    /* --------------- getters --------------- */
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public String    getTituloMaterial()  { return tituloMaterial;  }
    public String    getUsuario()         { return usuario;         }
    public boolean   isDevuelto()         { return devuelto;        }

    /** Año en que se generó el préstamo (para la tarifa de mora). */
    public int getAñoPrestamo() { return fechaPrestamo.getYear(); }

    /** Calcula días de retraso (0 si no hay). */
    public long calcularDiasRetraso(LocalDate fechaActual) {
        if (fechaActual.isAfter(fechaDevolucion)) {
            return ChronoUnit.DAYS.between(fechaDevolucion, fechaActual);
        }
        return 0;
    }

    /* --------------- marcar devolución --------------- */
    public void marcarDevuelto() { this.devuelto = true; }
}
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String usuario;
    private final String tituloMaterial;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaDevolucion;
    private boolean devuelto;


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


    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public String    getTituloMaterial()  { return tituloMaterial;  }
    public String    getUsuario()         { return usuario;         }
    public boolean   isDevuelto()         { return devuelto;        }

    public int getAñoPrestamo() { return fechaPrestamo.getYear(); }

    public long calcularDiasRetraso(LocalDate fechaActual) {
        if (fechaActual.isAfter(fechaDevolucion)) {
            return ChronoUnit.DAYS.between(fechaDevolucion, fechaActual);
        }
        return 0;
    }

    public void marcarDevuelto() { this.devuelto = true; }
}
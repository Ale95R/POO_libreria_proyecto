import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    private String nombreUsuario;
    private String tituloMaterial;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    public Prestamo(String nombreUsuario, String tituloMaterial, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.nombreUsuario = nombreUsuario;
        this.tituloMaterial = tituloMaterial;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = false;
    }

    public long calcularDiasRetraso(LocalDate fechaActual) {
        if (fechaActual.isAfter(fechaDevolucion)) {
            return ChronoUnit.DAYS.between(fechaDevolucion, fechaActual);
        }
        return 0;
    }

    public int getAñoPrestamo() {
        return fechaPrestamo.getYear();
    }

    public void marcarDevuelto() {
        this.devuelto = true;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public String getTituloMaterial() { return tituloMaterial; }
    public boolean isDevuelto() { return devuelto; }
}

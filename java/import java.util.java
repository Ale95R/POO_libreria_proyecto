import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class Mora {
    private static Map<Integer, Double> moraPorAño = new HashMap<>();

    public static void configurarMora(int año, double tarifaDiaria) {
        moraPorAño.put(año, tarifaDiaria);
    }

    public static double calcularMora(Prestamo prestamo, LocalDate fechaActual) {
        int año = prestamo.getAñoPrestamo();
        double tarifa = moraPorAño.getOrDefault(año, 0.50);
        long diasRetraso = prestamo.calcularDiasRetraso(fechaActual);
        return diasRetraso * tarifa;
    }
}

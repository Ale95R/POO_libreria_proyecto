import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public void prestar(String codigo, String usuario, int dias) throws SQLException {
        String sql = """
            INSERT INTO prestamo(codigo,usuario,fechaInicio,fechaLimite)
                 VALUES (?,?,date('now'),date('now', ?))
            """;
        try (PreparedStatement ps = DB.get().prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, usuario);
            ps.setString(3, "+" + dias + " day");
            ps.executeUpdate();
        }
    }

    public void devolver(String codigo) throws SQLException {
        try (PreparedStatement ps = DB.get().prepareStatement(
                "UPDATE prestamo SET devuelto = 1 WHERE codigo = ? AND devuelto = 0")) {
            ps.setString(1, codigo);
            ps.executeUpdate();
        }
    }

    public boolean estaPrestado(String codigo) throws SQLException {
        try (PreparedStatement ps = DB.get().prepareStatement(
                "SELECT 1 FROM prestamo WHERE codigo = ? AND devuelto = 0")) {
            ps.setString(1, codigo);
            return ps.executeQuery().next();
        }
    }

    public List<PrestamoRegistro> listarPrestamosUsuario(String usuario) throws SQLException {
        List<PrestamoRegistro> lista = new ArrayList<>();
        String sql = """
            SELECT p.codigo, m.titulo, p.fechaLimite
              FROM prestamo p JOIN material m USING (codigo)
             WHERE p.usuario = ? AND p.devuelto = 0
             ORDER BY p.fechaLimite
            """;
        try (PreparedStatement ps = DB.get().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new PrestamoRegistro(
                        rs.getString("codigo"),
                        rs.getString("titulo"),
                        rs.getString("fechaLimite")
                ));
            }
        }
        return lista;
    }

    public record PrestamoRegistro(String codigo, String titulo, String fechaLimite) {}
}

import java.sql.*;

public class MoraDAO {

    public double obtenerDeuda(String usuario) throws SQLException {
        String sql = "SELECT deuda FROM mora WHERE usuario = ?";
        try (PreparedStatement ps = DB.get().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("deuda") : 0.0;
        }
    }

    public void agregarMora(String usuario, double monto) throws SQLException {
        String sql = """
            INSERT INTO mora(usuario,deuda) VALUES (?,?)
            ON CONFLICT(usuario) DO UPDATE SET deuda = deuda + excluded.deuda
            """;
        try (PreparedStatement ps = DB.get().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setDouble(2, monto);
            ps.executeUpdate();
        }
    }
}
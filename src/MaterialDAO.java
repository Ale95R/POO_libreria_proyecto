import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso CRUD a la tabla material (SQLite). */
public class MaterialDAO {

    /* ---------- insertar o actualizar ---------- */
    public void guardar(CatalogoConsulta.Material m) throws SQLException {
        String sql = """
            INSERT INTO material(codigo,titulo,autor,tipo,idioma)
                 VALUES (?,?,?,?,?)
            ON CONFLICT(codigo) DO UPDATE SET
                 titulo = excluded.titulo,
                 autor  = excluded.autor,
                 tipo   = excluded.tipo,
                 idioma = excluded.idioma
            """;
        try (PreparedStatement ps = DB.get().prepareStatement(sql)) {
            ps.setString(1, m.codigo());
            ps.setString(2, m.titulo());
            ps.setString(3, m.autor());
            ps.setString(4, m.tipo());
            ps.setString(5, m.idioma());
            ps.executeUpdate();
        }
    }

    /* ---------- borrar ---------- */
    public void borrar(String codigo) throws SQLException {
        try (PreparedStatement ps = DB.get().prepareStatement(
                "DELETE FROM material WHERE codigo = ?")) {
            ps.setString(1, codigo);
            ps.executeUpdate();
        }
    }

    /* ---------- existe ---------- */
    public boolean existe(String codigo) throws SQLException {
        try (PreparedStatement ps = DB.get().prepareStatement(
                "SELECT 1 FROM material WHERE codigo = ?")) {
            ps.setString(1, codigo);
            return ps.executeQuery().next();
        }
    }

    /* ---------- listar ---------- */
    public List<CatalogoConsulta.Material> listar() throws SQLException {
        List<CatalogoConsulta.Material> lista = new ArrayList<>();
        String sql = "SELECT * FROM material ORDER BY codigo";
        try (Statement st = DB.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new CatalogoConsulta.Material(
                        rs.getString("codigo"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("tipo"),
                        rs.getString("idioma")
                ));
            }
        }
        return lista;
    }

    /* ---------- siguienteConsecutivo ---------- */
    public int siguienteConsecutivo(String prefijo) throws SQLException {
        String sql = "SELECT MAX(substr(codigo,4)) AS num FROM material WHERE codigo LIKE ?";
        try (PreparedStatement ps = DB.get().prepareStatement(sql)) {
            ps.setString(1, prefijo + "%");
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") + 1 : 1;
        }
    }
}

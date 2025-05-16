import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MostrarDevolucion extends JFrame {

    public MostrarDevolucion(String usuario, PrestamoDAO prestamoDAO) {
        setTitle("Mis préstamos – "+usuario);
        setSize(600,300); setLocationRelativeTo(null);

        String[] col={"Código","Título","Vence"};
        DefaultTableModel m=new DefaultTableModel(col,0);

        try {
            List<PrestamoDAO.PrestamoRegistro> lista = prestamoDAO.listarPrestamosUsuario(usuario);
            lista.forEach(r->m.addRow(new Object[]{r.codigo(),r.titulo(),r.fechaLimite()}));
        } catch(SQLException ex){ JOptionPane.showMessageDialog(this,ex.getMessage()); }

        add(new JScrollPane(new JTable(m)), BorderLayout.CENTER);
        setVisible(true);
    }
}


import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Ventana para devolver un libro específico. */
public class DevolverLibro extends JFrame {

    public DevolverLibro(String usuario) {
        setTitle("Devolver libro");
        setSize(340,170);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        add(p);

        JLabel l = new JLabel("Código del libro a devolver:");
        JTextField codigoTF = new JTextField(12);
        JButton ok = new JButton("Devolver");

        p.add(l); p.add(codigoTF); p.add(ok);
        setVisible(true);

        ok.addActionListener(e -> {
            String codigo = codigoTF.getText().trim();
            List<String> lista = CatalogoConsulta.prestamosPorUser.get(usuario);

            if (lista==null || !lista.contains(codigo)) {
                JOptionPane.showMessageDialog(this,"No tienes ese libro.");
                return;
            }
            /* devuelve */
            lista.remove(codigo);
            CatalogoConsulta.prestamos.remove(codigo);

            JOptionPane.showMessageDialog(this,"Devolución registrada.");
            dispose();
        });
    }
}
import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

/** Ventana simple para devolver un libro y calcular la mora. */
public class DevolverLibro extends JFrame {

    public DevolverLibro(String usuario){
        setTitle("Devolver libro"); setSize(340,170); setLocationRelativeTo(null);

        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); add(p);

        JTextField codTF = new JTextField(12);
        p.add(new JLabel("Código del libro a devolver:")); p.add(codTF);
        JButton ok = new JButton("Devolver"); p.add(ok);
        setVisible(true);

        ok.addActionListener(e->{
            String codigo = codTF.getText().trim();
            List<String> lista = CatalogoConsulta.prestamosPorUser.get(usuario);
            if (lista==null || !lista.contains(codigo)){
                JOptionPane.showMessageDialog(this,"No tienes ese libro."); return;
            }

            Prestamo pmo = CatalogoConsulta.prestamos.get(codigo);
            double mora  = Mora.calcularMora(pmo, LocalDate.now());

            /* actualizar estructuras */
            lista.remove(codigo); CatalogoConsulta.prestamos.remove(codigo);
            pmo.marcarDevuelto();

            if (mora>0){
                CatalogoConsulta.deudaPorUsuario.merge(usuario,mora,Double::sum);
                JOptionPane.showMessageDialog(this,"Devuelto. Mora: $"+String.format("%.2f",mora));
            } else JOptionPane.showMessageDialog(this,"Devuelto sin mora.");

            dispose();
        });
    }
}
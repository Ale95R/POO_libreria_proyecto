import javax.swing.*;
import java.sql.SQLException;

public class DevolverLibro extends JFrame {

    public DevolverLibro(String usuario, PrestamoDAO prestamoDAO, MoraDAO moraDAO) {
        setTitle("Devolver material");
        setSize(340,170); setLocationRelativeTo(null);
        JPanel p=new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); add(p);

        JTextField codTF=new JTextField(12);
        p.add(new JLabel("Código a devolver:")); p.add(codTF);
        JButton ok=new JButton("Devolver"); p.add(ok); setVisible(true);

        ok.addActionListener(e->{
            String codigo=codTF.getText().trim();
            try {
                if (!prestamoDAO.estaPrestado(codigo)){msg("Ese material no está prestado.");return;}
                prestamoDAO.devolver(codigo);
                msg("Devuelto correctamente.");
                dispose();
            }catch(SQLException ex){msg(ex.getMessage());}
        });
    }
    private void msg(String s){JOptionPane.showMessageDialog(this,s);}
}
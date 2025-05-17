import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CatalogoConsulta extends JFrame {

    private static final int DIAS_PRESTAMO = 7;

    /* ---------- credenciales demo ---------- */
    public static final java.util.Map<String,String> adminCreds  = java.util.Map.of("admin","123");
    public static final java.util.Map<String,String> profCreds   = java.util.Map.of("prof","123");
    public static final java.util.Map<String,String> alumnoCreds = java.util.Map.of(
            "alumno1","123","alumno2","123","alumno3","123","alumnoMoroso","123");

    /* ---------- Clases para SQL ---------- */
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final MoraDAO     moraDAO     = new MoraDAO();

    /* ---------- sesión ---------- */
    private String usuarioActual;
    private String tipoActual;

    /* ==================== MAIN ==================== */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CatalogoConsulta().mostrarLogin());
    }

    /* ==================== LOGIN ==================== */
    private void mostrarLogin() {
        JFrame login = new JFrame("Login");
        login.setSize(600,260);
        login.setDefaultCloseOperation(EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(6,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JComboBox<String> tipoCB = new JComboBox<>(new String[]{"Administrador","Profesor","Alumno"});
        JTextField userTF = new JTextField();
        JPasswordField passTF = new JPasswordField();
        JLabel msg = new JLabel(" "); msg.setForeground(Color.RED);

        form.add(new JLabel("Tipo de usuario:")); form.add(tipoCB);
        form.add(new JLabel("Usuario:"));         form.add(userTF);
        form.add(new JLabel("Contraseña:"));      form.add(passTF);
        form.add(new JLabel());                   form.add(msg);
        JButton ok = new JButton("Iniciar sesión"); form.add(ok);

        login.add(form); login.setVisible(true);

        ok.addActionListener(e->{
            String tipo = (String) tipoCB.getSelectedItem();
            String usr  = userTF.getText().trim();
            String pwd  = new String(passTF.getPassword());
            if (autenticar(tipo,usr,pwd)) {
                usuarioActual = usr; tipoActual = tipo;
                login.dispose(); mostrarMenu();
            } else msg.setText("Credenciales incorrectas.");
        });
    }
    private boolean autenticar(String tipo,String u,String p){
        return switch(tipo){
            case "Administrador" -> p.equals(adminCreds.get(u));
            case "Profesor"      -> p.equals(profCreds .get(u));
            case "Alumno"        -> p.equals(alumnoCreds.get(u));
            default -> false;
        };
    }

    /* ==================== MENÚ ==================== */
    private void mostrarMenu() {
        JFrame menu = new JFrame("Catálogo – "+tipoActual+" ("+usuarioActual+")");
        menu.setSize(650,340); menu.setLocationRelativeTo(null);
        menu.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(); root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        menu.add(root);

        Dimension d = new Dimension(220,32);

        if ("Administrador".equals(tipoActual)) {
            btn(root,"Ingresar nuevo ejemplar",d,e->formMaterial("agregar"));
            btn(root,"Editar ejemplar",        d,e->formMaterial("editar"));
            btn(root,"Borrar ejemplar",        d,e->formMaterial("borrar"));
        } else {
            btn(root,"Prestar libro",     d,e->accionPrestar());
            btn(root,"Devolver libro",    d,e->new DevolverLibro(usuarioActual,prestamoDAO,moraDAO).setVisible(true));
            btn(root,"Ver mis préstamos", d,e->new MostrarDevolucion(usuarioActual,prestamoDAO).setVisible(true));
            btn(root,"Ver mora",          d,e->verMora());
        }
        btn(root,"Ver ejemplares",d,e->tablaMateriales());

        root.add(Box.createVerticalStrut(15));
        btn(root,"Cerrar sesión",d,e->{ menu.dispose(); mostrarLogin(); })
                .setForeground(Color.RED);

        menu.setVisible(true);
    }
    private JButton btn(JPanel p,String txt,Dimension d,java.awt.event.ActionListener al){
        JButton b=new JButton(txt); b.setMaximumSize(d); b.setAlignmentX(CENTER_ALIGNMENT); b.addActionListener(al); p.add(b); return b;
    }

    /* =========== Material usando SQL =========== */
    private void formMaterial(String accion) {
        JFrame f = new JFrame(accion.toUpperCase()+" ejemplar");
        f.setSize(500,300); f.setLocationRelativeTo(null);
        JPanel p = new JPanel(new GridLayout(6,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        f.add(p);

        JTextField codTF=new JTextField(),titTF=new JTextField(),autTF=new JTextField(),cntTF=new JTextField();
        JComboBox<String> tipoCB=new JComboBox<>(new String[]{"Libro","Revista","CD","DVD"});
        JComboBox<String> idiCB =new JComboBox<>(new String[]{"Español","Inglés"});

        if (!"agregar".equals(accion)) {
            String c = JOptionPane.showInputDialog("Código:");
            try {
                if (c==null||!materialDAO.existe(c)){msg(f,"No existe");return;}
                var m = materialDAO.listar().stream()
                        .filter(x->x.codigo().equals(c))
                        .findFirst().orElseThrow();
                codTF.setText(c); titTF.setText(m.titulo()); autTF.setText(m.autor());
                tipoCB.setSelectedItem(m.tipo()); idiCB.setSelectedItem(m.idioma());
            } catch (SQLException ex){msg(f,ex.getMessage()); return;}
        }

        p.add(new JLabel("Título:"));   p.add(titTF);
        p.add(new JLabel("Autor:"));    p.add(autTF);
        p.add(new JLabel("Cantidad:")); p.add(cntTF);
        p.add(new JLabel("Tipo:"));     p.add(tipoCB);
        p.add(new JLabel("Idioma:"));   p.add(idiCB);

        JButton ok=new JButton(switch(accion){case"agregar"->"Guardar";case"editar"->"Actualizar";default->"Borrar";});
        p.add(ok); f.setVisible(true);

        ok.addActionListener(e->{
            String tipo=(String)tipoCB.getSelectedItem(),idi=(String)idiCB.getSelectedItem();
            String tit=titTF.getText(),aut=autTF.getText();
            int cant=cntTF.getText().isEmpty()?1:Integer.parseInt(cntTF.getText());

            try{
                if("agregar".equals(accion)){
                    for(int i=0;i<cant;i++){
                        String codigo=generarCodigo(tipo);
                        materialDAO.guardar(new Material(codigo,tit,aut,tipo,idi));
                    }
                    msg(f,"Añadido.");
                }else{
                    String c=codTF.getText();
                    if("editar".equals(accion)){
                        materialDAO.guardar(new Material(c,tit,aut,tipo,idi));
                        msg(f,"Actualizado.");
                    }else{
                        materialDAO.borrar(c); msg(f,"Eliminado.");
                    }
                }
                f.dispose();
            }catch(SQLException ex){msg(f,ex.getMessage());}
        });
    }

    /* ==================== Tabla ==================== */
    private void tablaMateriales() {
        try{
            List<Material> lista=materialDAO.listar();
            DefaultTableModel m=new DefaultTableModel(new String[]{"Código","Título","Autor","Tipo","Idioma"},0);
            lista.forEach(x->m.addRow(new Object[]{x.codigo(),x.titulo(),x.autor(),x.tipo(),x.idioma()}));
            JFrame t=new JFrame("Ejemplares"); t.setSize(600,400); t.setLocationRelativeTo(null);
            t.add(new JScrollPane(new JTable(m))); t.setVisible(true);
        }catch(SQLException ex){msg(this,ex.getMessage());}
    }

    /* ==================== Prestar ==================== */
    private void accionPrestar() {
        String codigo = JOptionPane.showInputDialog(this,"Código del material:");
        if (codigo==null||codigo.isBlank()) return;
        try {
            if (!materialDAO.existe(codigo))               { msg(this,"No existe ese código."); return; }
            if (prestamoDAO.estaPrestado(codigo))          { msg(this,"Ya está prestado.");    return; }
            if (moraDAO.obtenerDeuda(usuarioActual) > 0)   { msg(this,"Tienes mora.");         return; }
            prestamoDAO.prestar(codigo,usuarioActual,DIAS_PRESTAMO);
            msg(this,"¡Prestado! Vence en "+DIAS_PRESTAMO+" días.");
        } catch(SQLException ex){msg(this,ex.getMessage());}
    }

    /* ==================== Mora ==================== */
    private void verMora() {
        try {
            double d = moraDAO.obtenerDeuda(usuarioActual);
            msg(this,d==0?"Sin mora.":"Debes $"+d);
        } catch(SQLException ex){msg(this,ex.getMessage());}
    }

    /* ==================== Utilidades ==================== */
    private void msg(Component c,String s){JOptionPane.showMessageDialog(c,s);}

    private String generarCodigo(String tipo) throws SQLException {
        String pref=switch(tipo){
            case"Libro"->"LIB";case"Revista"->"REV";case"CD"->"CDA";case"DVD"->"DVD";default->"XXX";};
        int n=materialDAO.siguienteConsecutivo(pref);
        return pref+String.format("%05d",n);
    }
    
    public record Material(String codigo,String titulo,String autor,String tipo,String idioma){}
}
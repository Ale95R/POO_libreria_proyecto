import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;


public class CatalogoConsulta extends JFrame {


    public static final Map<String, String> adminCreds  = new HashMap<>();
    public static final Map<String, String> profCreds   = new HashMap<>();
    public static final Map<String, String> alumnoCreds = new HashMap<>();

    public static final Map<String, Material>            catalogo           = new HashMap<>();
    public static final Map<String, Integer>             contadorPorTipo    = new HashMap<>();
    public static final Map<String, List<String>>        prestamosPorUser   = new HashMap<>();
    public static final Map<String, String>              prestamos          = new HashMap<>();
    public static final Map<String, Boolean>             usuariosConMora    = new HashMap<>();

    public static final int LIMITE_PRESTAMOS = 3;

    private String usuarioActual;
    private String tipoActual;          // Administrador | Profesor | Alumno


    public static void main(String[] args) {

        adminCreds.put("admin",  "123");
        profCreds.put("prof",    "123");
        alumnoCreds.put("alumno1","123");
        alumnoCreds.put("alumno2","123");
        alumnoCreds.put("alumno3","123");
        usuariosConMora.put("alumno3", true);

        SwingUtilities.invokeLater(() -> new CatalogoConsulta().mostrarLogin());
    }

    public void mostrarLogin() {
        JFrame login = new JFrame("Login");
        login.setSize(600, 260);
        login.setDefaultCloseOperation(EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> tipoCB = new JComboBox<>(new String[]{"Administrador","Profesor","Alumno"});
        JTextField        userTF = new JTextField();
        JPasswordField    passTF = new JPasswordField();
        JLabel            msg    = new JLabel(" ");
        msg.setForeground(Color.RED);

        form.add(new JLabel("Tipo de usuario:")); form.add(tipoCB);
        form.add(new JLabel("Usuario:"));         form.add(userTF);
        form.add(new JLabel("Contraseña:"));      form.add(passTF);
        form.add(new JLabel());                   // hueco
        JButton loginBtn = new JButton("Iniciar sesión");
        form.add(loginBtn);
        form.add(msg);

        login.add(form);
        login.setVisible(true);

        /* ───── listener ───── */
        loginBtn.addActionListener(e -> {
            String tipo = (String) tipoCB.getSelectedItem();
            String usr  = userTF.getText().trim();
            String pass = new String(passTF.getPassword());

            if (autenticar(tipo, usr, pass)) {
                usuarioActual = usr;
                tipoActual    = tipo;
                login.dispose();
                mostrarMenuPrincipal();
            } else {
                msg.setText("Credenciales incorrectas.");
            }
        });
    }

    private boolean autenticar(String tipo, String user, String pass) {
        switch (tipo) {
            case "Administrador": return adminCreds.getOrDefault(user,"").equals(pass);
            case "Profesor":      return profCreds .getOrDefault(user,"").equals(pass);
            case "Alumno":        return alumnoCreds.getOrDefault(user,"").equals(pass);
            default:              return false;
        }
    }

    private void mostrarMenuPrincipal() {
        JFrame menu = new JFrame("Catálogo – "+tipoActual+" ("+usuarioActual+")");
        menu.setSize(650, 320);
        menu.setLocationRelativeTo(null);
        menu.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        menu.add(p);

        JLabel t = new JLabel("Consulta al catálogo");
        t.setAlignmentX(CENTER_ALIGNMENT);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        p.add(t);
        p.add(Box.createVerticalStrut(10));

        Dimension btnSize = new Dimension(220, 32);


        if ("Administrador".equals(tipoActual)) {
            addButton(p,"Ingresar nuevo ejemplar", btnSize,
                    e -> mostrarFormulario("agregar"));
            addButton(p,"Editar ejemplar",          btnSize,
                    e -> mostrarFormulario("editar"));
            addButton(p,"Borrar ejemplar",          btnSize,
                    e -> mostrarFormulario("borrar"));
        }

        if (!"Administrador".equals(tipoActual)) {
            addButton(p,"Prestar libro",  btnSize, e -> mostrarPrestamo());
            addButton(p,"Devolver libro", btnSize,
                    e -> new DevolverLibro(usuarioActual).setVisible(true));
            addButton(p,"Ver mis préstamos", btnSize,
                    e -> new MostrarDevolucion(usuarioActual).setVisible(true));
        }

        addButton(p,"Ver ejemplares", btnSize, e -> mostrarCatalogoTabla());

        JButton salir = new JButton("Cerrar sesión");
        salir.setForeground(Color.RED);
        salir.setMaximumSize(btnSize);
        salir.setAlignmentX(CENTER_ALIGNMENT);
        salir.addActionListener(e -> {
            menu.dispose();
            mostrarLogin();
        });
        p.add(Box.createVerticalStrut(15));
        p.add(salir);

        menu.setVisible(true);
    }

    private void addButton(JPanel panel, String txt, Dimension sz, java.awt.event.ActionListener al) {
        JButton b = new JButton(txt);
        b.setMaximumSize(sz);
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.addActionListener(al);
        panel.add(b);
    }

    private void mostrarFormulario(String accion) {
        JFrame f = new JFrame(accion.toUpperCase()+" ejemplar");
        f.setSize(500, 300);
        f.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6,2,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        f.add(panel);

        JTextField codTF     = new JTextField();
        JTextField titTF     = new JTextField();
        JTextField autorTF   = new JTextField();
        JTextField cantTF    = new JTextField();
        JComboBox<String> tipoCB   = new JComboBox<>(new String[]{"Libro","Revista","Obra","CD","Tesis"});
        JComboBox<String> idiomaCB = new JComboBox<>(new String[]{"Español","Inglés"});

        if (!"agregar".equals(accion)) {
            String c = JOptionPane.showInputDialog("Código del ejemplar:");
            if (c==null || !catalogo.containsKey(c)) { JOptionPane.showMessageDialog(f,"No encontrado"); return; }
            Material m = catalogo.get(c);
            codTF .setText(c);
            titTF .setText(m.titulo);
            autorTF.setText(m.autor);
            tipoCB .setSelectedItem(m.tipo);
            idiomaCB.setSelectedItem(m.idioma);
        }

        panel.add(new JLabel("Título:"));  panel.add(titTF);
        panel.add(new JLabel("Autor:"));   panel.add(autorTF);
        panel.add(new JLabel("Cantidad:"));panel.add(cantTF);
        panel.add(new JLabel("Tipo:"));    panel.add(tipoCB);
        panel.add(new JLabel("Idioma:"));  panel.add(idiomaCB);

        JButton ok = new JButton(switch (accion) {
            case "agregar" -> "Guardar";
            case "editar"  -> "Actualizar";
            default        -> "Borrar";
        });
        panel.add(ok);

        ok.addActionListener(e -> {
            String tipo   = (String) tipoCB.getSelectedItem();
            String idioma = (String) idiomaCB.getSelectedItem();
            String tit    = titTF.getText();
            String autor  = autorTF.getText();
            int cant;
            try { cant = cantTF.getText().isEmpty()?1:Integer.parseInt(cantTF.getText()); }
            catch(NumberFormatException ex){ cant=1; }

            if ("agregar".equals(accion)) {
                for (int i=0;i<cant;i++){
                    String codigo = generarCodigo(tipo);
                    catalogo.put(codigo,new Material(codigo,tit,autor,tipo,idioma));
                }
                JOptionPane.showMessageDialog(f,"Se agregaron "+cant+" unidades.");
            } else {
                String c = codTF.getText();
                if ("editar".equals(accion)) {
                    catalogo.put(c,new Material(c,tit,autor,tipo,idioma));
                    JOptionPane.showMessageDialog(f,"Actualizado.");
                } else { // borrar
                    catalogo.remove(c);
                    JOptionPane.showMessageDialog(f,"Eliminado.");
                }
            }
            f.dispose();
        });

        f.setVisible(true);
    }

    private void mostrarCatalogoTabla() {
        String[] col = {"Código","Título","Autor","Tipo","Idioma"};
        DefaultTableModel m = new DefaultTableModel(col,0);
        catalogo.values().forEach(mat ->
                m.addRow(new Object[]{mat.codigo,mat.titulo,mat.autor,mat.tipo,mat.idioma}));

        JTable tabla = new JTable(m);
        JFrame t = new JFrame("Ejemplares");
        t.setSize(600,400);
        t.setLocationRelativeTo(null);
        t.add(new JScrollPane(tabla));
        t.setVisible(true);
    }

    private void mostrarPrestamo() {
        JFrame pr = new JFrame("Prestar libro");
        pr.setSize(400,180);
        pr.setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        pr.add(p);

        JLabel l  = new JLabel("Código del libro a prestar:");
        JTextField codigoTF = new JTextField(12);
        JButton ok = new JButton("Prestar");

        p.add(l); p.add(codigoTF); p.add(ok);
        pr.setVisible(true);

        ok.addActionListener(e -> {
            String codigo = codigoTF.getText().trim();
            /* validaciones */
            if (!catalogo.containsKey(codigo))              { msg(pr,"Código no válido."); return; }
            if (usuariosConMora.getOrDefault(usuarioActual,false)) { msg(pr,"Tienes mora."); return; }
            if (prestamos.containsKey(codigo))              { msg(pr,"Ya está prestado."); return; }

            List<String> lista = prestamosPorUser.computeIfAbsent(usuarioActual, k->new ArrayList<>());
            if (lista.size()>=LIMITE_PRESTAMOS)             { msg(pr,"Límite "+LIMITE_PRESTAMOS+"."); return; }
            lista.add(codigo);
            prestamos.put(codigo,"prestado");

            msg(pr,"Préstamo registrado.");
            pr.dispose();
        });
    }

    private void msg(Component c, String s){ JOptionPane.showMessageDialog(c,s); }

    private String generarCodigo(String tipo){
        contadorPorTipo.merge(tipo,1,Integer::sum);
        return tipo.substring(0,1).toUpperCase()+contadorPorTipo.get(tipo);
    }

    public static class Material {
        public final String codigo,titulo,autor,tipo,idioma;
        public Material(String c,String t,String a,String ti,String id){
            codigo=c; titulo=t; autor=a; tipo=ti; idioma=id;
        }
    }
}

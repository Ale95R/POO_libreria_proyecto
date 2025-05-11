import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;


public class CatalogoConsulta extends JFrame {

    public static final Map<String,String>             adminCreds       = new HashMap<>();
    public static final Map<String,String>             profCreds        = new HashMap<>();
    public static final Map<String,String>             alumnoCreds      = new HashMap<>();
    public static final Map<String,Material>           catalogo         = new HashMap<>();
    public static final Map<String,Integer>            contadorPorTipo  = new HashMap<>();
    public static final Map<String,List<String>>       prestamosPorUser = new HashMap<>();
    public static final Map<String,Prestamo>           prestamos        = new HashMap<>();
    public static final Map<String,Double>             deudaPorUsuario  = new HashMap<>();

    public static final int LIMITE_PRESTAMOS = 3;
    public static final int DIAS_PRESTAMO    = 7;

    private String usuarioActual;
    private String tipoActual;   // Administrador | Profesor | Alumno

    /* ════════════════════════════════════════
                      main
       ════════════════════════════════════════ */
    public static void main(String[] args) {

        adminCreds.put("admin","123");
        profCreds .put("prof","123");
        alumnoCreds.put("alumno1","123");
        alumnoCreds.put("alumno2","123");
        alumnoCreds.put("alumno3","123");

        SwingUtilities.invokeLater(() -> new CatalogoConsulta().mostrarLogin());
    }

    /* ════════════════════════════════════════
                     LOGIN
       ════════════════════════════════════════ */
    public void mostrarLogin() {
        JFrame login = new JFrame("Login");
        login.setSize(600,260);
        login.setDefaultCloseOperation(EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(6,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

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
        form.add(loginBtn); form.add(msg);
        login.add(form); login.setVisible(true);

        loginBtn.addActionListener(e -> {
            String tipo = (String) tipoCB.getSelectedItem();
            String usr  = userTF.getText().trim();
            String pass = new String(passTF.getPassword());

            if (autenticar(tipo,usr,pass)) {
                usuarioActual = usr;
                tipoActual    = tipo;
                login.dispose();
                mostrarMenuPrincipal();
            } else msg.setText("Credenciales incorrectas.");
        });
    }
    private boolean autenticar(String tipo,String u,String p){
        return switch (tipo) {
            case "Administrador" -> p.equals(adminCreds .getOrDefault(u,""));
            case "Profesor"      -> p.equals(profCreds  .getOrDefault(u,""));
            case "Alumno"        -> p.equals(alumnoCreds.getOrDefault(u,""));
            default              -> false;
        };
    }

    /* ════════════════════════════════════════
                MENÚ PRINCIPAL
       ════════════════════════════════════════ */
    private void mostrarMenuPrincipal() {
        JFrame menu = new JFrame("Catálogo – "+tipoActual+" ("+usuarioActual+")");
        menu.setSize(650,340); menu.setLocationRelativeTo(null);
        menu.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        menu.add(p);

        JLabel t = new JLabel("Consulta al catálogo");
        t.setAlignmentX(CENTER_ALIGNMENT);
        t.setFont(t.getFont().deriveFont(Font.BOLD,18f));
        p.add(t); p.add(Box.createVerticalStrut(10));

        Dimension btn = new Dimension(220,32);

        if ("Administrador".equals(tipoActual)) {
            addButton(p,"Ingresar nuevo ejemplar",btn,e->mostrarFormulario("agregar"));
            addButton(p,"Editar ejemplar",        btn,e->mostrarFormulario("editar"));
            addButton(p,"Borrar ejemplar",        btn,e->mostrarFormulario("borrar"));
        }

        if (!"Administrador".equals(tipoActual)) {
            addButton(p,"Prestar libro",      btn,e->mostrarPrestamo());
            addButton(p,"Devolver libro",     btn,e->new DevolverLibro(usuarioActual).setVisible(true));
            addButton(p,"Ver mis préstamos",  btn,e->new MostrarDevolucion(usuarioActual).setVisible(true));
            addButton(p,"Ver mora",           btn,e->mostrarMora());
        }

        addButton(p,"Ver ejemplares",btn,e->mostrarCatalogoTabla());

        JButton salir = new JButton("Cerrar sesión");
        salir.setForeground(Color.RED);
        salir.setMaximumSize(btn); salir.setAlignmentX(CENTER_ALIGNMENT);
        salir.addActionListener(e->{ menu.dispose(); mostrarLogin(); });
        p.add(Box.createVerticalStrut(15)); p.add(salir);

        menu.setVisible(true);
    }
    private void addButton(JPanel pan,String txt,Dimension d,java.awt.event.ActionListener al){
        JButton b = new JButton(txt);
        b.setMaximumSize(d); b.setAlignmentX(CENTER_ALIGNMENT); b.addActionListener(al);
        pan.add(b);
    }

    /* ════════════════════════════════════════
          FORMULARIO ingreso / edición / borrado
       ════════════════════════════════════════ */
    private void mostrarFormulario(String accion){
        JFrame f = new JFrame(accion.toUpperCase()+" ejemplar");
        f.setSize(500,300); f.setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(6,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18)); f.add(p);

        JTextField codTF = new JTextField();
        JTextField titTF = new JTextField();
        JTextField autTF = new JTextField();
        JTextField cntTF = new JTextField();
        JComboBox<String> tipoCB = new JComboBox<>(new String[]{"Libro","Revista","CD","DVD"});
        JComboBox<String> idiCB  = new JComboBox<>(new String[]{"Español","Inglés"});

        if (!"agregar".equals(accion)) {
            String c = JOptionPane.showInputDialog("Código del ejemplar:");
            if (c==null || !catalogo.containsKey(c)){ msg(f,"No encontrado"); return; }
            Material m = catalogo.get(c);
            codTF.setText(c); titTF.setText(m.titulo); autTF.setText(m.autor);
            tipoCB.setSelectedItem(m.tipo); idiCB.setSelectedItem(m.idioma);
        }

        p.add(new JLabel("Título:"));  p.add(titTF);
        p.add(new JLabel("Autor:"));   p.add(autTF);
        p.add(new JLabel("Cantidad:"));p.add(cntTF);
        p.add(new JLabel("Tipo:"));    p.add(tipoCB);
        p.add(new JLabel("Idioma:"));  p.add(idiCB);

        JButton ok = new JButton( switch(accion){
            case "agregar"->"Guardar"; case "editar"->"Actualizar"; default->"Borrar";
        });
        p.add(ok);

        ok.addActionListener(e->{
            String tipo=(String)tipoCB.getSelectedItem();
            String idi =(String)idiCB .getSelectedItem();
            String tit =titTF.getText();
            String aut =autTF.getText();
            int cant;
            try{ cant = cntTF.getText().isEmpty()?1:Integer.parseInt(cntTF.getText()); }
            catch(NumberFormatException ex){ cant=1; }

            if ("agregar".equals(accion)){
                for(int i=0;i<cant;i++){
                    String codigo = generarCodigo(tipo);
                    registrarMaterial(new Material(codigo,tit,aut,tipo,idi));
                }
                msg(f,"Se agregaron "+cant+" unidades.");
            } else {
                String c=codTF.getText();
                if("editar".equals(accion)){
                    registrarMaterial(new Material(c,tit,aut,tipo,idi));
                    msg(f,"Actualizado.");
                } else {
                    catalogo.remove(c); msg(f,"Eliminado.");
                }
            }
            f.dispose();
        });
        f.setVisible(true);
    }

    private void registrarMaterial(Material m){ catalogo.put(m.codigo,m); }

    /* ════════════════════════════════════════
                 TABLA de ejemplares
       ════════════════════════════════════════ */
    private void mostrarCatalogoTabla(){
        String[] col={"Código","Título","Autor","Tipo","Idioma"};
        DefaultTableModel m = new DefaultTableModel(col,0);
        catalogo.values().forEach(x-> m.addRow(new Object[]{x.codigo,x.titulo,x.autor,x.tipo,x.idioma}));

        JFrame t = new JFrame("Ejemplares"); t.setSize(600,400); t.setLocationRelativeTo(null);
        t.add(new JScrollPane(new JTable(m))); t.setVisible(true);
    }

    /* ════════════════════════════════════════
                       PRÉSTAMO
       ════════════════════════════════════════ */
    private void mostrarPrestamo(){
        JFrame pr = new JFrame("Prestar libro");
        pr.setSize(400,180); pr.setLocationRelativeTo(null);

        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); pr.add(p);

        JTextField codTF = new JTextField(12);
        p.add(new JLabel("Código del libro a prestar:")); p.add(codTF);
        JButton ok = new JButton("Prestar"); p.add(ok);
        pr.setVisible(true);

        ok.addActionListener(e->{
            String codigo = codTF.getText().trim();

            if (!catalogo.containsKey(codigo)){ msg(pr,"Código no válido."); return; }
            if (deudaPorUsuario.getOrDefault(usuarioActual,0.0)>0){ msg(pr,"Tienes mora pendiente."); return; }
            if (prestamos.containsKey(codigo)){ msg(pr,"Ya está prestado."); return; }

            List<String> lista = prestamosPorUser.computeIfAbsent(usuarioActual,k->new ArrayList<>());
            if (lista.size()>=LIMITE_PRESTAMOS){ msg(pr,"Límite "+LIMITE_PRESTAMOS+"."); return; }

            LocalDate hoy  = LocalDate.now();
            LocalDate devo = hoy.plusDays(DIAS_PRESTAMO);
            Prestamo pmo   = new Prestamo(usuarioActual,catalogo.get(codigo).titulo,hoy,devo);
            prestamos.put(codigo,pmo); lista.add(codigo);

            msg(pr,"Préstamo registrado. Devuelve antes del "+devo+".");
            pr.dispose();
        });
    }

    /* ════════════════════════════════════════
                         MORA
       ════════════════════════════════════════ */
    private void mostrarMora(){
        double deuda = deudaPorUsuario.getOrDefault(usuarioActual,0.0);
        msg(this,deuda==0 ? "No tienes mora pendiente." :
                "Tu mora acumulada es de $"+String.format("%.2f",deuda));
    }

    /* ════════════════════════════════════════
                  utilidades varias
       ════════════════════════════════════════ */
    private void msg(Component c,String s){ JOptionPane.showMessageDialog(c,s); }

    private String generarCodigo(String tipo){

        String pref = switch(tipo){
            case "Libro"   -> "LIB";
            case "Revista" -> "REV";
            case "CD"      -> "CDA";
            case "DVD"     -> "DVD";
            default        -> "XXX";
        };
        int num = contadorPorTipo.merge(pref,1,Integer::sum);
        return pref+String.format("%05d",num);
    }

    public static class Material{
        public final String codigo,titulo,autor,tipo,idioma;
        public Material(String c,String t,String a,String ti,String id){
            codigo=c; titulo=t; autor=a; tipo=ti; idioma=id;
        }
    }
}

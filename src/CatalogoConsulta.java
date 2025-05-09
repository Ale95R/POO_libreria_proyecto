import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Agregar esta importación
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CatalogoConsulta extends JFrame {

    private static HashMap<String, String> adminCreds = new HashMap<>();
    private static HashMap<String, String> profCreds = new HashMap<>();
    private static HashMap<String, String> alumnoCreds = new HashMap<>();
    private static HashMap<String, Material> catalogo = new HashMap<>();
    private static Map<String, Integer> contadorPorTipo = new HashMap<>();
    private static Map<String, String> prestamos = new HashMap<>();
    private static Map<String, Boolean> usuariosConMora = new HashMap<>();

    public static void main(String[] args) {
        adminCreds.put("admin", "123");
        profCreds.put("prof", "123");
        alumnoCreds.put("alumno1", "123");
        alumnoCreds.put("alumno2", "123");
        alumnoCreds.put("alumno3", "123");
        usuariosConMora.put("alumno1", false);
        usuariosConMora.put("alumno2", false);
        usuariosConMora.put("alumno3", true); // Simulamos que este usuario tiene mora
        usuariosConMora.put("prof", false);   // También puede aplicar a profesores
        SwingUtilities.invokeLater(() -> new CatalogoConsulta().mostrarLogin());
    }

    public void mostrarLogin() {
        JFrame loginFrame = new JFrame("Login de Usuarios");
        loginFrame.setSize(600, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tipoLabel = new JLabel("Tipo de usuario:");
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Administrador", "Profesor", "Alumno"});

        JLabel userLabel = new JLabel("Usuario:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Contraseña:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Iniciar sesión");

        JLabel mensaje = new JLabel("");
        mensaje.setForeground(Color.RED);

        JButton olvidarContraseñaBtn = new JButton("¿Olvidaste la contraseña?");

        formPanel.add(tipoLabel);
        formPanel.add(tipoCombo);
        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(new JLabel());
        formPanel.add(loginBtn);
        formPanel.add(mensaje);

        formPanel.add(new JLabel());
        formPanel.add(olvidarContraseñaBtn);

        JPanel contenedor = new JPanel(new BorderLayout());

        try {
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("logo_udb.png"));
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel imagenLabel = new JLabel(new ImageIcon(imagenEscalada));
            imagenLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            contenedor.add(imagenLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.out.println("⚠️ Imagen no encontrada: logo_udb.png");
        }

        contenedor.add(formPanel, BorderLayout.CENTER);
        loginFrame.add(contenedor);
        loginFrame.setVisible(true);

        loginBtn.addActionListener(e -> {
            String tipo = (String) tipoCombo.getSelectedItem();
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (autenticar(tipo, user, pass)) {
                loginFrame.dispose();
                mostrarCatalogo(tipo);
            } else {
                mensaje.setText("Usuario o contraseña incorrectos.");
            }
        });

        olvidarContraseñaBtn.addActionListener(e -> mostrarOlvidoContraseña());
    }

    private boolean autenticar(String tipo, String user, String pass) {
        switch (tipo) {
            case "Administrador":
                return adminCreds.containsKey(user) && adminCreds.get(user).equals(pass);
            case "Profesor":
                return profCreds.containsKey(user) && profCreds.get(user).equals(pass);
            case "Alumno":
                return alumnoCreds.containsKey(user) && alumnoCreds.get(user).equals(pass);
            default:
                return false;
        }
    }

    private void mostrarOlvidoContraseña() {
        JFrame olvidoFrame = new JFrame("Olvidaste la contraseña");
        olvidoFrame.setSize(400, 200);
        olvidoFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Ingresa tu nombre de usuario:");
        JTextField userField = new JTextField(15);

        JButton solicitarBtn = new JButton("Solicitar contraseña");

        panel.add(label);
        panel.add(userField);
        panel.add(solicitarBtn);

        olvidoFrame.add(panel);
        olvidoFrame.setVisible(true);

        solicitarBtn.addActionListener(e -> {
            String usuario = userField.getText();
            if (adminCreds.containsKey(usuario) || profCreds.containsKey(usuario) || alumnoCreds.containsKey(usuario)) {
                JOptionPane.showMessageDialog(olvidoFrame, "Tu contraseña será enviada a tu correo universitario en 24 horas por el departamento de administración.");
            } else {
                JOptionPane.showMessageDialog(olvidoFrame, "Usuario no encontrado.");
            }
            olvidoFrame.dispose();
        });
    }

    public void mostrarCatalogo(String tipoUsuario) {
        JFrame frame = new JFrame("Catálogo - " + tipoUsuario);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel("Consulta al catálogo");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(tituloLabel);

        Dimension buttonSize = new Dimension(200, 30);

        if (tipoUsuario.equals("Administrador")) {
            JButton btnAgregar = new JButton("Ingresar nuevo ejemplar");
            btnAgregar.setPreferredSize(buttonSize);
            btnAgregar.setMaximumSize(buttonSize);
            btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAgregar.addActionListener(e -> mostrarFormulario("agregar"));
            panel.add(btnAgregar);

            JButton btnEditar = new JButton("Editar ejemplar");
            btnEditar.setPreferredSize(buttonSize);
            btnEditar.setMaximumSize(buttonSize);
            btnEditar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnEditar.addActionListener(e -> mostrarFormulario("editar"));
            panel.add(btnEditar);

            JButton btnBorrar = new JButton("Borrar ejemplar");
            btnBorrar.setPreferredSize(buttonSize);
            btnBorrar.setMaximumSize(buttonSize);
            btnBorrar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnBorrar.addActionListener(e -> mostrarFormulario("borrar"));
            panel.add(btnBorrar);
        }

        if (tipoUsuario.equals("Profesor") || tipoUsuario.equals("Alumno")) {
            JButton btnPrestar = new JButton("Prestar libro");
            btnPrestar.setPreferredSize(buttonSize);
            btnPrestar.setMaximumSize(buttonSize);
            btnPrestar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnPrestar.addActionListener(e -> mostrarPrestamo());
            panel.add(btnPrestar);
        }

        JButton btnVer = new JButton("Ver ejemplares");
        btnVer.setPreferredSize(buttonSize);
        btnVer.setMaximumSize(buttonSize);
        btnVer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVer.addActionListener(e -> mostrarCatalogoLista());
        panel.add(btnVer);

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setPreferredSize(buttonSize);
        btnCerrarSesion.setMaximumSize(buttonSize);
        btnCerrarSesion.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrarSesion.setForeground(Color.RED);
        btnCerrarSesion.addActionListener(e -> {
            frame.dispose();
            mostrarLogin();
        });
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnCerrarSesion);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void mostrarFormulario(String accion) {
        JFrame frame = new JFrame("Formulario - " + accion.toUpperCase());
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField codField = new JTextField();
        JTextField tituloField = new JTextField();
        JTextField autorField = new JTextField();
        JTextField cantidadField = new JTextField();
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Libro", "Revista", "Obra", "CD", "Tesis"});
        JComboBox<String> idiomaCombo = new JComboBox<>(new String[]{"Español", "Inglés"});

        if (!accion.equals("agregar")) {
            String cod = JOptionPane.showInputDialog("Ingrese el código de identificación interna:");
            if (cod == null || !catalogo.containsKey(cod)) {
                JOptionPane.showMessageDialog(null, "Código no encontrado.");
                return;
            }
            Material mat = catalogo.get(cod);
            codField.setText(cod);
            tituloField.setText(mat.titulo);
            autorField.setText(mat.autor);
            tipoCombo.setSelectedItem(mat.tipo);
            idiomaCombo.setSelectedItem(mat.idioma);
        }

        panel.add(new JLabel("Título:"));
        panel.add(tituloField);
        panel.add(new JLabel("Autor:"));
        panel.add(autorField);
        panel.add(new JLabel("Cantidad:"));
        panel.add(cantidadField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        panel.add(new JLabel("Idioma:"));
        panel.add(idiomaCombo);

        JButton btn = new JButton(accion.equals("agregar") ? "Guardar" : (accion.equals("editar") ? "Actualizar" : "Borrar"));
        btn.addActionListener(e -> {
            String tipo = (String) tipoCombo.getSelectedItem();
            String idioma = (String) idiomaCombo.getSelectedItem();
            String titulo = tituloField.getText();
            String autor = autorField.getText();
            String cantidadText = cantidadField.getText();
            int cantidad = 1;

            try {
                if (!cantidadText.isEmpty()) {
                    cantidad = Integer.parseInt(cantidadText);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Cantidad inválida. Se agregará 1 unidad por defecto.");
            }

            if (accion.equals("agregar")) {
                for (int i = 0; i < cantidad; i++) {
                    String codigo = generarCodigo(tipo);
                    catalogo.put(codigo, new Material(codigo, titulo, autor, tipo, idioma));
                }
                JOptionPane.showMessageDialog(frame, "Se agregaron " + cantidad + " unidades.");
            } else if (accion.equals("editar")) {
                String cod = codField.getText();
                catalogo.put(cod, new Material(cod, titulo, autor, tipo, idioma));
                JOptionPane.showMessageDialog(frame, "Ejemplar actualizado.");
            } else if (accion.equals("borrar")) {
                String cod = codField.getText();
                catalogo.remove(cod);
                JOptionPane.showMessageDialog(frame, "Ejemplar eliminado.");
            }
            frame.dispose();
        });

        panel.add(btn);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void mostrarCatalogoLista() {
        String[] columnas = {"Código", "Título", "Autor", "Tipo", "Idioma"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        for (Material m : catalogo.values()) {
            model.addRow(new Object[]{m.codigo, m.titulo, m.autor, m.tipo, m.idioma});
        }

        JTable tabla = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabla);
        JFrame tablaFrame = new JFrame("Listado de Ejemplares");
        tablaFrame.setSize(600, 400);
        tablaFrame.setLocationRelativeTo(null);
        tablaFrame.add(scrollPane);
        tablaFrame.setVisible(true);
    }

    private void mostrarPrestamo() {
        JFrame prestamoFrame = new JFrame("Préstamo de libros");
        prestamoFrame.setSize(400, 250);
        prestamoFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel userLabel = new JLabel("Ingresa tu nombre de usuario:");
        JTextField userField = new JTextField(15);

        JLabel label = new JLabel("Ingresa el código de identificación interna del libro:");
        JTextField codigoField = new JTextField(15);

        JButton prestarBtn = new JButton("Prestar");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(label);
        panel.add(codigoField);
        panel.add(prestarBtn);

        prestamoFrame.add(panel);
        prestamoFrame.setVisible(true);

        prestarBtn.addActionListener(e -> {
            String usuario = userField.getText().trim();
            String codigo = codigoField.getText().trim();

            boolean registrado = alumnoCreds.containsKey(usuario) || profCreds.containsKey(usuario);
            boolean tieneMora = usuariosConMora.getOrDefault(usuario, false);

            if (!registrado) {
                JOptionPane.showMessageDialog(prestamoFrame, "Usuario no registrado.");
                return;
            }

            if (tieneMora) {
                JOptionPane.showMessageDialog(prestamoFrame, "No puedes realizar préstamos debido a una mora.");
                return;
            }

            if (catalogo.containsKey(codigo) && !prestamos.containsKey(codigo)) {
                prestamos.put(codigo, "prestado");
                JOptionPane.showMessageDialog(prestamoFrame, "El libro ha sido prestado.");
            } else {
                JOptionPane.showMessageDialog(prestamoFrame, "Código no válido o libro ya prestado.");
            }
        });
    }

    private String generarCodigo(String tipo) {
        contadorPorTipo.putIfAbsent(tipo, 0);
        contadorPorTipo.put(tipo, contadorPorTipo.get(tipo) + 1);
        return tipo.substring(0, 1).toUpperCase() + contadorPorTipo.get(tipo);
    }

    static class Material {
        String codigo;
        String titulo;
        String autor;
        String tipo;
        String idioma;

        Material(String codigo, String titulo, String autor, String tipo, String idioma) {
            this.codigo = codigo;
            this.titulo = titulo;
            this.autor = autor;
            this.tipo = tipo;
            this.idioma = idioma;
        }
    }
}

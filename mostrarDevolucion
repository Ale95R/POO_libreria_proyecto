private void mostrarDevolucion() {
    JFrame devolucionFrame = new JFrame("Devolver libro");
    devolucionFrame.setSize(300, 150);
    devolucionFrame.setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JLabel label = new JLabel("Ingrese el código del libro a devolver:");
    JTextField codigoField = new JTextField(15);

    JButton devolverBtn = new JButton("Devolver");

    panel.add(label);
    panel.add(codigoField);
    panel.add(devolverBtn);

    devolucionFrame.add(panel);
    devolucionFrame.setVisible(true);

    devolverBtn.addActionListener(e -> {
        String codigo = codigoField.getText();
        if (prestamos.containsKey(codigo)) {
            List<String> prestamosUsuario = prestamosPorUsuario.get(usuarioActual);
if (prestamosUsuario != null && prestamosUsuario.contains(codigo)) {
    prestamosUsuario.remove(codigo);
    JOptionPane.showMessageDialog(devolucionFrame, "Libro devuelto exitosamente.");
} else {
    JOptionPane.showMessageDialog(devolucionFrame, "No tienes este libro en préstamo.");
}

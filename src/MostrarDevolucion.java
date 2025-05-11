import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MostrarDevolucion extends JFrame {

    public MostrarDevolucion(String usuario){
        setTitle("Mis préstamos – "+usuario);
        setSize(600,300); setLocationRelativeTo(null);

        String[] col = {"Código","Título","Vence","Acción"};
        DefaultTableModel m = new DefaultTableModel(col,0){ @Override public boolean isCellEditable(int r,int c){ return c==3; }};

        List<String> lista = CatalogoConsulta.prestamosPorUser.get(usuario);
        if (lista!=null){
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lista.forEach(cod->{
                Prestamo p = CatalogoConsulta.prestamos.get(cod);
                m.addRow(new Object[]{cod,p.getTituloMaterial(),p.getFechaDevolucion().format(fmt),"Devolver"});
            });
        }

        JTable t = new JTable(m);
        t.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        t.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(codigo->{
            new DevolverLibro(usuario); dispose();
        }));

        add(new JScrollPane(t), BorderLayout.CENTER);
        setVisible(true);
    }

    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer{
        public ButtonRenderer(){ setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
            setText(v==null?"":v.toString()); return this;
        }
    }
    static class ButtonEditor extends DefaultCellEditor{
        private final java.util.function.Consumer<String> onClick; private String codigo;
        public ButtonEditor(java.util.function.Consumer<String> oc){ super(new JTextField()); onClick=oc; }
        @Override public Component getTableCellEditorComponent(JTable t,Object v,boolean s,int r,int c){
            codigo = t.getValueAt(r,0).toString();
            JButton b=new JButton(v.toString()); b.addActionListener(e->onClick.accept(codigo));
            return b;
        }
        @Override public Object getCellEditorValue(){ return "Devolver"; }
    }
}

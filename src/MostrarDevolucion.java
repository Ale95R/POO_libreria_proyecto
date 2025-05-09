import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MostrarDevolucion extends JFrame {

    public MostrarDevolucion(String usuario) {
        setTitle("Mis préstamos – "+usuario);
        setSize(500,300);
        setLocationRelativeTo(null);

        String[] col = {"Código","Título","Acción"};
        DefaultTableModel m = new DefaultTableModel(col,0){

            @Override public boolean isCellEditable(int r,int c){ return c==2; }
        };

        List<String> lista = CatalogoConsulta.prestamosPorUser.get(usuario);
        if (lista!=null){
            lista.forEach(cod -> {
                CatalogoConsulta.Material mat = CatalogoConsulta.catalogo.get(cod);
                m.addRow(new Object[]{cod,mat!=null?mat.titulo:"(sin título)","Devolver"});
            });
        }

        JTable t = new JTable(m);
        add(new JScrollPane(t), BorderLayout.CENTER);

        t.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        t.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor((codigo)->{

            new DevolverLibro(usuario);
            dispose();
        }));

        setVisible(true);
    }


    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer{
        public ButtonRenderer(){ setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
            setText((v==null)?"":v.toString()); return this;
        }
    }
    static class ButtonEditor extends DefaultCellEditor{
        private final java.util.function.Consumer<String> onClick;
        private String codigo;
        public ButtonEditor(java.util.function.Consumer<String> oc){ super(new JTextField()); onClick=oc; }
        @Override public Component getTableCellEditorComponent(JTable t,Object v,boolean s,int r,int c){
            codigo = t.getValueAt(r,0).toString();
            JButton b = new JButton(v.toString());
            b.addActionListener(e -> onClick.accept(codigo));
            return b;
        }
        @Override public Object getCellEditorValue(){ return "Devolver"; }
    }
}
package tvsystem.view.gui;

import tvsystem.model.*;
import tvsystem.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Diálogo para mostrar detalles completos de un sector
 * 
 * @author Elias Manriquez
 */
public class SectorDetailDialog extends JDialog {
    private final Sector sector;
    private final ClienteService clienteService;
    private final PlanService planService;
    
    public SectorDetailDialog(JFrame parent, Sector sector, 
                             ClienteService clienteService, 
                             PlanService planService) {
        super(parent, "Detalles del Sector: " + sector.getNombre(), true);
        this.sector = sector;
        this.clienteService = clienteService;
        this.planService = planService;
        
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(800, 600);
        setLayout(new BorderLayout());
        
        // Panel superior con información general del sector
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);
        
        // Panel central con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de clientes
        JPanel clientesPanel = createClientesPanel();
        tabbedPane.addTab("Clientes (" + sector.contarClientes() + ")", clientesPanel);
        
        // Pestaña de estadísticas
        JPanel estadisticasPanel = createEstadisticasPanel();
        tabbedPane.addTab("Estadísticas", estadisticasPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Información del Sector"));
        panel.setBackground(new Color(240, 248, 255));
        
        panel.add(new JLabel("Nombre:"));
        panel.add(new JLabel(sector.getNombre()));
        
        panel.add(new JLabel("Total de Clientes:"));
        panel.add(new JLabel(String.valueOf(sector.contarClientes())));
        
        return panel;
    }
    
    private JPanel createClientesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear tabla de clientes
        String[] columnas = {"RUT", "Nombre", "Dirección", "Plan", "Estado Suscripción", "Precio Mensual"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Llenar tabla con clientes del sector
        List<Cliente> clientes = sector.getClientes();
        for (Cliente cliente : clientes) {
            Object[] fila = {
                cliente.getRut(),
                cliente.getNombre(),
                cliente.getDomicilio(),
                cliente.getSuscripcion() != null ? 
                    cliente.getSuscripcion().getPlan().getNombrePlan() : "Sin plan",
                cliente.getSuscripcion() != null ? 
                    cliente.getSuscripcion().getEstado() : "Sin suscripción",
                cliente.getSuscripcion() != null ? 
                    String.format("$%d", cliente.getSuscripcion().getPlan().calcularPrecioFinal()) : "$0"
            };
            model.addRow(fila);
        }
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Ajustar anchos de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // RUT
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Dirección
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Plan
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Estado
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Precio
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de acciones para clientes
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnVerCliente = new JButton("👁️ Ver Detalles");
        JButton btnEditarCliente = new JButton("✏️ Editar");
        JButton btnEliminarCliente = new JButton("🗑️ Eliminar");
        
        btnVerCliente.setEnabled(false);
        btnEditarCliente.setEnabled(false);
        btnEliminarCliente.setEnabled(false);
        
        // Habilitar botones cuando se seleccione una fila
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = table.getSelectedRow() != -1;
            btnVerCliente.setEnabled(hasSelection);
            btnEditarCliente.setEnabled(hasSelection);
            btnEliminarCliente.setEnabled(hasSelection);
        });
        
        // Acciones de botones
        btnVerCliente.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String rut = (String) model.getValueAt(selectedRow, 0);
                Cliente cliente = clienteService.obtenerClientePorRut(rut);
                if (cliente != null) {
                    JOptionPane.showMessageDialog(this, 
                        cliente.mostrarInfo(true, true), 
                        "Detalles del Cliente", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        btnEditarCliente.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                JOptionPane.showMessageDialog(this, 
                    "Función de edición en desarrollo", 
                    "Próximamente", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnEliminarCliente.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String rut = (String) model.getValueAt(selectedRow, 0);
                String nombre = (String) model.getValueAt(selectedRow, 1);
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar al cliente " + nombre + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = clienteService.eliminarCliente(rut);
                    if (eliminado) {
                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
                        // Actualizar contador en el título de la pestaña
                        updateClientesTab();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al eliminar cliente");
                    }
                }
            }
        });
        
        actionPanel.add(btnVerCliente);
        actionPanel.add(btnEditarCliente);
        actionPanel.add(btnEliminarCliente);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEstadisticasPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        List<Cliente> clientes = sector.getClientes();
        
        // Calcular estadísticas
        int totalClientes = clientes.size();
        int clientesConSuscripcion = 0;
        double ingresosTotales = 0.0;
        
        for (Cliente cliente : clientes) {
            if (cliente.getSuscripcion() != null) {
                clientesConSuscripcion++;
                ingresosTotales += cliente.getSuscripcion().getPlan().calcularPrecioFinal();
            }
        }
        
        int clientesSinSuscripcion = totalClientes - clientesConSuscripcion;
        
        // Crear paneles de estadísticas
        panel.add(createStatPanel("Total de Clientes", String.valueOf(totalClientes), new Color(33, 150, 243)));
        panel.add(createStatPanel("Con Suscripción", String.valueOf(clientesConSuscripcion), new Color(76, 175, 80)));
        panel.add(createStatPanel("Sin Suscripción", String.valueOf(clientesSinSuscripcion), new Color(255, 152, 0)));
        panel.add(createStatPanel("Ingresos Mensuales", String.format("$%.2f", ingresosTotales), new Color(156, 39, 176)));
        
        // Tasa de suscripción
        double tasaSuscripcion = totalClientes > 0 ? (clientesConSuscripcion * 100.0 / totalClientes) : 0;
        panel.add(createStatPanel("Tasa de Suscripción", String.format("%.1f%%", tasaSuscripcion), new Color(0, 150, 136)));
        
        // Promedio de ingresos por cliente
        double promedioIngresos = clientesConSuscripcion > 0 ? (ingresosTotales / clientesConSuscripcion) : 0;
        panel.add(createStatPanel("Promedio por Cliente", String.format("$%.2f", promedioIngresos), new Color(63, 81, 181)));
        
        return panel;
    }
    
    private JPanel createStatPanel(String titulo, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        panel.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblTitulo.setForeground(color);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        lblValor.setForeground(color);
        lblValor.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void updateClientesTab() {
        // Actualizar el título de la pestaña de clientes con el nuevo conteo
        JTabbedPane parent = (JTabbedPane) getContentPane().getComponent(1);
        parent.setTitleAt(0, "Clientes (" + sector.contarClientes() + ")");
    }
}
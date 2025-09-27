package tvsystem.view;

import tvsystem.model.*;
import tvsystem.service.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Dialogo para mostrar detalles completos de un cliente
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class ClienteDetailDialog extends JDialog {
    private final Cliente cliente;
    private final ClienteService clienteService;
    private final PlanService planService;
    private final MainWindow parentWindow;
    
    public ClienteDetailDialog(MainWindow parent, Cliente cliente, 
                              ClienteService clienteService, 
                              PlanService planService) {
        super(parent, "Detalles del Cliente: " + cliente.getNombre(), true);
        this.cliente = cliente;
        this.clienteService = clienteService;
        this.planService = planService;
        this.parentWindow = parent;
        
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(600, 500);
        setLayout(new BorderLayout());
        
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de información general
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("Información General", infoPanel);
        
        // Pestaña de suscripcion
        JPanel suscripcionPanel = createSuscripcionPanel();
        tabbedPane.addTab("Suscripción", suscripcionPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Titulo
        JLabel titleLabel = new JLabel("Información del Cliente");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        JLabel nombreLabel = new JLabel(cliente.getNombre());
        nombreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        panel.add(nombreLabel, gbc);
        
        // RUT
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("RUT:"), gbc);
        gbc.gridx = 1;
        JLabel rutLabel = new JLabel(cliente.getRut());
        rutLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        panel.add(rutLabel, gbc);
        
        // Domicilio
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        JLabel domicilioLabel = new JLabel(cliente.getDomicilio());
        domicilioLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.add(domicilioLabel, gbc);
        
        // Estado general
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        
        // Considerar solo suscripciones ACTIVAS como "Con Suscripción"
        boolean tieneSuscripcionActiva = cliente.getSuscripcion() != null && 
                                        "ACTIVA".equalsIgnoreCase(cliente.getSuscripcion().getEstado());
        String estado = tieneSuscripcionActiva ? "Con Suscripción Activa" : "Sin Suscripción Activa";
        JLabel estadoLabel = new JLabel(estado);
        estadoLabel.setForeground(tieneSuscripcionActiva ? 
            new Color(76, 175, 80) : new Color(244, 67, 54));
        estadoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        panel.add(estadoLabel, gbc);
        
        return panel;
    }
    
    private JPanel createSuscripcionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        if (cliente.getSuscripcion() == null) {
            // Si no tiene suscripción
            JPanel noSuscripcionPanel = new JPanel(new FlowLayout());
            JLabel noSuscripcionLabel = new JLabel("Este cliente no tiene suscripción activa");
            noSuscripcionLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
            noSuscripcionLabel.setForeground(new Color(158, 158, 158));
            noSuscripcionPanel.add(noSuscripcionLabel);
            panel.add(noSuscripcionPanel, BorderLayout.CENTER);
        } else {
            // Panel con detalles de suscripción
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            Suscripcion suscripcion = cliente.getSuscripcion();
            PlanSector plan = suscripcion.getPlan();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            // Titulo
            JLabel titleLabel = new JLabel("Detalles de la Suscripción");
            titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            titleLabel.setForeground(new Color(33, 150, 243));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            detailsPanel.add(titleLabel, gbc);
            
            gbc.gridwidth = 1;
            
            // Plan
            gbc.gridx = 0; gbc.gridy = 1;
            detailsPanel.add(new JLabel("Plan:"), gbc);
            gbc.gridx = 1;
            JLabel planLabel = new JLabel(plan.getNombrePlan());
            planLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            detailsPanel.add(planLabel, gbc);
            
            // Estado
            gbc.gridx = 0; gbc.gridy = 2;
            detailsPanel.add(new JLabel("Estado:"), gbc);
            gbc.gridx = 1;
            JLabel estadoLabel = new JLabel(suscripcion.getEstado());
            Color estadoColor = getColorPorEstado(suscripcion.getEstado());
            estadoLabel.setForeground(estadoColor);
            estadoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            detailsPanel.add(estadoLabel, gbc);
            
            // Fecha inicio
            gbc.gridx = 0; gbc.gridy = 3;
            detailsPanel.add(new JLabel("Fecha Inicio:"), gbc);
            gbc.gridx = 1;
            detailsPanel.add(new JLabel(dateFormat.format(suscripcion.getFechaInicio())), gbc);
            
            // Fecha termino
            gbc.gridx = 0; gbc.gridy = 4;
            detailsPanel.add(new JLabel("Fecha Término:"), gbc);
            gbc.gridx = 1;
            detailsPanel.add(new JLabel(dateFormat.format(suscripcion.getFechaTermino())), gbc);
            
            // Precio mensual
            gbc.gridx = 0; gbc.gridy = 5;
            detailsPanel.add(new JLabel("Precio Mensual:"), gbc);
            gbc.gridx = 1;
            JLabel precioLabel = new JLabel(String.format("$%d", plan.calcularPrecioFinal()));
            precioLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            precioLabel.setForeground(new Color(76, 175, 80));
            detailsPanel.add(precioLabel, gbc);
            
            // Oferta activa
            if (plan.getOfertaActiva()) {
                gbc.gridx = 0; gbc.gridy = 6;
                detailsPanel.add(new JLabel("Descuento:"), gbc);
                gbc.gridx = 1;
                JLabel descuentoLabel = new JLabel(String.format("%.0f%%", plan.getDescuento() * 100));
                descuentoLabel.setForeground(new Color(255, 152, 0));
                descuentoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                detailsPanel.add(descuentoLabel, gbc);
                
                gbc.gridx = 0; gbc.gridy = 7;
                detailsPanel.add(new JLabel("Precio Original:"), gbc);
                gbc.gridx = 1;
                JLabel precioOriginalLabel = new JLabel(String.format("$%d", plan.getPrecioMensual()));
                precioOriginalLabel.setForeground(new Color(158, 158, 158));
                detailsPanel.add(precioOriginalLabel, gbc);
            }
            
            panel.add(detailsPanel, BorderLayout.CENTER);
            
            // Panel de acciones
            JPanel actionPanel = new JPanel(new FlowLayout());
            JButton btnCambiarEstado = new JButton("Cambiar Estado");
            btnCambiarEstado.addActionListener(e -> cambiarEstadoSuscripcion());
            actionPanel.add(btnCambiarEstado);
            
            panel.add(actionPanel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private Color getColorPorEstado(String estado) {
        switch (estado.toUpperCase()) {
            case "ACTIVA":
                return new Color(76, 175, 80);
            case "SUSPENDIDA":
                return new Color(255, 152, 0);
            case "CANCELADA":
                return new Color(244, 67, 54);
            default:
                return new Color(158, 158, 158);
        }
    }
    
    private void cambiarEstadoSuscripcion() {
        String[] opciones = {"ACTIVA", "SUSPENDIDA", "CANCELADA"};
        String estadoActual = cliente.getSuscripcion().getEstado();
        
        String nuevoEstado = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el nuevo estado de la suscripción:",
            "Cambiar Estado",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            estadoActual
        );
        
        if (nuevoEstado != null && !nuevoEstado.equals(estadoActual)) {
            boolean actualizado = clienteService.actualizarEstadoSuscripcion(
                cliente.getRut(), nuevoEstado);
            
            if (actualizado) {
                JOptionPane.showMessageDialog(this, 
                    "Estado actualizado correctamente a: " + nuevoEstado);
                
                // Notificar al MainWindow para actualizar la tabla
                if (parentWindow != null) {
                    parentWindow.actualizarInterfaz();
                }
                
                // Cerrar el dialogo actual y reabrir con datos frescos
                dispose();
                
                // Obtener cliente actualizado
                Cliente clienteActualizado = clienteService.obtenerClientePorRut(cliente.getRut());
                if (clienteActualizado != null) {
                    ClienteDetailDialog nuevoDialog = new ClienteDetailDialog(
                        parentWindow, 
                        clienteActualizado, 
                        clienteService, 
                        planService);
                    nuevoDialog.setVisible(true);
                } else {
                    System.out.println("No se pudo recargar el cliente actualizado");
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar el estado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnCerrar);
        
        return panel;
    }
}
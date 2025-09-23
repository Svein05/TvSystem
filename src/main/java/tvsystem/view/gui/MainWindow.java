package tvsystem.view.gui;

import tvsystem.service.*;
import tvsystem.model.*;
import tvsystem.util.CsvManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Ventana principal de la interfaz gráfica del sistema.
 * Proporciona una interfaz moderna y funcional.
 * 
 * @author Elias Manriquez
 */
public class MainWindow extends JFrame {
    private SectorService sectorService;
    private ClienteService clienteService;
    private PlanService planService;
    private CaptacionService captacionService;
    
    // Componentes de la interfaz
    private JTabbedPane tabbedPane;
    private JTextArea outputArea;
    private JButton btnAgregarCliente;
    private JButton btnBuscarCliente;
    private JButton btnEjecutarCaptacion;
    private JButton btnMostrarReporte;
    
    public MainWindow(SectorService sectorService, 
                     ClienteService clienteService,
                     PlanService planService,
                     CaptacionService captacionService) {
        this.sectorService = sectorService;
        this.clienteService = clienteService;
        this.planService = planService;
        this.captacionService = captacionService;
        
        initComponents();
        setupEventListeners();
        configurarCierreVentana();
    }
    
    /**
     * Configura el manejo del cierre de ventana para guardar datos automáticamente
     */
    private void configurarCierreVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manejarCierreVentana();
            }
        });
    }
    
    /**
     * Maneja el cierre de la ventana con confirmación de guardado
     */
    private void manejarCierreVentana() {
        try {
            if (CsvManager.tieneArchivoSeleccionado()) {
                int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea guardar los cambios antes de salir?\n\nArchivo: " + 
                    new java.io.File(CsvManager.getArchivoActual()).getName(),
                    "Guardar cambios",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (opcion == JOptionPane.CANCEL_OPTION) {
                    return; // No cerrar
                }
                
                if (opcion == JOptionPane.YES_OPTION) {
                    boolean guardado = CsvManager.guardarDatos(sectorService, clienteService, planService);
                    if (guardado) {
                        appendToOutput("💾 Datos guardados correctamente antes de salir.");
                    } else {
                        int confirmar = JOptionPane.showConfirmDialog(
                            this,
                            "Error al guardar. ¿Desea salir sin guardar?",
                            "Error de Guardado",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                        );
                        if (confirmar != JOptionPane.YES_OPTION) {
                            return; // No cerrar
                        }
                    }
                }
            }
            
            System.out.println("👋 Cerrando Sistema de Gestión Televisiva...");
            dispose();
            System.exit(0);
            
        } catch (Exception ex) {
            System.err.println("❌ Error al cerrar la aplicación: " + ex.getMessage());
            ex.printStackTrace();
            dispose();
            System.exit(1);
        }
    }
    
    private void initComponents() {
        setTitle("Sistema de Gestión Televisiva");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel superior con información del archivo y botón guardar
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Crear pestañas
        tabbedPane = new JTabbedPane();
        
        // Pestaña de Clientes
        JPanel clientesPanel = createClientesPanel();
        tabbedPane.addTab("Clientes", clientesPanel);
        
        // Pestaña de Reportes
        JPanel reportesPanel = createReportesPanel();
        tabbedPane.addTab("Reportes", reportesPanel);
        
        // Pestaña de Captación
        JPanel captacionPanel = createCaptacionPanel();
        tabbedPane.addTab("Captación", captacionPanel);
        
        // Área de output
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultado"));
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Mostrar estadísticas iniciales
        mostrarEstadisticasIniciales();
    }
    
    /**
     * Crea el panel superior con información del archivo y botón guardar
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Panel izquierdo con información del archivo
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel archivoLabel = new JLabel();
        
        if (CsvManager.tieneArchivoSeleccionado()) {
            String nombreArchivo = new java.io.File(CsvManager.getArchivoActual()).getName();
            archivoLabel.setText("📁 Archivo: " + nombreArchivo);
            archivoLabel.setToolTipText(CsvManager.getArchivoActual());
        } else {
            archivoLabel.setText("📁 Sin archivo seleccionado");
        }
        
        infoPanel.add(archivoLabel);
        
        // Panel derecho con botón guardar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setToolTipText("Guardar datos en el archivo CSV");
        btnGuardar.addActionListener(e -> guardarDatos());
        
        JButton btnSalir = new JButton("🚪 Salir");
        btnSalir.setToolTipText("Salir del sistema");
        btnSalir.addActionListener(e -> manejarCierreVentana());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnSalir);
        
        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Guarda los datos en el archivo CSV
     */
    private void guardarDatos() {
        if (!CsvManager.tieneArchivoSeleccionado()) {
            JOptionPane.showMessageDialog(
                this,
                "No hay archivo seleccionado para guardar.",
                "Sin archivo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        try {
            boolean exitoso = CsvManager.guardarDatos(sectorService, clienteService, planService);
            
            if (exitoso) {
                appendToOutput("💾 Datos guardados correctamente en: " + 
                    new java.io.File(CsvManager.getArchivoActual()).getName());
                
                JOptionPane.showMessageDialog(
                    this,
                    "Datos guardados correctamente.",
                    "Guardado Exitoso",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar los datos. Revise los logs para más información.",
                    "Error de Guardado",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception e) {
            System.err.println("❌ Error inesperado al guardar: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(
                this,
                "Error inesperado al guardar:\n" + e.getMessage(),
                "Error de Guardado",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private JPanel createClientesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Título
        JLabel titleLabel = new JLabel("Gestión de Clientes");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Botón agregar cliente
        btnAgregarCliente = new JButton("Agregar Cliente");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnAgregarCliente, gbc);
        
        // Botón buscar cliente
        btnBuscarCliente = new JButton("Buscar Cliente por RUT");
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(btnBuscarCliente, gbc);
        
        // Botón listar todos
        JButton btnListarTodos = new JButton("Listar Todos los Clientes");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnListarTodos, gbc);
        
        // Event listener para listar todos
        btnListarTodos.addActionListener(e -> listarTodosLosClientes());
        
        return panel;
    }
    
    private JPanel createReportesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Título
        JLabel titleLabel = new JLabel("Reportes y Consultas");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Botones de reportes
        JButton btnClientesPorSector = new JButton("Clientes por Sector");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnClientesPorSector, gbc);
        
        JButton btnEstadisticasGenerales = new JButton("Estadísticas Generales");
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(btnEstadisticasGenerales, gbc);
        
        JButton btnPlanesPorSector = new JButton("Planes por Sector");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(btnPlanesPorSector, gbc);
        
        JButton btnPlanesConOfertas = new JButton("Planes con Ofertas");
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(btnPlanesConOfertas, gbc);
        
        // Event listeners
        btnClientesPorSector.addActionListener(e -> mostrarClientesPorSector());
        btnEstadisticasGenerales.addActionListener(e -> mostrarEstadisticasGenerales());
        btnPlanesPorSector.addActionListener(e -> mostrarPlanesPorSector());
        btnPlanesConOfertas.addActionListener(e -> mostrarPlanesConOfertas());
        
        return panel;
    }
    
    private JPanel createCaptacionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Título
        JLabel titleLabel = new JLabel("Captación Automática");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Botón ejecutar captación
        btnEjecutarCaptacion = new JButton("Ejecutar Campaña de Captación");
        btnEjecutarCaptacion.setBackground(new Color(76, 175, 80));
        btnEjecutarCaptacion.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnEjecutarCaptacion, gbc);
        
        // Botón mostrar reporte
        btnMostrarReporte = new JButton("Mostrar Reporte de Captación");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(btnMostrarReporte, gbc);
        
        // Botón sectores débiles
        JButton btnSectoresDebiles = new JButton("Identificar Sectores Débiles");
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(btnSectoresDebiles, gbc);
        
        // Botón desactivar ofertas
        JButton btnDesactivarOfertas = new JButton("Desactivar Todas las Ofertas");
        btnDesactivarOfertas.setBackground(new Color(244, 67, 54));
        btnDesactivarOfertas.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnDesactivarOfertas, gbc);
        
        // Event listeners
        btnSectoresDebiles.addActionListener(e -> mostrarSectoresDebiles());
        btnDesactivarOfertas.addActionListener(e -> desactivarTodasLasOfertas());
        
        return panel;
    }
    
    private void setupEventListeners() {
        btnAgregarCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoAgregarCliente();
            }
        });
        
        btnBuscarCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarClientePorRut();
            }
        });
        
        btnEjecutarCaptacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarCampanaCaptacion();
            }
        });
        
        btnMostrarReporte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarReporteCaptacion();
            }
        });
    }
    
    private void mostrarDialogoAgregarCliente() {
        JDialog dialog = new JDialog(this, "Agregar Cliente", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos del formulario
        JTextField txtNombre = new JTextField(20);
        JTextField txtRut = new JTextField(20);
        JTextField txtDomicilio = new JTextField(20);
        
        // ComboBox para sectores
        List<String> sectores = sectorService.obtenerNombresSectores();
        JComboBox<String> cmbSector = new JComboBox<>(sectores.toArray(new String[0]));
        
        // ComboBox para planes (se llenará según el sector seleccionado)
        JComboBox<String> cmbPlan = new JComboBox<>();
        
        // Actualizar planes cuando cambie el sector
        cmbSector.addActionListener(e -> {
            String sectorSeleccionado = (String) cmbSector.getSelectedItem();
            if (sectorSeleccionado != null) {
                cmbPlan.removeAllItems();
                List<PlanSector> planes = planService.obtenerPlanesPorSector(sectorSeleccionado);
                for (PlanSector plan : planes) {
                    cmbPlan.addItem(plan.getCodigoPlan() + " - " + plan.getNombrePlan());
                }
            }
        });
        
        // Trigger inicial para llenar planes
        if (cmbSector.getItemCount() > 0) {
            cmbSector.setSelectedIndex(0);
        }
        
        // Layout del formulario
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("RUT:"), gbc);
        gbc.gridx = 1; panel.add(txtRut, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1; panel.add(txtDomicilio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Sector:"), gbc);
        gbc.gridx = 1; panel.add(cmbSector, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Plan:"), gbc);
        gbc.gridx = 1; panel.add(cmbPlan, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel();
        JButton btnAceptar = new JButton("Agregar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String rut = txtRut.getText().trim();
                String domicilio = txtDomicilio.getText().trim();
                String sector = (String) cmbSector.getSelectedItem();
                String planSeleccionado = (String) cmbPlan.getSelectedItem();
                
                if (nombre.isEmpty() || rut.isEmpty() || domicilio.isEmpty() || 
                    sector == null || planSeleccionado == null) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios");
                    return;
                }
                
                // Extraer código del plan
                String codigoPlan = planSeleccionado.split(" - ")[0];
                
                boolean exitoso = clienteService.agregarCliente(sector, nombre, rut, domicilio, codigoPlan);
                
                if (exitoso) {
                    JOptionPane.showMessageDialog(dialog, "Cliente agregado exitosamente");
                    dialog.dispose();
                    appendToOutput("Cliente " + nombre + " agregado al sector " + sector);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al agregar cliente");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnAceptar);
        buttonPanel.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void buscarClientePorRut() {
        String rut = JOptionPane.showInputDialog(this, "Ingrese el RUT del cliente:");
        if (rut != null && !rut.trim().isEmpty()) {
            Cliente cliente = clienteService.obtenerClientePorRut(rut.trim());
            if (cliente != null) {
                StringBuilder info = new StringBuilder();
                info.append("=== CLIENTE ENCONTRADO ===\n");
                info.append(cliente.mostrarInfo(true, true)).append("\n");
                if (cliente.getSuscripcion() != null) {
                    info.append("Plan: ").append(cliente.getSuscripcion().getPlan().getNombrePlan()).append("\n");
                    info.append("Precio: $").append(cliente.getSuscripcion().getPlan().calcularPrecioFinal());
                }
                appendToOutput(info.toString());
            } else {
                appendToOutput("Cliente con RUT " + rut + " no encontrado.");
            }
        }
    }
    
    private void listarTodosLosClientes() {
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        
        if (clientes.isEmpty()) {
            appendToOutput("No hay clientes registrados.");
            return;
        }
        
        StringBuilder output = new StringBuilder();
        output.append("=== LISTADO DE TODOS LOS CLIENTES ===\n");
        for (Cliente cliente : clientes) {
            output.append(cliente.mostrarInfo(true, true)).append("\n");
            if (cliente.getSuscripcion() != null) {
                output.append("Plan: ").append(cliente.getSuscripcion().getPlan().getNombrePlan()).append("\n");
            }
            output.append("----------------------------------------\n");
        }
        appendToOutput(output.toString());
    }
    
    private void mostrarClientesPorSector() {
        List<String> sectores = sectorService.obtenerNombresSectores();
        String sectorSeleccionado = (String) JOptionPane.showInputDialog(
            this, "Seleccione un sector:", "Clientes por Sector",
            JOptionPane.QUESTION_MESSAGE, null, sectores.toArray(), sectores.get(0));
        
        if (sectorSeleccionado != null) {
            List<Cliente> clientes = clienteService.obtenerClientesPorSector(sectorSeleccionado);
            
            StringBuilder output = new StringBuilder();
            output.append("=== CLIENTES EN ").append(sectorSeleccionado).append(" ===\n");
            
            if (clientes.isEmpty()) {
                output.append("No hay clientes en este sector.");
            } else {
                for (Cliente cliente : clientes) {
                    output.append(cliente.mostrarInfo(true, true)).append("\n");
                    if (cliente.getSuscripcion() != null) {
                        output.append("Plan: ").append(cliente.getSuscripcion().getPlan().getNombrePlan()).append("\n");
                    }
                    output.append("\n");
                }
            }
            appendToOutput(output.toString());
        }
    }
    
    private void mostrarEstadisticasGenerales() {
        StringBuilder output = new StringBuilder();
        output.append("=== ESTADÍSTICAS GENERALES ===\n");
        
        int totalClientes = clienteService.contarClientesTotales();
        int totalSectores = sectorService.obtenerTodosLosSectores().size();
        
        output.append("Total de clientes: ").append(totalClientes).append("\n");
        output.append("Total de sectores: ").append(totalSectores).append("\n");
        
        if (totalSectores > 0) {
            double promedio = (double) totalClientes / totalSectores;
            output.append("Promedio por sector: ").append(String.format("%.2f", promedio)).append("\n");
        }
        
        int totalPlanes = planService.obtenerTodosLosPlanes().size();
        int planesConOferta = planService.obtenerPlanesConOferta().size();
        
        output.append("Total de planes: ").append(totalPlanes).append("\n");
        output.append("Planes con ofertas: ").append(planesConOferta).append("\n");
        
        appendToOutput(output.toString());
    }
    
    private void mostrarPlanesPorSector() {
        List<String> sectores = sectorService.obtenerNombresSectores();
        String sectorSeleccionado = (String) JOptionPane.showInputDialog(
            this, "Seleccione un sector:", "Planes por Sector",
            JOptionPane.QUESTION_MESSAGE, null, sectores.toArray(), sectores.get(0));
        
        if (sectorSeleccionado != null) {
            List<PlanSector> planes = planService.obtenerPlanesPorSector(sectorSeleccionado);
            
            StringBuilder output = new StringBuilder();
            output.append("=== PLANES EN ").append(sectorSeleccionado).append(" ===\n");
            
            for (PlanSector plan : planes) {
                output.append(plan.getCodigoPlan()).append(": ").append(plan.getNombrePlan()).append("\n");
                output.append("  Precio: $").append(plan.getPrecioMensual());
                if (plan.getOfertaActiva()) {
                    output.append(" → $").append(plan.calcularPrecioFinal()).append(" (OFERTA)");
                }
                output.append("\n\n");
            }
            
            appendToOutput(output.toString());
        }
    }
    
    private void mostrarPlanesConOfertas() {
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        
        StringBuilder output = new StringBuilder();
        output.append("=== PLANES CON OFERTAS ACTIVAS ===\n");
        
        if (planesConOferta.isEmpty()) {
            output.append("No hay planes con ofertas activas.");
        } else {
            for (PlanSector plan : planesConOferta) {
                output.append(plan.getCodigoPlan()).append(": ").append(plan.getNombrePlan()).append("\n");
                output.append("  Sector: ").append(plan.getSectorAsociado()).append("\n");
                output.append("  Precio original: $").append(plan.getPrecioMensual()).append("\n");
                output.append("  Precio con oferta: $").append(plan.calcularPrecioFinal()).append("\n");
                output.append("  Descuento: ").append(plan.getDescuento() * 100).append("%\n\n");
            }
        }
        
        appendToOutput(output.toString());
    }
    
    private void ejecutarCampanaCaptacion() {
        // Ejecutar en un thread separado para no bloquear la UI
        SwingUtilities.invokeLater(() -> {
            StringBuilder output = new StringBuilder();
            
            // Capturar la salida del sistema
            List<Sector> sectoresDebiles = captacionService.identificarSectoresParaCaptacion();
            
            output.append("=== CAMPAÑA DE CAPTACIÓN AUTOMÁTICA ===\n");
            output.append("Sectores identificados para captación: ").append(sectoresDebiles.size()).append("\n\n");
            
            if (sectoresDebiles.isEmpty()) {
                output.append("No se encontraron sectores que requieran captación.");
            } else {
                for (Sector sector : sectoresDebiles) {
                    output.append("--- Activando oferta en sector: ").append(sector.getNombre()).append(" ---\n");
                    output.append("Clientes actuales: ").append(sector.contarClientes()).append("\n");
                    
                    // Activar ofertas
                    planService.activarOfertaPorSector(sector.getNombre(), 0.15);
                    
                    output.append("✓ Oferta del 15% activada en todos los planes\n");
                    
                    List<PlanSector> planes = planService.obtenerPlanesPorSector(sector.getNombre());
                    for (PlanSector plan : planes) {
                        output.append("  - ").append(plan.getNombrePlan())
                              .append(": $").append(plan.getPrecioMensual())
                              .append(" → $").append(plan.calcularPrecioFinal())
                              .append(" (Oferta activa)\n");
                    }
                    output.append("\n");
                }
            }
            
            appendToOutput(output.toString());
        });
    }
    
    private void mostrarReporteCaptacion() {
        StringBuilder output = new StringBuilder();
        
        List<Sector> sectoresDebiles = captacionService.identificarSectoresParaCaptacion();
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        
        output.append("=== REPORTE DE CAPTACIÓN ===\n");
        output.append("Sectores débiles identificados: ").append(sectoresDebiles.size()).append("\n");
        output.append("Planes con ofertas activas: ").append(planesConOferta.size()).append("\n");
        
        int potencial = sectoresDebiles.size() * 3;
        output.append("Potencial de captación estimado: ").append(potencial).append(" nuevos clientes\n\n");
        
        if (!sectoresDebiles.isEmpty()) {
            output.append("Sectores débiles:\n");
            for (Sector sector : sectoresDebiles) {
                String estado = sector.contarClientes() <= 1 ? "PRIORITARIO" : "DÉBIL";
                output.append("  - ").append(sector.getNombre())
                      .append(" (").append(sector.contarClientes()).append(" clientes) [").append(estado).append("]\n");
            }
        }
        
        appendToOutput(output.toString());
    }
    
    private void mostrarSectoresDebiles() {
        List<Sector> sectoresDebiles = captacionService.identificarSectoresParaCaptacion();
        
        StringBuilder output = new StringBuilder();
        output.append("=== SECTORES DÉBILES ===\n");
        
        if (sectoresDebiles.isEmpty()) {
            output.append("No hay sectores débiles identificados.");
        } else {
            for (Sector sector : sectoresDebiles) {
                output.append("- ").append(sector.getNombre())
                      .append(": ").append(sector.contarClientes()).append(" clientes\n");
            }
        }
        
        appendToOutput(output.toString());
    }
    
    private void desactivarTodasLasOfertas() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro de desactivar todas las ofertas?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<Sector> sectores = sectorService.obtenerTodosLosSectores();
            
            StringBuilder output = new StringBuilder();
            output.append("=== DESACTIVANDO OFERTAS DE CAPTACIÓN ===\n");
            
            for (Sector sector : sectores) {
                planService.desactivarOfertaPorSector(sector.getNombre());
                output.append("✓ Ofertas desactivadas en sector: ").append(sector.getNombre()).append("\n");
            }
            
            appendToOutput(output.toString());
        }
    }
    
    private void mostrarEstadisticasIniciales() {
        StringBuilder output = new StringBuilder();
        output.append("=== SISTEMA DE GESTIÓN TELEVISIVA ===\n");
        output.append("Sistema inicializado correctamente.\n");
        output.append("Total de sectores: ").append(sectorService.obtenerTodosLosSectores().size()).append("\n");
        output.append("Total de clientes: ").append(clienteService.contarClientesTotales()).append("\n");
        output.append("Total de planes: ").append(planService.obtenerTodosLosPlanes().size()).append("\n");
        output.append("\n¡Bienvenido al sistema!");
        
        appendToOutput(output.toString());
    }
    
    private void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
}
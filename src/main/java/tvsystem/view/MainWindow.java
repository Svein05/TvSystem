
package tvsystem.view;

import tvsystem.service.*;
import tvsystem.model.*;
import tvsystem.util.CsvManager;
import tvsystem.util.LoggerHelper;
import tvsystem.util.FileDialogHelper;
import tvsystem.util.SystemConfigurer;
import tvsystem.config.AppConstants;
import tvsystem.exception.ClienteInvalidoException;
import tvsystem.exception.SectorNoEncontradoException;
import tvsystem.exception.SuscripcionInvalidaException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

/**
 * Ventana principal de la interfaz grafica del sistema.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class MainWindow extends JFrame {
    private SectorService sectorService;
    private ClienteService clienteService;
    private PlanService planService;
    private CaptacionService captacionService;
    private ReportService reportService;
    
    // Componentes de la interfaz
    private JTabbedPane tabbedPane;
    private JPanel chartPanel;
    private JPanel sectoresGridPanel;
    
    // Array de colores para los graficos
    private final Color[] coloresGraficos = AppConstants.COLORES_GRAFICOS;
    
    // Estado del filtro por umbral
    private boolean filtroUmbralAplicado = false;
    private int ultimoUmbralAplicado = -1;
    
    public MainWindow(SectorService sectorService, 
                     ClienteService clienteService,
                     PlanService planService,
                     CaptacionService captacionService) {
        this.sectorService = sectorService;
        this.clienteService = clienteService;
        this.planService = planService;
        this.captacionService = captacionService;
        this.reportService = new ReportService(sectorService, clienteService, planService);
        
        initComponents();
        configurarCierreVentana();
        LoggerHelper.success("Ventana principal inicializada correctamente");
    }
    
    private void configurarCierreVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manejarCierreVentana();
            }
        });
    }
    
    // Maneja el cierre de la ventana con confirmación de guardado
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
                    return; // Cuando el usuario cancela
                }
                
                if (opcion == JOptionPane.YES_OPTION) {
                    boolean guardado = CsvManager.guardarDatos(sectorService, clienteService, planService);
                    if (guardado) {
                        LoggerHelper.info("Datos guardados correctamente antes de salir.");
                    } else {
                        int confirmar = JOptionPane.showConfirmDialog(
                            this,
                            "Error al guardar. ¿Desea salir sin guardar?",
                            "Error de Guardado",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                        );
                        if (confirmar != JOptionPane.YES_OPTION) {
                            return; // Cuando el usuario cancela
                        }
                    }
                }
            }
            
            LoggerHelper.info("Cerrando Sistema de Gestión Televisiva...");
            dispose();
            System.exit(0);
            
        } catch (Exception ex) {
            System.err.println("Error al cerrar la aplicación: " + ex.getMessage());
            ex.printStackTrace();
            dispose();
            System.exit(1);
        }
    }
    
    private void initComponents() {
        try {
            setTitle("Sistema de Gestión Televisiva");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Configurar ventana para pantalla completa maximizada
            setExtendedState(JFrame.MAXIMIZED_BOTH);

            // Tamaño minimo por si no se puede maximizar
            setSize(1200, 800);
            setLocationRelativeTo(null);
            
            // Panel principal
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Panel superior
            JPanel topPanel = createTopPanel();
            mainPanel.add(topPanel, BorderLayout.NORTH);
            
            // Crear pestañas principales
            tabbedPane = new JTabbedPane();
            
            // Pestaña de Gestion de Sectores
            JPanel sectoresPanel = createSectoresPanel();
            tabbedPane.addTab("Gestión de Sectores", sectoresPanel);
            
            // Pestaña de Gestion de Clientes
            JPanel clientesPanel = createClientesPanel();
            tabbedPane.addTab("Gestión de Clientes", clientesPanel);
            
            mainPanel.add(tabbedPane, BorderLayout.CENTER);
            
            add(mainPanel);
            
            // Mostrar estadisticas iniciales en consola
            mostrarEstadisticasIniciales();
            
            // Inicializar filtros después de que todo esté creado
            SwingUtilities.invokeLater(() -> {
                actualizarSectoresGrid(-1); // Actualizar sin filtro inicial
            });
            
        } catch (Exception e) {
            System.err.println("Error al crear la ventana principal: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Crea el panel superior con informacion del archivo y boton guardar
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Panel izquierdo con información del archivo
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel archivoLabel = new JLabel();
        
        if (CsvManager.tieneArchivoSeleccionado()) {
            String nombreArchivo = new java.io.File(CsvManager.getArchivoActual()).getName();
            archivoLabel.setText("Archivo Abierto: " + nombreArchivo);
        } else {
            archivoLabel.setText("Sin archivo seleccionado");
        }
        
        infoPanel.add(archivoLabel);
        
        // Panel derecho con botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Boton Reporte
        JButton btnReporte = new JButton("Reporte");
        SystemConfigurer.configurarBotonConColor(btnReporte, AppConstants.COLOR_BOTON_REPORTE, AppConstants.COLOR_TEXTO_BLANCO);
        btnReporte.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnReporte.setPreferredSize(new Dimension(AppConstants.BOTON_ANCHO_MEDIANO, AppConstants.BOTON_ALTO_MEDIANO));
        
        btnReporte.addActionListener(e -> generarReporteAnalisis());
        
        // Boton Guardar
        JButton btnGuardar = new JButton("Guardar");
        SystemConfigurer.configurarBotonConColor(btnGuardar, AppConstants.COLOR_BOTON_GUARDAR, AppConstants.COLOR_TEXTO_BLANCO);
        btnGuardar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnGuardar.setPreferredSize(new Dimension(AppConstants.BOTON_ANCHO_MEDIANO, AppConstants.BOTON_ALTO_MEDIANO));
        
        btnGuardar.addActionListener(e -> {
            if (CsvManager.tieneArchivoSeleccionado()) {
                boolean guardado = CsvManager.guardarDatos(sectorService, clienteService, planService);
                if (guardado) {
                    JOptionPane.showMessageDialog(this, "Datos guardados correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar los datos");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay archivo seleccionado para guardar");
            }
        });
        
        actionPanel.add(btnReporte);
        actionPanel.add(btnGuardar);
        
        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    // Crea el panel de gestion de sectores
    private JPanel createSectoresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(1000);
        splitPane.setResizeWeight(0.3);
        
        // Panel derecho - Grid de sectores
        JPanel rightPanel = createGridSectoresPanel();
        splitPane.setRightComponent(rightPanel);
        
        // Panel izquierdo - Analisis de sectores
        JPanel leftPanel = createSectoresAnalysisPanel();
        splitPane.setLeftComponent(leftPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Crea el panel de analisis de sectores (mitad izquierda)
    private JPanel createSectoresAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior - Estadisticas basicas
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Panel central - Gráfico de torta mejorado
        chartPanel = createChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Graficos para Analisis"));
        chartPanel.setPreferredSize(new Dimension(400, 250)); // Ajustado para el nuevo layout
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // Panel inferior - Sectores debiles
        JPanel debilesPanel = createSectoresDebilesPanel();
        panel.add(debilesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Resumen General"));
        panel.setPreferredSize(new Dimension(400, 160));
        
        // Calcular estadisticas avanzadas
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        int totalSectores = sectores.size();
        int totalClientes = clienteService.contarClientesTotales();
        
        // Encontrar sector con mas clientes e ingresos
        String sectorMasClientes = "N/A";
        String sectorMasIngresos = "N/A";
        String sectorMenorClientes = "N/A";
        int maxClientes = 0;
        int minClientes = Integer.MAX_VALUE;
        double maxIngresos = 0.0;
        
        for (Sector sector : sectores) {
            if (sector == null) continue;
            
            int clientes = sector.contarClientes();
            double ingresos = calcularIngresosSector(sector);
            
            // Sector con mas clientes
            if (clientes > maxClientes) {
                maxClientes = clientes;
                sectorMasClientes = sector.getNombre() != null ? sector.getNombre() : "Sin nombre";
            }
            
            // Sector con menos clientes
            if (clientes > 0 && clientes < minClientes) {
                minClientes = clientes;
                sectorMenorClientes = sector.getNombre() != null ? sector.getNombre() : "Sin nombre";
            }
            
            // Sector con mas ingresos
            if (ingresos > maxIngresos) {
                maxIngresos = ingresos;
                sectorMasIngresos = sector.getNombre() != null ? sector.getNombre() : "Sin nombre";
            }
        }
        
        // Crear etiquetas con bordes redondeados grises
        JLabel lblTop = new JLabel("Mas Clientes: " + sectorMasClientes + " (" + maxClientes + ")", JLabel.CENTER);
        lblTop.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblTop.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        
        JLabel lblIngresos = new JLabel("Mas Ingresos: " + sectorMasIngresos + " ($" + String.format("%.0f", maxIngresos) + ")", JLabel.CENTER);
        lblIngresos.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblIngresos.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        
        JLabel lblMenor = new JLabel("Menos Clientes: " + sectorMenorClientes + " (" + (minClientes == Integer.MAX_VALUE ? 0 : minClientes) + ")", JLabel.CENTER);
        lblMenor.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblMenor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        
        JLabel lblPromedio = new JLabel("Promedio de Clientes: " + String.format("%.1f", totalClientes / (double)Math.max(totalSectores, 1)) + " clientes", JLabel.CENTER);
        lblPromedio.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblPromedio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        
        panel.add(lblTop);
        panel.add(lblIngresos);
        panel.add(lblMenor);
        panel.add(lblPromedio);
        
        return panel;
    }
    
    // Calcula los ingresos totales de un sector
    private double calcularIngresosSector(Sector sector) {
        if (sector == null) return 0.0;
        
        double ingresos = 0.0;
        try {
            List<Cliente> clientes = sector.getClientes();
            if (clientes != null) {
                for (Cliente cliente : clientes) {
                    if (cliente != null && cliente.getSuscripcion() != null 
                        && cliente.getSuscripcion().getPlan() != null
                        && "ACTIVA".equalsIgnoreCase(cliente.getSuscripcion().getEstado())) {
                        ingresos += cliente.getSuscripcion().getPlan().calcularPrecioFinal();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculando ingresos del sector " + sector.getNombre() + ": " + e.getMessage());
        }
        return ingresos;
    }
    
    private JPanel createSectoresDebilesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Sectores con Pocos Clientes"));
        panel.setPreferredSize(new Dimension(300, 80));
        
        // Panel de controles - filtro y descuento automático
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Umbral mínimo:"));
        
        JSpinner umbralSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        controlPanel.add(umbralSpinner);
        
        JButton btnFiltrar = new JButton("Filtrar");
        controlPanel.add(btnFiltrar);
        
        JButton btnDescuentoAutomatico = new JButton("Aplicar Descuento Automático");
        controlPanel.add(btnDescuentoAutomatico);
        
        // Accion del boton filtrar - solo actualiza el grid visual
        btnFiltrar.addActionListener(e -> {
            int umbral = (Integer) umbralSpinner.getValue();
            // Actualizar grid de sectores con colores criticos
            actualizarSectoresGrid(umbral);
            
            // Marcar que se aplico filtro por umbral
            filtroUmbralAplicado = true;
            ultimoUmbralAplicado = umbral;
        });
        
        // Accion del boton de descuento automático
        btnDescuentoAutomatico.addActionListener(e -> {
            if (filtroUmbralAplicado) {
                // Si ya filtro, aplicar descuento con el ultimo umbral usado
                aplicarDescuentoAutomaticoConResumen(ultimoUmbralAplicado);
            } else {
                // Si no ha filtrado, mostrar dialogo de opciones
                mostrarDialogoOpcionesDescuento();
            }
        });
        
        panel.add(controlPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Crea el panel con grid de botones de sectores (mitad derecha)
    private JPanel createGridSectoresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Acceso Directo a Sectores"));
        
        // Grid de botones de sectores
        sectoresGridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        sectoresGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        actualizarSectoresGrid();
        
        JScrollPane scrollPane = new JScrollPane(sectoresGridPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Actualiza la cuadrícula de sectores con datos frescos
    private void actualizarSectoresGrid() {
        actualizarSectoresGrid(-1); // Sin filtro de umbral
        
        // Resetear estado del filtro
        filtroUmbralAplicado = false;
        ultimoUmbralAplicado = -1;
    }
    
    // Actualiza la cuadrícula de sectores con datos frescos y filtro visual
    private void actualizarSectoresGrid(int umbralCritico) {
        if (sectoresGridPanel == null) {
            LoggerHelper.warning("sectoresGridPanel es null, no se puede actualizar aún");
            return;
        }
        
        sectoresGridPanel.removeAll();
        
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        
        for (Sector sector : sectores) {
            // Agregar informacion del sector
            int clientes = sector.contarClientes();
            double ingresos = calcularIngresosSector(sector);
            
            // Crear boton personalizado con bolita si tiene ingresos
            JButton btnSector;
            if (ingresos > 0) {
                final Color colorSector = obtenerColorSector(sector.getNombre());
                btnSector = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        
                        // Dibujar la bolita en la esquina superior derecha
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        int bolitaSize = 8;
                        int margen = 5;
                        int x = getWidth() - bolitaSize - margen;
                        int y = margen;
                        
                        g2d.setColor(colorSector);
                        g2d.fillOval(x, y, bolitaSize, bolitaSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(x, y, bolitaSize, bolitaSize);
                        
                        g2d.dispose();
                    }
                };
            } else {
                btnSector = new JButton();
            }
            
            btnSector.setPreferredSize(new Dimension(150, 50));
            
            btnSector.setText("<html><center>" + sector.getNombre() + 
                             "<br><small>" + clientes + " clientes</small>" +
                             "<br><small>$" + String.format("%.0f", ingresos) + "</small></center></html>");
            
            // Aplicar color critico si el sector está por debajo del umbral
            if (umbralCritico > 0 && clientes < umbralCritico) {
                btnSector.setBackground(new Color(255, 205, 210)); // Rojo pastel
                btnSector.setOpaque(true);
                btnSector.setBorderPainted(true);
                btnSector.setBorder(BorderFactory.createLineBorder(new Color(244, 67, 54), 2));
            } else {
                // Color normal
                btnSector.setBackground(UIManager.getColor("Button.background"));
                btnSector.setOpaque(false);
                btnSector.setBorderPainted(true);
                btnSector.setBorder(UIManager.getBorder("Button.border"));
            }
            
            // Accion al hacer clic - mostrar ventana del sector
            btnSector.addActionListener(e -> {
                SectorDetailDialog dialog = new SectorDetailDialog(
                    this, sector, clienteService, planService);
                dialog.setVisible(true);
                // Actualizar despues de cerrar el diálogo
                actualizarTodasLasVistas();
            });
            
            sectoresGridPanel.add(btnSector);
        }
        
        sectoresGridPanel.revalidate();
        sectoresGridPanel.revalidate();
        sectoresGridPanel.repaint();
    }
    
    // Crea el panel de gestion de clientes
    private JPanel createClientesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla
        String[] columnas = {"Nombre", "RUT", "Sector", "Plan", "Domicilio", "Estado", "Acciones"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna de acciones es editable
            }
        };
        
        // Crear tabla con el modelo
        JTable clientesTable = new JTable(tableModel);
        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientesTable.setRowHeight(25);
        
        // Configurar columnas
        clientesTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Nombre
        clientesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // RUT
        clientesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Sector
        clientesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Plan
        clientesTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Domicilio
        clientesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Estado
        clientesTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Acciones
        
        // Agregar listener para clicks en la tabla (acciones)
        clientesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = clientesTable.rowAtPoint(e.getPoint());
                int col = clientesTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 6) { // Columna de acciones
                    String accion = (String) tableModel.getValueAt(row, 6);
                    if ("Registrar Pago".equals(accion)) {
                        String rutCliente = (String) tableModel.getValueAt(row, 1);
                        registrarPagoCliente(rutCliente);
                        // Actualizar tabla después del pago
                        actualizarTablaClientes(tableModel);
                    }
                }
            }
        });
        
        // Panel superior con controles - pasar referencias de tabla y modelo
        JPanel topPanel = createClientesTopPanel(tableModel, clientesTable);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Panel central - Lista de clientes
        JPanel clientesListPanel = createClientesListPanel(tableModel, clientesTable);
        panel.add(clientesListPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Crea el panel superior de clientes con acciones principales y filtros funcionales
    private JPanel createClientesTopPanel(javax.swing.table.DefaultTableModel tableModel, JTable clientesTable) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        // Boton principal - Agregar Cliente
        JButton btnAgregarCliente = new JButton("➕ Agregar Cliente");
        btnAgregarCliente.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        SystemConfigurer.configurarBotonConColor(btnAgregarCliente, AppConstants.COLOR_BOTON_GUARDAR, AppConstants.COLOR_TEXTO_BLANCO);
        btnAgregarCliente.setPreferredSize(new Dimension(AppConstants.BOTON_ANCHO_GRANDE, AppConstants.BOTON_ALTO_MEDIANO));
        
        // ComboBox filtrar por
        JLabel lblFiltrar = new JLabel("Filtrar por:");
        String[] filtroOpciones = {"Todos", "Sector", "Plan", "Estado"};
        JComboBox<String> cmbFiltrar = new JComboBox<>(filtroOpciones);
        
        // ComboBox para valor del filtro (se actualiza dinámicamente)
        JComboBox<String> cmbValorFiltro = new JComboBox<>();
        cmbValorFiltro.setVisible(false); // Initially hidden
        
        // ComboBox ordenar por
        JLabel lblOrdenar = new JLabel("Ordenar por:");
        String[] ordenOpciones = {"Nombre", "RUT", "Sector", "Plan"};
        JComboBox<String> cmbOrdenar = new JComboBox<>(ordenOpciones);
        
        // Listener para cambio de filtro
        cmbFiltrar.addActionListener(e -> {
            String filtroSeleccionado = (String) cmbFiltrar.getSelectedItem();
            actualizarValoresFiltro(cmbValorFiltro, filtroSeleccionado);
            cmbValorFiltro.setVisible(!"Todos".equals(filtroSeleccionado));
            panel.revalidate();
            panel.repaint();
        });
        
        // Listener para aplicar filtro
        cmbValorFiltro.addActionListener(e -> {
            String tipoFiltro = (String) cmbFiltrar.getSelectedItem();
            String valorFiltro = (String) cmbValorFiltro.getSelectedItem();
            aplicarFiltro(tableModel, tipoFiltro, valorFiltro);
        });
        
        // Listener para ordenar
        cmbOrdenar.addActionListener(e -> {
            String criterioOrden = (String) cmbOrdenar.getSelectedItem();
            String tipoFiltro = (String) cmbFiltrar.getSelectedItem();
            String valorFiltro = "Todos".equals(tipoFiltro) ? null : (String) cmbValorFiltro.getSelectedItem();
            aplicarFiltroYOrden(tableModel, tipoFiltro, valorFiltro, criterioOrden);
        });
        
        // Accion agregar cliente
        btnAgregarCliente.addActionListener(e -> {
            mostrarDialogoAgregarCliente();
            actualizarTablaClientes(tableModel);
        });
        
        panel.add(btnAgregarCliente);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblFiltrar);
        panel.add(cmbFiltrar);
        panel.add(cmbValorFiltro);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(lblOrdenar);
        panel.add(cmbOrdenar);
        
        // Cargar datos iniciales
        actualizarTablaClientes(tableModel);
        
        return panel;
    }
    
    // Crea el panel de lista de clientes con tabla moderna
    private JPanel createClientesListPanel(javax.swing.table.DefaultTableModel tableModel, JTable clientesTable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVerDetalles = new JButton("Ver Detalles");
        
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnVerDetalles.setEnabled(false);
        
        // Listener para seleccion en la tabla
        clientesTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = clientesTable.getSelectedRow() != -1;
            btnEditar.setEnabled(hasSelection);
            btnEliminar.setEnabled(hasSelection);
            btnVerDetalles.setEnabled(hasSelection);
        });
        
        // Acciones de botones
        btnVerDetalles.addActionListener(e -> {
            int selectedRow = clientesTable.getSelectedRow();
            if (selectedRow != -1) {
                String rut = (String) tableModel.getValueAt(selectedRow, 1);
                Cliente cliente = buscarClientePorRut(rut);
                if (cliente != null) {
                    ClienteDetailDialog dialog = new ClienteDetailDialog(
                        this, cliente, clienteService, planService);
                    dialog.setVisible(true);
                    // actualizarTodasLasVistas(); // Se actualizará desde el diálogo
                }
            }
        });
        
        btnEditar.addActionListener(e -> {
            int selectedRow = clientesTable.getSelectedRow();
            if (selectedRow != -1) {
                String rut = (String) tableModel.getValueAt(selectedRow, 1);
                Cliente cliente = buscarClientePorRut(rut);
                if (cliente != null) {
                    mostrarDialogoEditarCliente(cliente);
                }
            }
        });
        
        btnEliminar.addActionListener(e -> {
            int selectedRow = clientesTable.getSelectedRow();
            if (selectedRow != -1) {
                String nombre = (String) tableModel.getValueAt(selectedRow, 0);
                String rut = (String) tableModel.getValueAt(selectedRow, 1);
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar al cliente " + nombre + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = clienteService.eliminarCliente(rut);
                    if (eliminado) {
                        JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
                        actualizarTablaClientes(tableModel); // Recargar tabla
                        actualizarTodasLasVistas();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al eliminar cliente");
                    }
                }
            }
        });
        
        buttonPanel.add(btnVerDetalles);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Actualiza la tabla de clientes con todos los datos
    private void actualizarTablaClientes(javax.swing.table.DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        
        // Obtener todos los clientes de todos los sectores
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        for (Sector sector : sectores) {
            List<Cliente> clientes = sector.getClientes();
            for (Cliente cliente : clientes) {
                String estado = obtenerEstadoCliente(cliente);
                String accion = "";
                
                // Mostrar boton "Registrar Pago" si está próxima a vencer
                if ("Próxima a Vencer".equals(estado)) {
                    accion = "Registrar Pago";
                }
                
                Object[] fila = {
                    cliente.getNombre(),
                    cliente.getRut(),
                    sector.getNombre(),
                    cliente.getSuscripcion() != null && cliente.getSuscripcion().getPlan() != null ? 
                        cliente.getSuscripcion().getPlan().getNombrePlan() : "Sin plan",
                    cliente.getDomicilio(),
                    estado,
                    accion
                };
                tableModel.addRow(fila);
            }
        }
    }
    
    // Busca un cliente por su RUT en todos los sectores
    private Cliente buscarClientePorRut(String rut) {
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        for (Sector sector : sectores) {
            List<Cliente> clientes = sector.getClientes();
            for (Cliente cliente : clientes) {
                if (cliente.getRut().equals(rut)) {
                    return cliente;
                }
            }
        }
        return null;
    }
    
    // Actualiza los valores disponibles para filtrar segu  n el tipo seleccionado
    private void actualizarValoresFiltro(JComboBox<String> cmbValorFiltro, String tipoFiltro) {
        cmbValorFiltro.removeAllItems();
        
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        java.util.Set<String> valoresUnicos = new java.util.HashSet<>();
        
        switch (tipoFiltro) {
            case "Sector":
                for (Sector sector : sectores) {
                    valoresUnicos.add(sector.getNombre());
                }
                break;
            case "Plan":
                for (Sector sector : sectores) {
                    for (Cliente cliente : sector.getClientes()) {
                        if (cliente.getSuscripcion() != null && cliente.getSuscripcion().getPlan() != null) {
                            String nombrePlan = cliente.getSuscripcion().getPlan().getNombrePlan().toLowerCase();
                            // Agrupar por tipo de plan
                            if (nombrePlan.contains("premium")) {
                                valoresUnicos.add("Premium");
                            } else if (nombrePlan.contains("basico") || nombrePlan.contains("básico")) {
                                valoresUnicos.add("Básico");
                            } else if (nombrePlan.contains("familiar")) {
                                valoresUnicos.add("Familiar");
                            } else {
                                // Para planes que no encajen en las categorías principales
                                valoresUnicos.add("Otros");
                            }
                        } else {
                            valoresUnicos.add("Sin plan");
                        }
                    }
                }
                break;
            case "Estado":
                valoresUnicos.add("Activo");
                valoresUnicos.add("Próxima a Vencer");
                valoresUnicos.add("Suspendido");
                valoresUnicos.add("Cancelado");
                break;
        }
        
        for (String valor : valoresUnicos) {
            cmbValorFiltro.addItem(valor);
        }
    }
    
    // Aplica el filtro seleccionado a la tabla
    private void aplicarFiltro(javax.swing.table.DefaultTableModel tableModel, String tipoFiltro, String valorFiltro) {
        aplicarFiltroYOrden(tableModel, tipoFiltro, valorFiltro, null);
    }
    
    // Aplica filtro y ordenamiento a la tabla
    private void aplicarFiltroYOrden(javax.swing.table.DefaultTableModel tableModel, String tipoFiltro, String valorFiltro, String criterioOrden) {
        tableModel.setRowCount(0);
        java.util.List<Object[]> datosFiltrados = new java.util.ArrayList<>();
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        
        for (Sector sector : sectores) {
            List<Cliente> clientes = sector.getClientes();
            for (Cliente cliente : clientes) {
                // Preparar datos de la fila
                String nombreCliente = cliente.getNombre();
                String rutCliente = cliente.getRut();
                String nombreSector = sector.getNombre();
                String nombrePlan = cliente.getSuscripcion() != null && cliente.getSuscripcion().getPlan() != null ? 
                    cliente.getSuscripcion().getPlan().getNombrePlan() : "Sin plan";
                String domicilio = cliente.getDomicilio();
                String estado = obtenerEstadoCliente(cliente);
                
                // Determinar el tipo de plan para filtrado
                String tipoPlan = "Sin plan";
                if (cliente.getSuscripcion() != null && cliente.getSuscripcion().getPlan() != null) {
                    String planLower = cliente.getSuscripcion().getPlan().getNombrePlan().toLowerCase();
                    if (planLower.contains("premium")) {
                        tipoPlan = "Premium";
                    } else if (planLower.contains("basico") || planLower.contains("básico")) {
                        tipoPlan = "Básico";
                    } else if (planLower.contains("familiar")) {
                        tipoPlan = "Familiar";
                    } else {
                        tipoPlan = "Otros";
                    }
                }
                
                // Aplicar filtro
                boolean incluir = true;
                if (tipoFiltro != null && !"Todos".equals(tipoFiltro) && valorFiltro != null) {
                    switch (tipoFiltro) {
                        case "Sector":
                            incluir = nombreSector.equals(valorFiltro);
                            break;
                        case "Plan":
                            incluir = tipoPlan.equals(valorFiltro);
                            break;
                        case "Estado":
                            incluir = estado.equals(valorFiltro);
                            break;
                    }
                }
                
                if (incluir) {
                    Object[] fila = {nombreCliente, rutCliente, nombreSector, nombrePlan, domicilio, estado};
                    datosFiltrados.add(fila);
                }
            }
        }
        
        // Aplicar ordenamiento si se especifica
        if (criterioOrden != null) {
            datosFiltrados.sort((fila1, fila2) -> {
                String valor1 = "", valor2 = "";
                switch (criterioOrden) {
                    case "Nombre":
                        valor1 = (String) fila1[0];
                        valor2 = (String) fila2[0];
                        break;
                    case "RUT":
                        valor1 = (String) fila1[1];
                        valor2 = (String) fila2[1];
                        break;
                    case "Sector":
                        valor1 = (String) fila1[2];
                        valor2 = (String) fila2[2];
                        break;
                    case "Plan":
                        valor1 = (String) fila1[3];
                        valor2 = (String) fila2[3];
                        break;
                }
                return valor1.compareToIgnoreCase(valor2);
            });
        }
        
        // Agregar filas filtradas y ordenadas a la tabla
        for (Object[] fila : datosFiltrados) {
            tableModel.addRow(fila);
        }
    }
    
    
    private void mostrarDialogoAgregarCliente() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Cliente", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(30);
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("RUT:"), gbc);
        gbc.gridx = 1;
        JTextField txtRut = new JTextField(30);
        panel.add(txtRut, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        JTextField txtDomicilio = new JTextField(30);
        panel.add(txtDomicilio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Sector:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbSector = new JComboBox<>();
        for (Sector sector : sectorService.obtenerTodosLosSectores()) {
            cmbSector.addItem(sector.getNombre());
        }
        panel.add(cmbSector, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Plan:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbPlan = new JComboBox<>();
        panel.add(cmbPlan, gbc);
        
        cmbSector.addActionListener(e -> {
            String sectorSeleccionado = (String) cmbSector.getSelectedItem();
            if (sectorSeleccionado != null) {
                actualizarPlanesPorSector(cmbPlan, sectorSeleccionado);
            }
        });
        
        // Inicializar planes para el primer sector
        if (cmbSector.getItemCount() > 0) {
            cmbSector.setSelectedIndex(0);
            actualizarPlanesPorSector(cmbPlan, (String) cmbSector.getSelectedItem());
        }
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAceptar = new JButton("Aceptar");
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
                
                // Extraer codigo del plan
                String codigoPlan = planSeleccionado.split(" - ")[0];
                
                boolean exitoso = clienteService.agregarCliente(sector, nombre, rut, domicilio, codigoPlan);
                
                if (exitoso) {
                    JOptionPane.showMessageDialog(dialog, "Cliente agregado exitosamente");
                    dialog.dispose();
                    actualizarTodasLasVistas();
                    LoggerHelper.success("Cliente " + nombre + " agregado al sector " + sector);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al agregar cliente");
                }
            } catch (ClienteInvalidoException ex) {
                String mensaje = "Error de validación del cliente:\n" + ex.getMessage();
                if (ex.getCodigoError() != null) {
                    mensaje += "\nCódigo de error: " + ex.getCodigoError();
                }
                JOptionPane.showMessageDialog(dialog, mensaje, "Error de Cliente", JOptionPane.ERROR_MESSAGE);
                LoggerHelper.error("ClienteInvalidoException: " + ex.getMessage());
            } catch (SectorNoEncontradoException ex) {
                String mensaje = "Error de sector:\n" + ex.getMessage();
                if (ex.getSectoresDisponibles() > 0) {
                    mensaje += "\nSectores disponibles: " + ex.getSectoresDisponibles();
                }
                JOptionPane.showMessageDialog(dialog, mensaje, "Sector No Encontrado", JOptionPane.WARNING_MESSAGE);
                LoggerHelper.error("SectorNoEncontradoException: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                LoggerHelper.error("Error inesperado al agregar cliente", ex);
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
    
    // Actualiza el ComboBox de planes segun el sector seleccionado
    private void actualizarPlanesPorSector(JComboBox<String> cmbPlan, String nombreSector) {
        cmbPlan.removeAllItems();
        
        // Buscar el sector por nombre
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        Sector sectorEncontrado = null;
        for (Sector sector : sectores) {
            if (sector.getNombre().equals(nombreSector)) {
                sectorEncontrado = sector;
                break;
            }
        }
        
        if (sectorEncontrado != null) {
            // Obtener planes especificos del sector
            Map<String, PlanSector> planesDelSector = sectorEncontrado.getPlanesDisponibles();
            for (PlanSector plan : planesDelSector.values()) {
                cmbPlan.addItem(plan.getCodigoPlan() + " - " + plan.getNombrePlan());
            }
            
            // Si no hay planes especificos, mostrar todos los planes disponibles
            if (planesDelSector.isEmpty()) {
                for (PlanSector plan : planService.obtenerTodosLosPlanes()) {
                    cmbPlan.addItem(plan.getCodigoPlan() + " - " + plan.getNombrePlan());
                }
            }
        }
    }
    
    private void mostrarEstadisticasIniciales() {
        LoggerHelper.info("=== SISTEMA DE GESTIÓN TELEVISIVA ===");
        LoggerHelper.info("Sistema inicializado correctamente.");
        LoggerHelper.info("Total de sectores: " + sectorService.obtenerTodosLosSectores().size());
        LoggerHelper.info("Total de clientes: " + clienteService.contarClientesTotales());
        LoggerHelper.info("Total de planes: " + planService.obtenerTodosLosPlanes().size());
        LoggerHelper.success("¡Bienvenido al sistema!");
    }
    
    public void actualizarTodasLasVistas() {
        SwingUtilities.invokeLater(() -> {
            // 1. Actualizar grid de sectores
            actualizarSectoresGrid();
            
            // 2. Actualizar panel de estadisticas (recrear el panel)
            JPanel sectoresPanel = createSectoresPanel();
            tabbedPane.setComponentAt(0, sectoresPanel);
            
            // 3. Actualizar panel de clientes (recrear el panel)
            JPanel clientesPanel = createClientesPanel();
            tabbedPane.setComponentAt(1, clientesPanel);
            
            // 4. Revalidar y repintar todo
            tabbedPane.revalidate();
            tabbedPane.repaint();
            
            LoggerHelper.success("Vistas actualizadas correctamente");
        });
    }
    
    // Muestra el dialogo de detalles
    public void mostrarDetallesCliente(Cliente cliente) {
        ClienteDetailDialog dialog = new ClienteDetailDialog(
            this, cliente, clienteService, planService);
        dialog.setVisible(true);
        // actualizarTodasLasVistas(); // Se actualizará desde el diálogo
    }
    
    // Muestra el dialogo para editar un cliente existente
    public void mostrarDialogoEditarCliente(Cliente cliente) {
        JDialog dialog = new JDialog(this, "Editar Cliente", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos del formulario
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(cliente.getNombre(), 20);
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("RUT:"), gbc);
        gbc.gridx = 1;
        JTextField txtRut = new JTextField(cliente.getRut(), 20);
        txtRut.setEditable(false); // RUT no debe ser editable
        txtRut.setBackground(Color.LIGHT_GRAY);
        panel.add(txtRut, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        JTextField txtDomicilio = new JTextField(cliente.getDomicilio(), 20);
        panel.add(txtDomicilio, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String domicilio = txtDomicilio.getText().trim();
                
                if (nombre.isEmpty() || domicilio.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "El nombre y domicilio son obligatorios");
                    return;
                }
                
                // Actualizar el cliente
                cliente.setNombre(nombre);
                cliente.setDomicilio(domicilio);
                
                JOptionPane.showMessageDialog(dialog, "Cliente actualizado exitosamente");
                dialog.dispose();
                actualizarTodasLasVistas();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // Crea un panel con grafico de torta mejorado (circular)
    private JPanel createChartPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Panel izquierdo - Grafico de torta
        JPanel tortaPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                List<Sector> sectores = sectorService.obtenerTodosLosSectores();
                int totalClientes = clienteService.contarClientesTotales();
                
                if (totalClientes == 0) {
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    g2d.drawString("No hay clientes", getWidth() / 2 - 50, getHeight() / 2);
                    g2d.dispose();
                    return;
                }
                
                int diameter = Math.min(getWidth() - 60, getHeight() - 60);
                int x = (getWidth() - diameter) / 2;
                int y = 30;
                
                int startAngle = 0;
                
                for (Sector sector : sectores) {
                    int clientesSector = sector.contarClientes();
                    if (clientesSector > 0) {
                        double porcentaje = (clientesSector * 1.0) / totalClientes;
                        int angle = (int) (porcentaje * 360);
                        
                        // Usar color consistente para este sector
                        Color colorSector = obtenerColorSector(sector.getNombre());
                        g2d.setColor(colorSector);
                        g2d.fillArc(x, y, diameter, diameter, startAngle, angle);
                        
                        g2d.setColor(Color.WHITE);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawArc(x, y, diameter, diameter, startAngle, angle);
                        
                        startAngle += angle;
                    }
                }
                
                g2d.dispose();
            }
        };
        tortaPanel.setBorder(BorderFactory.createTitledBorder("Distribución Clientes"));
        tortaPanel.setPreferredSize(new Dimension(200, 240));
        
        // Panel derecho - Grafico de barras de ingresos
        JPanel barrasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                List<Sector> sectores = sectorService.obtenerTodosLosSectores();
                
                if (sectores.isEmpty()) {
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    g2d.drawString("No hay datos", getWidth() / 2 - 40, getHeight() / 2);
                    g2d.dispose();
                    return;
                }
                
                double maxIngresos = 0;
                for (Sector sector : sectores) {
                    double ingresos = calcularIngresosSector(sector);
                    if (ingresos > maxIngresos) {
                        maxIngresos = ingresos;
                    }
                }
                
                if (maxIngresos == 0) {
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    g2d.drawString("Sin ingresos", getWidth() / 2 - 40, getHeight() / 2);
                    g2d.dispose();
                    return;
                }
                
                int barWidth = Math.max(25, (getWidth() - 60) / Math.max(sectores.size(), 1));
                int maxBarHeight = getHeight() - 80;
                int startX = 30;
                int baseY = getHeight() - 40;
                
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                
                for (Sector sector : sectores) {
                    double ingresos = calcularIngresosSector(sector);
                    if (ingresos > 0) {
                        int barHeight = (int) ((ingresos / maxIngresos) * maxBarHeight);
                        
                        // Usar color consistente para este sector
                        Color colorSector = obtenerColorSector(sector.getNombre());
                        g2d.setColor(colorSector);
                        g2d.fillRect(startX, baseY - barHeight, barWidth - 5, barHeight);
                        
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(startX, baseY - barHeight, barWidth - 5, barHeight);
                    }
                    startX += barWidth;
                }
                
                g2d.dispose();
            }
        };
        barrasPanel.setBorder(BorderFactory.createTitledBorder("Ingresos por Sector"));
        barrasPanel.setPreferredSize(new Dimension(200, 240));
        
        mainPanel.add(tortaPanel);
        mainPanel.add(barrasPanel);
        
        return mainPanel;
    }
    
    private Color obtenerColorSector(String nombreSector) {
        // Usar el hash del nombre para asegurar consistencia
        int hash = Math.abs(nombreSector.hashCode());
        int indiceColor = hash % coloresGraficos.length;
        return coloresGraficos[indiceColor];
    }
    
    // Determina el estado correcto de un cliente basado en su suscripcion
    private String obtenerEstadoCliente(Cliente cliente) {
        if (cliente.getSuscripcion() == null) {
            return "Cancelado";
        }

        String estadoActual = cliente.getSuscripcion().obtenerEstadoActual();
        
        switch (estadoActual) {
            case "ACTIVA":
                return "Activo";
            case "SUSPENDIDA":
                return "Suspendido";
            case "CANCELADA":
                return "Cancelado";
            case "PROXIMA_A_VENCER":
                return "Próxima a Vencer";
            default:
                return "Cancelado";
        }
    }
    
    private void registrarPagoCliente(String rutCliente) {
        Cliente cliente = buscarClientePorRut(rutCliente);
        if (cliente != null && cliente.getSuscripcion() != null) {
            cliente.getSuscripcion().registrarPago();
            
            JOptionPane.showMessageDialog(this,
                "Pago registrado exitosamente para " + cliente.getNombre() + "\n" +
                "Nueva fecha de vencimiento: " + cliente.getSuscripcion().getProximoVencimiento(),
                "Pago Registrado",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo encontrar el cliente o su suscripción",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void actualizarInterfaz() {
        actualizarTablaClientesActiva();
        
        SwingUtilities.invokeLater(() -> {
            actualizarSectoresGrid();
            
            if (tabbedPane.getSelectedIndex() == 1) {
                tabbedPane.getSelectedComponent().revalidate();
                tabbedPane.getSelectedComponent().repaint();
            }
        });
        
        LoggerHelper.debug("Interfaz actualizada desde diálogo");
    }
    

    private void actualizarTablaClientesActiva() {
        Component panelClientes = tabbedPane.getComponentAt(1);
        javax.swing.table.DefaultTableModel tableModel = encontrarTableModelEnPanel(panelClientes);
        
        if (tableModel != null) {
            actualizarTablaClientes(tableModel);
            LoggerHelper.success("Tabla de clientes actualizada directamente");
        } else {
            LoggerHelper.warning("No se encontró la tabla de clientes para actualizar");
        }
    }
    
    private javax.swing.table.DefaultTableModel encontrarTableModelEnPanel(Component component) {
        if (component instanceof JTable) {
            JTable table = (JTable) component;
            if (table.getModel() instanceof javax.swing.table.DefaultTableModel) {
                return (javax.swing.table.DefaultTableModel) table.getModel();
            }
        }
        
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                javax.swing.table.DefaultTableModel result = encontrarTableModelEnPanel(child);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    // Aplica descuento automatico a sectores criticos
    private void mostrarDialogoOpcionesDescuento() {
        String[] opciones = {
            "Filtrar por umbral personalizado", 
            "Aplicar captación de clientes automática (umbral 5)",
            "Cancelar"
        };
        
        String mensaje = "Primero debe filtrar por umbral para aplicar descuento.\n\n" +
                        "Seleccione una opción:";
        
        int seleccion = JOptionPane.showOptionDialog(
            this,
            mensaje,
            "Filtrar antes de aplicar descuento",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        switch (seleccion) {
            case 0: // Filtrar por umbral personalizado
                mostrarDialogoUmbralPersonalizado();
                break;
            case 1: // Aplicar con umbral por defecto (5)
                aplicarDescuentoAutomaticoConResumen(AppConstants.THRESHOLDS.UMBRAL_SECTOR_DEBIL);
                break;
            case 2: // Cancelar
            default:
                // No hacer nada
                break;
        }
    }
    
    // Muestra dialogo para ingresar umbral personalizado
    private void mostrarDialogoUmbralPersonalizado() {
        String input = JOptionPane.showInputDialog(
            this,
            "Ingrese el umbral personalizado (número de clientes mínimo):",
            "Filtro por Umbral Personalizado",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int umbralPersonalizado = Integer.parseInt(input.trim());
                if (umbralPersonalizado < 1) {
                    JOptionPane.showMessageDialog(this,
                        "El umbral debe ser mayor a 0",
                        "Valor inválido",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Aplicar filtro primero
                actualizarSectoresGrid(umbralPersonalizado);
                filtroUmbralAplicado = true;
                ultimoUmbralAplicado = umbralPersonalizado;
                
                // Luego aplicar descuento
                aplicarDescuentoAutomaticoConResumen(umbralPersonalizado);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un número válido",
                    "Valor inválido",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aplicarDescuentoAutomaticoConResumen(int umbral) {
        try {
            List<Sector> sectoresParaCaptacion = captacionService.identificarSectoresParaCaptacion(umbral);
            
            if (sectoresParaCaptacion.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron sectores que requieran captación con umbral de " + umbral + " clientes.", 
                    "Descuento Automático", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Mostrar información de descuentos escalonados
            String mensaje = String.format(
                "Se aplicarán descuentos escalonados a %d sectores:\n\n" +
                "• Sectores muy críticos (0-33%% del umbral): 30%% descuento\n" +
                "• Sectores críticos (34-66%% del umbral): 20%% descuento\n" +
                "• Sectores moderados (67-99%% del umbral): 15%% descuento\n\n" +
                "Umbral utilizado: %d clientes\n¿Continuar?",
                sectoresParaCaptacion.size(), umbral
            );
            
            // Confirmar aplicacion de descuentos
            int confirmacion = JOptionPane.showConfirmDialog(this,
                mensaje,
                "Confirmar Descuento Escalonado",
                JOptionPane.YES_NO_OPTION);
                
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Aplicar descuentos con el umbral personalizado
            captacionService.ejecutarCampanaCaptacionConUmbral(umbral);
            
            // Mostrar ventana de resumen
            mostrarResumenDescuentos(sectoresParaCaptacion, umbral);
            
            // Actualizar la vista
            actualizarSectoresGrid(-1);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al aplicar descuentos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Muestra ventana de resumen con los sectores a los que se aplico el descuento
    private void mostrarResumenDescuentos(List<Sector> sectoresConDescuento, int umbral) {
        // Crear ventana de dialogo
        JDialog dialog = new JDialog(this, "Resumen de Descuentos Escalonados Aplicados", true);
        dialog.setSize(700, 450);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Titulo
        JLabel titulo = new JLabel("Descuentos Escalonados Aplicados por Umbral", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titulo, BorderLayout.NORTH);
        
        // Tabla de sectores
        String[] columnas = {"Sector", "Clientes", "% del Umbral", "Descuento Aplicado", "Categoría"};
        Object[][] datos = new Object[sectoresConDescuento.size()][5];
        
        for (int i = 0; i < sectoresConDescuento.size(); i++) {
            Sector sector = sectoresConDescuento.get(i);
            int clientes = sector.contarClientes();
            double porcentajeUmbral = (double) clientes / umbral * 100;
            
            // Calcular el descuento que se aplicó
            String descuentoTexto;
            String categoria;
            
            if (porcentajeUmbral <= 33) {
                descuentoTexto = "30%";
                categoria = "Muy Crítico";
            } else if (porcentajeUmbral <= 66) {
                descuentoTexto = "20%";
                categoria = "Crítico";
            } else {
                descuentoTexto = "15%";
                categoria = "Moderado";
            }
            
            datos[i][0] = sector.getNombre();
            datos[i][1] = clientes + "/" + umbral;
            datos[i][2] = String.format("%.1f%%", porcentajeUmbral);
            datos[i][3] = descuentoTexto;
            datos[i][4] = categoria;
        }
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new GridLayout(5, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("Total de sectores afectados: " + sectoresConDescuento.size()));
        infoPanel.add(new JLabel("Umbral utilizado: " + umbral + " clientes"));
        infoPanel.add(new JLabel("Descuentos aplicados: 30% (Muy Crítico), 20% (Crítico), 15% (Moderado)"));
        infoPanel.add(new JLabel("Los descuentos se aplicaron a todos los planes de estos sectores."));
        
        // Boton de cerrar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCerrar);
        
        // Panel inferior que contiene info y boton
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    // Genera y exporta un reporte completo
    private void generarReporteAnalisis() {
        String rutaArchivo = FileDialogHelper.seleccionarUbicacionReporte(AppConstants.NOMBRE_REPORTE_DEFAULT);
        
        if (rutaArchivo != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(rutaArchivo))) {
                reportService.generarReporteCompleto(writer);
                
                FileDialogHelper.mostrarInformacion(
                    AppConstants.MSG_REPORTE_GENERADO + "\\n" + rutaArchivo, 
                    AppConstants.TITULO_REPORTE);
                    
                LoggerHelper.success("Reporte generado en: " + rutaArchivo);
                    
            } catch (java.io.IOException e) {
                FileDialogHelper.mostrarError(
                    AppConstants.MSG_ERROR_REPORTE + "\\n" + e.getMessage(), 
                    AppConstants.TITULO_ERROR);
                LoggerHelper.error("Error al generar reporte", e);
            }
        } else {
            LoggerHelper.info("Generación de reporte cancelada");
        }
    }
}
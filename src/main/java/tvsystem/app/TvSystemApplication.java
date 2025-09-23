package tvsystem.app;

import tvsystem.service.*;
import tvsystem.repository.*;
import tvsystem.util.CsvManager;
import tvsystem.view.gui.MainWindow;
import javax.swing.*;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Elias Manriquez
 * @author Maximiliano Rodriguez
 */
public class TvSystemApplication {
    
    private SectorRepository sectorRepository;
    private ClienteRepository clienteRepository;
    private PlanRepository planRepository;
    
    private SectorService sectorService;
    private ClienteService clienteService;
    private PlanService planService;
    private CaptacionService captacionService;
    
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema...");
        
        TvSystemApplication app = new TvSystemApplication();
        
        try {
            app.inicializar();
            app.ejecutarModoGrafico();
            
        } catch (RuntimeException e) {
            System.err.println("\nFALLO CRITICO EN EL ARRANQUE:");
            System.err.println(e.getMessage());
            System.exit(1);
            
        } catch (Exception e) {
            System.err.println("\nERROR INESPERADO:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // Metodo para inicializar los repositorios y servicios.
    public void inicializar() {
        System.out.println("Inicializando Sistema...");
        
        try {
            // Inicialización de repositorios
            System.out.println("-> Inicializando repositorios...");
            sectorRepository = new SectorRepository();
            clienteRepository = new ClienteRepository(sectorRepository);
            planRepository = new PlanRepository(sectorRepository);
            System.out.println("Repositorios inicializados correctamente");
            
            // Inicialización de servicios
            System.out.println("-> Inicializando servicios...");
            sectorService = new SectorService(sectorRepository);
            clienteService = new ClienteService(clienteRepository, sectorRepository, planRepository);
            planService = new PlanService(planRepository);
            captacionService = new CaptacionService(sectorService, planService, clienteService);
            System.out.println("Servicios inicializados correctamente");
            
            // Inicialización de datos del sistema
            System.out.println("-> Cargando datos iniciales...");
            sectorService.inicializarSistema();
            System.out.println("Datos iniciales cargados correctamente");
            
            // Verificación final
            System.out.println("\n=== RESUMEN DE INICIALIZACIÓN ===");
            System.out.println("Sistema inicializado correctamente");
            System.out.println("- Sectores creados: " + sectorService.obtenerTodosLosSectores().size());
            System.out.println("- Planes creados: " + planService.obtenerTodosLosPlanes().size());
            
        } catch (Exception e) {
            System.err.println("\nERROR CRÍTICO durante la inicialización del sistema:");
            System.err.println("Tipo de error: " + e.getClass().getSimpleName());
            System.err.println("Mensaje: " + e.getMessage());
            
            // Log detallado para desarrollo
            System.err.println("\nStack trace completo:");
            e.printStackTrace();
            
            // Limpiar referencias parciales
            limpiarInicializacionParcial();
            
            throw new RuntimeException("Fallo en la inicialización del sistema. " +
                "Verifique la configuración y los logs para más detalles.", e);
        }
    }
    
    // Limpia las referencias en caso de fallo de inicialización
    private void limpiarInicializacionParcial() {
        System.out.println("Limpiando inicialización parcial...");
        
        sectorRepository = null;
        clienteRepository = null;
        planRepository = null;
        sectorService = null;
        clienteService = null;
        planService = null;
        captacionService = null;
        
        System.out.println("- Referencias limpiadas");
    }
    
    // Metodo para determinar de que manera se ejecutara el programa
    // Solo para pruebas
    /*private void ejecutar(String[] args) {
        boolean modoConsola = false;
        
        // Se verifican argumentos de consola
        for (String arg : args) {
            if ("--console".equals(arg) || "-c".equals(arg)) {
                modoConsola = true;
                break;
            }
        }
        
        // Si no los hay, se pregunta mediante una interfaz visual
        // que opcion se escogera
        if (args.length == 0) {
            modoConsola = preguntarModoEjecucion();
        }
        
        if (modoConsola) {
            ejecutarModoConsola();
        } else {
            ejecutarModoGrafico();
        }
    }
    
    
    private boolean preguntarModoEjecucion() {
        String[] opciones = {"Interfaz Gráfica", "Línea de Comandos"};
        int opcion = JOptionPane.showOptionDialog(
            null,
            "Seleccione el modo de ejecución:",
            "Sistema de Gestión Televisiva",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        return opcion == 1; // true si selecciona "Línea de Comandos"
    }
    
    private void ejecutarModoConsola() {
        System.out.println("\n=== MODO CONSOLA ===");
        System.out.println("Iniciando interfaz de línea de comandos...");
        
        try {
            ConsoleInterface consoleInterface = new ConsoleInterface(
                sectorService, 
                clienteService, 
                planService, 
                captacionService
            );
            
            consoleInterface.iniciar();
            
        } catch (Exception e) {
            System.err.println("❌ Error al iniciar modo consola: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo en modo consola", e);
        }
    }
    */
    
    private void ejecutarModoGrafico() {
        System.out.println("\n=== MODO GRAFICO ===");
        System.out.println("Iniciando interfaz grafica...");
        
        try {
            // Configurar Look and Feel
            configurarLookAndFeel();
            
            // Solicitar archivo CSV antes de abrir la interfaz
            if (!CsvManager.seleccionarArchivo()) {
                System.out.println("🚫 Operación cancelada por el usuario");
                return;
            }
            
            // Cargar datos desde CSV
            System.out.println("📂 Cargando datos desde archivo CSV...");
            boolean cargaExitosa = CsvManager.cargarDatos(sectorService, clienteService, planService);
            
            if (!cargaExitosa) {
                int opcion = JOptionPane.showConfirmDialog(
                    null,
                    "Error al cargar el archivo CSV.\n¿Desea continuar con el sistema vacío?",
                    "Error de Carga",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (opcion != JOptionPane.YES_OPTION) {
                    System.out.println("🚫 Operación cancelada por el usuario");
                    return;
                }
            }
            
            // Ejecutar en el Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    MainWindow mainWindow = new MainWindow(
                        sectorService,
                        clienteService,
                        planService,
                        captacionService
                    );
                    
                    mainWindow.setVisible(true);
                    System.out.println("✅ Interfaz grafica iniciada correctamente.");
                    
                    if (CsvManager.tieneArchivoSeleccionado()) {
                        System.out.println("📁 Archivo actual: " + CsvManager.getArchivoActual());
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ Error al crear la ventana principal: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Mostrar diálogo de error
                    JOptionPane.showMessageDialog(
                        null,
                        "Error al iniciar la interfaz gráfica:\n" + e.getMessage(),
                        "Error del Sistema",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            
        } catch (Exception e) {
            System.err.println("❌ Error crítico en modo gráfico: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo en modo gráfico", e);
        }
    }
    
    // Configura el Look and Feel del sistema
    private void configurarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("Look and Feel configurado: Nimbus");
                    return;
                }
            }
            
            // Si Nimbus no está disponible, usar el del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel configurado: Sistema por defecto");
            
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
            System.out.println("Continuando con el Look and Feel por defecto...");
        }
    }
    
    // Métodos para testing y configuración
    
    public SectorService getSectorService() {
        return sectorService;
    }
    
    public ClienteService getClienteService() {
        return clienteService;
    }
    
    public PlanService getPlanService() {
        return planService;
    }
    
    public CaptacionService getCaptacionService() {
        return captacionService;
    }
    
    // Método para cerrar la aplicacion limpiamente
    public void cerrar() {
        System.out.println("Cerrando Sistema...");
        
        // TODO: Guardar datos en csv o excel
        
        System.out.println("Sistema cerrado correctamente.");
        System.exit(0);
    }
    
    /**
     * Método para obtener información del sistema
     */
    public void mostrarInformacionSistema() {
        System.out.println("\n=== INFORMACIÓN DEL SISTEMA ===");
        System.out.println("Sistema de Gestión Televisiva v1.0");
        System.out.println("Sectores configurados: " + sectorService.obtenerTodosLosSectores().size());
        System.out.println("Planes disponibles: " + planService.obtenerTodosLosPlanes().size());
        System.out.println("Clientes registrados: " + clienteService.contarClientesTotales());
        System.out.println("Sectores con ofertas activas: " + 
            planService.obtenerPlanesConOferta().stream()
                .map(plan -> plan.getSectorAsociado())
                .distinct()
                .count());
    }
    
    /**
     * Método para diagnosticar el estado del sistema
     */
    public boolean diagnosticarSistema() {
        System.out.println("\n=== DIAGNÓSTICO DEL SISTEMA ===");
        
        boolean sistemaOk = true;
        
        // Verificar repositories
        if (sectorRepository == null) {
            System.err.println("ERROR: SectorRepository no inicializado");
            sistemaOk = false;
        }
        
        if (clienteRepository == null) {
            System.err.println("ERROR: ClienteRepository no inicializado");
            sistemaOk = false;
        }
        
        if (planRepository == null) {
            System.err.println("ERROR: PlanRepository no inicializado");
            sistemaOk = false;
        }
        
        // Verificar services
        if (sectorService == null) {
            System.err.println("ERROR: SectorService no inicializado");
            sistemaOk = false;
        }
        
        if (clienteService == null) {
            System.err.println("ERROR: ClienteService no inicializado");
            sistemaOk = false;
        }
        
        if (planService == null) {
            System.err.println("ERROR: PlanService no inicializado");
            sistemaOk = false;
        }
        
        if (captacionService == null) {
            System.err.println("ERROR: CaptacionService no inicializado");
            sistemaOk = false;
        }
        
        // Verificar datos básicos
        if (sectorService != null && sectorService.obtenerTodosLosSectores().isEmpty()) {
            System.err.println("WARNING: No hay sectores configurados");
        }
        
        if (planService != null && planService.obtenerTodosLosPlanes().isEmpty()) {
            System.err.println("WARNING: No hay planes configurados");
        }
        
        if (sistemaOk) {
            System.out.println("✓ Sistema funcionando correctamente");
        } else {
            System.err.println("✗ Sistema con errores");
        }
        
        return sistemaOk;
    }
}
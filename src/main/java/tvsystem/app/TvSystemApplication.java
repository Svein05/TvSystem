package tvsystem.app;

import tvsystem.service.*;
import tvsystem.repository.*;
import tvsystem.util.CsvManager;
import tvsystem.util.LoggerHelper;
import tvsystem.util.SystemConfigurer;
import tvsystem.view.MainWindow;
import javax.swing.*;

/**
 * Clase principal de la aplicacion.
 * 
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
        LoggerHelper.system("Iniciando Sistema TV...");
        
        TvSystemApplication app = new TvSystemApplication();
        
        try {
            app.inicializar();
            app.ejecutarModoGrafico();
            
        } catch (RuntimeException e) {
            LoggerHelper.error("FALLO CRITICO EN EL ARRANQUE: " + e.getMessage());
            System.exit(1);
            
        } catch (Exception e) {
            LoggerHelper.error("ERROR INESPERADO", e);
            System.exit(1);
        }
    }
    
    // Metodo para inicializar los repositorios y servicios.
    public void inicializar() {
        LoggerHelper.init("Inicializando Sistema...");
        
        try {
            // Inicialización de repositorios
            LoggerHelper.info("Inicializando repositorios...");
            sectorRepository = new SectorRepository();
            clienteRepository = new ClienteRepository(sectorRepository);
            planRepository = new PlanRepository(sectorRepository);
            LoggerHelper.serviceInitialized("Repositorios");
            
            // Inicialización de servicios
            LoggerHelper.info("Inicializando servicios...");
            sectorService = new SectorService(sectorRepository);
            clienteService = new ClienteService(clienteRepository, sectorRepository, planRepository);
            planService = new PlanService(planRepository);
            captacionService = new CaptacionService(sectorService, planService, clienteService);
            
            // Configurar referencias cruzadas para evitar dependencias circulares
            clienteService.configurarServicios(sectorService, planService);
            LoggerHelper.serviceInitialized("Servicios de negocio");
            
            // Inicialización de datos del sistema
            LoggerHelper.info("Cargando datos iniciales...");
            sectorService.inicializarSistema();
            LoggerHelper.success("Datos iniciales cargados correctamente");
            
            // Verificación final
            LoggerHelper.data("Sectores creados: " + sectorService.obtenerTodosLosSectores().size());
            LoggerHelper.data("Planes creados: " + planService.obtenerTodosLosPlanes().size());
            
        } catch (Exception e) {
            LoggerHelper.error("ERROR CRÍTICO durante la inicialización del sistema", e);
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
        LoggerHelper.warning("Limpiando inicialización parcial...");
        
        sectorRepository = null;
        clienteRepository = null;
        planRepository = null;
        sectorService = null;
        clienteService = null;
        planService = null;
        captacionService = null;
        
        LoggerHelper.debug("- Referencias limpiadas");
    }
    
    private void ejecutarModoGrafico() {
        LoggerHelper.system("=== MODO GRAFICO ===");
        LoggerHelper.info("Iniciando interfaz grafica...");
        
        try {
            // Configurar sistema usando clase especializada
            SystemConfigurer.configurarSistema();
            
            // Solicitar archivo CSV antes de abrir la interfaz
            if (!CsvManager.seleccionarArchivo()) {
                LoggerHelper.warning("Operación cancelada por el usuario");
                return;
            }
            
            // Cargar datos desde CSV
            LoggerHelper.info("Cargando datos desde archivo CSV...");
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
                    LoggerHelper.warning("Operación cancelada por el usuario");
                    return;
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                try {
                    MainWindow mainWindow = new MainWindow(
                        sectorService,
                        clienteService,
                        planService,
                        captacionService
                    );
                    
                    mainWindow.setVisible(true);
                    LoggerHelper.success("Interfaz grafica iniciada correctamente.");
                    
                    if (CsvManager.tieneArchivoSeleccionado()) {
                        LoggerHelper.info("Archivo actual: " + CsvManager.getArchivoActual());
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error al crear la ventana principal: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Mostrar dialogo de error
                    JOptionPane.showMessageDialog(
                        null,
                        "Error al iniciar la interfaz gráfica:\n" + e.getMessage(),
                        "Error del Sistema",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error crítico en modo gráfico: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo en modo gráfico", e);
        }
    }
    
    // Metodo para cerrar la aplicacion limpiamente
    public void cerrar() {
        LoggerHelper.info("Cerrando Sistema...");
        LoggerHelper.info("Sistema cerrado correctamente.");
        System.exit(0);
    }
}
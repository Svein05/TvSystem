package tvsystem.view.cli;

import tvsystem.model.*;
import tvsystem.service.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz de línea de comandos para el sistema de televisión.
 * Extrae la lógica de presentación del Main original.
 * 
 * @author Elias Manriquez
 */
public class ConsoleInterface {
    private SectorService sectorService;
    private ClienteService clienteService;
    private PlanService planService;
    private CaptacionService captacionService;
    private BufferedReader lector;
    
    public ConsoleInterface(SectorService sectorService, 
                           ClienteService clienteService,
                           PlanService planService,
                           CaptacionService captacionService) {
        this.sectorService = sectorService;
        this.clienteService = clienteService;
        this.planService = planService;
        this.captacionService = captacionService;
        this.lector = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void iniciar() {
        mostrarMenu();
    }
    
    private void mostrarMenu() {
        int opcion;
        try {
            do {
                System.out.println("\n=== SISTEMA DE GESTIÓN TELEVISIVA ===");
                System.out.println("1. Gestión de Clientes");
                System.out.println("2. Reportes y Consultas");
                System.out.println("3. Captación y Ofertas");
                System.out.println("4. Gestión de Planes");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                
                opcion = Integer.parseInt(lector.readLine());
                
                switch (opcion) {
                    case 1:
                        mostrarMenuClientes();
                        break;
                    case 2:
                        mostrarMenuReportes();
                        break;
                    case 3:
                        mostrarMenuCaptacion();
                        break;
                    case 4:
                        mostrarMenuPlanes();
                        break;
                    case 0:
                        System.out.println("¡Gracias por usar el sistema!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } while (opcion != 0);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private void mostrarMenuClientes() throws IOException {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Agregar cliente");
            System.out.println("2. Buscar cliente por RUT");
            System.out.println("3. Listar todos los clientes");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            opcion = Integer.parseInt(lector.readLine());
            
            switch (opcion) {
                case 1:
                    agregarCliente();
                    break;
                case 2:
                    buscarClientePorRut();
                    break;
                case 3:
                    listarTodosLosClientes();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
    
    private void mostrarMenuReportes() throws IOException {
        int opcion;
        do {
            System.out.println("\n--- REPORTES Y CONSULTAS ---");
            System.out.println("1. Clientes por sector");
            System.out.println("2. Clientes por plan");
            System.out.println("3. Estadísticas generales");
            System.out.println("4. Sectores con más/menos clientes");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            opcion = Integer.parseInt(lector.readLine());
            
            switch (opcion) {
                case 1:
                    mostrarClientesPorSector();
                    break;
                case 2:
                    mostrarClientesPorPlan();
                    break;
                case 3:
                    mostrarEstadisticasGenerales();
                    break;
                case 4:
                    mostrarEstadisticasSectores();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
    
    private void mostrarMenuCaptacion() throws IOException {
        int opcion;
        do {
            System.out.println("\n--- CAPTACIÓN Y OFERTAS ---");
            System.out.println("1. Ejecutar campaña de captación automática");
            System.out.println("2. Ver reporte de captación");
            System.out.println("3. Identificar sectores débiles");
            System.out.println("4. Desactivar todas las ofertas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            opcion = Integer.parseInt(lector.readLine());
            
            switch (opcion) {
                case 1:
                    captacionService.ejecutarCampanaCaptacion();
                    break;
                case 2:
                    captacionService.mostrarReporteCaptacion();
                    break;
                case 3:
                    mostrarSectoresDebiles();
                    break;
                case 4:
                    captacionService.desactivarOfertasCaptacion();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
    
    private void mostrarMenuPlanes() throws IOException {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE PLANES ---");
            System.out.println("1. Listar todos los planes");
            System.out.println("2. Planes por sector");
            System.out.println("3. Planes con ofertas activas");
            System.out.println("4. Estadísticas de precios");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            opcion = Integer.parseInt(lector.readLine());
            
            switch (opcion) {
                case 1:
                    listarTodosLosPlanes();
                    break;
                case 2:
                    mostrarPlanesPorSector();
                    break;
                case 3:
                    mostrarPlanesConOfertas();
                    break;
                case 4:
                    mostrarEstadisticasPrecios();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
    
    private void agregarCliente() {
        try {
            System.out.println("\n--- AGREGAR CLIENTE ---");
            
            // Mostrar sectores disponibles
            System.out.println("Sectores disponibles:");
            List<String> sectores = sectorService.obtenerNombresSectores();
            for (String sector : sectores) {
                System.out.println("- " + sector);
            }
            
            System.out.print("Ingrese el sector: ");
            String nombreSector = lector.readLine().toUpperCase();
            
            // Mostrar planes disponibles para el sector
            System.out.println("Planes disponibles para " + nombreSector + ":");
            List<PlanSector> planes = planService.obtenerPlanesPorSector(nombreSector);
            for (PlanSector plan : planes) {
                System.out.println("- " + plan.getCodigoPlan() + ": " + plan.getNombrePlan() + 
                                 " ($" + plan.calcularPrecioFinal() + 
                                 (plan.getOfertaActiva() ? " - OFERTA ACTIVA" : "") + ")");
            }
            
            System.out.print("Ingrese el código del plan: ");
            String codigoPlan = lector.readLine().toUpperCase();
            
            // Solicitar datos del cliente
            System.out.print("Ingrese el nombre del cliente: ");
            String nombre = lector.readLine();
            
            System.out.print("Ingrese el RUT del cliente: ");
            String rut = lector.readLine();
            
            System.out.print("Ingrese el domicilio del cliente: ");
            String domicilio = lector.readLine();
            
            // Intentar agregar el cliente
            boolean exitoso = clienteService.agregarCliente(nombreSector, nombre, rut, domicilio, codigoPlan);
            
            if (exitoso) {
                System.out.println("¡Cliente agregado exitosamente al sector " + nombreSector + "!");
            } else {
                System.out.println("Error: No se pudo agregar el cliente.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void buscarClientePorRut() {
        try {
            System.out.print("Ingrese el RUT del cliente: ");
            String rut = lector.readLine();
            
            Cliente cliente = clienteService.obtenerClientePorRut(rut);
            if (cliente != null) {
                System.out.println("\n--- CLIENTE ENCONTRADO ---");
                System.out.println(cliente.mostrarInfo(true, true));
                if (cliente.getSuscripcion() != null) {
                    System.out.println("Plan: " + cliente.getSuscripcion().getPlan().getNombrePlan());
                    System.out.println("Precio: $" + cliente.getSuscripcion().getPlan().calcularPrecioFinal());
                }
            } else {
                System.out.println("Cliente no encontrado.");
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private void listarTodosLosClientes() {
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        
        System.out.println("\n--- LISTADO DE TODOS LOS CLIENTES ---");
        for (Cliente cliente : clientes) {
            System.out.println(cliente.mostrarInfo(true, true));
            if (cliente.getSuscripcion() != null) {
                System.out.println("Plan: " + cliente.getSuscripcion().getPlan().getNombrePlan());
            }
            System.out.println("----------------------------------------");
        }
    }
    
    private void mostrarClientesPorSector() {
        try {
            System.out.println("Sectores con clientes:");
            List<Sector> sectoresConClientes = sectorService.obtenerSectoresConClientes();
            for (Sector sector : sectoresConClientes) {
                System.out.println("- " + sector.getNombre() + " (" + sector.contarClientes() + " clientes)");
            }
            
            System.out.print("Ingrese el sector: ");
            String nombreSector = lector.readLine().toUpperCase();
            
            List<Cliente> clientes = clienteService.obtenerClientesPorSector(nombreSector);
            
            if (clientes.isEmpty()) {
                System.out.println("No hay clientes en el sector " + nombreSector);
                return;
            }
            
            System.out.println("\n--- CLIENTES EN " + nombreSector + " ---");
            for (Cliente cliente : clientes) {
                System.out.println(cliente.mostrarInfo(true, true));
                if (cliente.getSuscripcion() != null) {
                    System.out.println("Plan: " + cliente.getSuscripcion().getPlan().getNombrePlan());
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private void mostrarClientesPorPlan() {
        try {
            System.out.println("Planes disponibles:");
            List<PlanSector> planes = planService.obtenerTodosLosPlanes();
            for (PlanSector plan : planes) {
                System.out.println("- " + plan.getCodigoPlan() + ": " + plan.getNombrePlan());
            }
            
            System.out.print("Ingrese el código del plan: ");
            String codigoPlan = lector.readLine().toUpperCase();
            
            List<Cliente> clientes = clienteService.obtenerClientesPorPlan(codigoPlan);
            
            if (clientes.isEmpty()) {
                System.out.println("No hay clientes con el plan " + codigoPlan);
                return;
            }
            
            System.out.println("\n--- CLIENTES CON PLAN " + codigoPlan + " ---");
            for (Cliente cliente : clientes) {
                System.out.println(cliente.mostrarInfo(true, true));
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private void mostrarEstadisticasGenerales() {
        System.out.println("\n--- ESTADÍSTICAS GENERALES ---");
        
        // Estadísticas de clientes
        int totalClientes = clienteService.contarClientesTotales();
        System.out.println("Total de clientes: " + totalClientes);
        
        // Estadísticas por sector
        int totalSectores = sectorService.obtenerTodosLosSectores().size();
        System.out.println("Total de sectores: " + totalSectores);
        
        if (totalSectores > 0) {
            double promedioPorSector = (double) totalClientes / totalSectores;
            System.out.println("Promedio de clientes por sector: " + String.format("%.2f", promedioPorSector));
        }
        
        // Estadísticas de planes
        int totalPlanes = planService.obtenerTodosLosPlanes().size();
        System.out.println("Total de planes: " + totalPlanes);
        
        int planesConOferta = planService.obtenerPlanesConOferta().size();
        System.out.println("Planes con ofertas activas: " + planesConOferta);
    }
    
    private void mostrarEstadisticasSectores() {
        System.out.println("\n--- ESTADÍSTICAS POR SECTOR ---");
        
        Sector sectorMasClientes = sectorService.obtenerSectorConMasClientes();
        Sector sectorMenosClientes = sectorService.obtenerSectorConMenosClientes();
        
        if (sectorMasClientes != null) {
            System.out.println("Sector con más clientes: " + sectorMasClientes.getNombre() + 
                             " (" + sectorMasClientes.contarClientes() + " clientes)");
        }
        
        if (sectorMenosClientes != null) {
            System.out.println("Sector con menos clientes: " + sectorMenosClientes.getNombre() + 
                             " (" + sectorMenosClientes.contarClientes() + " clientes)");
        }
    }
    
    private void mostrarSectoresDebiles() {
        List<Sector> sectoresDebiles = captacionService.identificarSectoresParaCaptacion();
        
        if (sectoresDebiles.isEmpty()) {
            System.out.println("No hay sectores débiles identificados.");
            return;
        }
        
        System.out.println("\n--- SECTORES DÉBILES ---");
        for (Sector sector : sectoresDebiles) {
            System.out.println("- " + sector.getNombre() + ": " + sector.contarClientes() + " clientes");
        }
    }
    
    private void listarTodosLosPlanes() {
        List<PlanSector> planes = planService.obtenerTodosLosPlanes();
        
        System.out.println("\n--- TODOS LOS PLANES ---");
        for (PlanSector plan : planes) {
            System.out.println(plan.getCodigoPlan() + ": " + plan.getNombrePlan());
            System.out.println("  Sector: " + plan.getSectorAsociado());
            System.out.println("  Precio: $" + plan.getPrecioMensual() + 
                             (plan.getOfertaActiva() ? " → $" + plan.calcularPrecioFinal() + " (OFERTA)" : ""));
            System.out.println();
        }
    }
    
    private void mostrarPlanesPorSector() {
        try {
            System.out.print("Ingrese el sector: ");
            String nombreSector = lector.readLine().toUpperCase();
            
            List<PlanSector> planes = planService.obtenerPlanesPorSector(nombreSector);
            
            if (planes.isEmpty()) {
                System.out.println("No hay planes para el sector " + nombreSector);
                return;
            }
            
            System.out.println("\n--- PLANES EN " + nombreSector + " ---");
            for (PlanSector plan : planes) {
                System.out.println(plan.getCodigoPlan() + ": " + plan.getNombrePlan());
                System.out.println("  Precio: $" + plan.getPrecioMensual() + 
                                 (plan.getOfertaActiva() ? " → $" + plan.calcularPrecioFinal() + " (OFERTA)" : ""));
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private void mostrarPlanesConOfertas() {
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        
        if (planesConOferta.isEmpty()) {
            System.out.println("No hay planes con ofertas activas.");
            return;
        }
        
        System.out.println("\n--- PLANES CON OFERTAS ACTIVAS ---");
        for (PlanSector plan : planesConOferta) {
            System.out.println(plan.getCodigoPlan() + ": " + plan.getNombrePlan());
            System.out.println("  Sector: " + plan.getSectorAsociado());
            System.out.println("  Precio original: $" + plan.getPrecioMensual());
            System.out.println("  Precio con oferta: $" + plan.calcularPrecioFinal());
            System.out.println("  Descuento: " + (plan.getDescuento() * 100) + "%");
            System.out.println();
        }
    }
    
    private void mostrarEstadisticasPrecios() {
        System.out.println("\n--- ESTADÍSTICAS DE PRECIOS ---");
        
        // Esta funcionalidad se implementaría en PlanService
        System.out.println("Estadísticas de precios próximamente disponibles...");
    }
}
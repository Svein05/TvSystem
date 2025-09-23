package tvsystem.service;

import tvsystem.model.*;
import java.util.*;

/**
 * Servicio para gestionar la captación automatica de clientes en sectores débiles.
 * 
 * @author Elias Manriquez
 */
public class CaptacionService {
    private SectorService sectorService;
    private PlanService planService;
    private ClienteService clienteService;
    
    private static final int UMBRAL_SECTOR_DEBIL = 5;
    private static final double DESCUENTO_CAPTACION = 0.15; // 15% de descuento
    
    public CaptacionService(SectorService sectorService, 
                           PlanService planService,
                           ClienteService clienteService) {
        this.sectorService = sectorService;
        this.planService = planService;
        this.clienteService = clienteService;
    }
    
    public List<Sector> identificarSectoresParaCaptacion() {
        return sectorService.identificarSectoresDebiles(UMBRAL_SECTOR_DEBIL);
    }
    
    public void ejecutarCampanaCaptacion() {
        List<Sector> sectoresDebiles = identificarSectoresParaCaptacion();
        
        System.out.println("=== CAMPAÑA DE CAPTACIÓN AUTOMÁTICA ===");
        System.out.println("Sectores identificados para captación: " + sectoresDebiles.size());
        
        for (Sector sector : sectoresDebiles) {
            activarOfertaCaptacion(sector);
        }
        
        if (sectoresDebiles.isEmpty()) {
            System.out.println("No se encontraron sectores que requieran captación.");
        }
    }
    
    private void activarOfertaCaptacion(Sector sector) {
        System.out.println("\n--- Activando oferta en sector: " + sector.getNombre() + " ---");
        System.out.println("Clientes actuales: " + sector.contarClientes());
        
        // Activar descuento en todos los planes del sector
        planService.activarOfertaPorSector(sector.getNombre(), DESCUENTO_CAPTACION);
        
        System.out.println("✓ Oferta del " + (DESCUENTO_CAPTACION * 100) + "% activada en todos los planes");
        
        // Mostrar planes con ofertas activas
        List<PlanSector> planes = planService.obtenerPlanesPorSector(sector.getNombre());
        for (PlanSector plan : planes) {
            System.out.println("  - " + plan.getNombrePlan() + 
                             ": $" + plan.getPrecioMensual() + 
                             " → $" + plan.calcularPrecioFinal() + 
                             " (Oferta activa)");
        }
    }
    
    public void desactivarOfertasCaptacion() {
        List<Sector> todosLosSectores = sectorService.obtenerTodosLosSectores();
        
        System.out.println("=== DESACTIVANDO OFERTAS DE CAPTACIÓN ===");
        
        for (Sector sector : todosLosSectores) {
            planService.desactivarOfertaPorSector(sector.getNombre());
            System.out.println("✓ Ofertas desactivadas en sector: " + sector.getNombre());
        }
    }
    
    public Map<String, Object> generarReporteCaptacion() {
        Map<String, Object> reporte = new HashMap<>();
        
        List<Sector> sectoresDebiles = identificarSectoresParaCaptacion();
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        
        reporte.put("sectoresDebiles", sectoresDebiles);
        reporte.put("cantidadSectoresDebiles", sectoresDebiles.size());
        reporte.put("planesConOferta", planesConOferta);
        reporte.put("cantidadPlanesConOferta", planesConOferta.size());
        
        // Calcular potencial de captación
        int potencialCaptacion = sectoresDebiles.size() * 3; // Estimación: 3 nuevos clientes por sector
        reporte.put("potencialCaptacion", potencialCaptacion);
        
        // Sectores prioritarios (con 0 o 1 cliente)
        List<Sector> sectoresPrioritarios = new ArrayList<>();
        for (Sector sector : sectoresDebiles) {
            if (sector.contarClientes() <= 1) {
                sectoresPrioritarios.add(sector);
            }
        }
        reporte.put("sectoresPrioritarios", sectoresPrioritarios);
        
        return reporte;
    }
    
    public void mostrarReporteCaptacion() {
        Map<String, Object> reporte = generarReporteCaptacion();
        
        System.out.println("\n=== REPORTE DE CAPTACIÓN ===");
        System.out.println("Sectores débiles identificados: " + reporte.get("cantidadSectoresDebiles"));
        System.out.println("Planes con ofertas activas: " + reporte.get("cantidadPlanesConOferta"));
        System.out.println("Potencial de captación estimado: " + reporte.get("potencialCaptacion") + " nuevos clientes");
        
        @SuppressWarnings("unchecked")
        List<Sector> sectoresPrioritarios = (List<Sector>) reporte.get("sectoresPrioritarios");
        if (!sectoresPrioritarios.isEmpty()) {
            System.out.println("\nSectores prioritarios (≤1 cliente):");
            for (Sector sector : sectoresPrioritarios) {
                System.out.println("  - " + sector.getNombre() + " (" + sector.contarClientes() + " clientes)");
            }
        }
        
        // Mostrar penetración por sector
        System.out.println("\nPenetración por sector:");
        Map<String, Integer> estadisticas = sectorService.obtenerEstadisticasPorSector();
        for (Map.Entry<String, Integer> entry : estadisticas.entrySet()) {
            String estado = entry.getValue() < UMBRAL_SECTOR_DEBIL ? "DÉBIL" : "FUERTE";
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " clientes [" + estado + "]");
        }
    }
    
    public boolean requiereCaptacion(String nombreSector) {
        Sector sector = sectorService.obtenerSectorPorNombre(nombreSector);
        return sector != null && sector.esSectorDebil(UMBRAL_SECTOR_DEBIL);
    }
    
    public void configurarUmbralCaptacion(int nuevoUmbral) {
        // En una implementación real, esto se guardaría en configuración
        System.out.println("Umbral de captación configurado a: " + nuevoUmbral + " clientes");
    }
    
    public boolean validarElegibilidadCliente(String rutCliente) {
        // Verificar si un cliente específico es elegible para ofertas de captación
        Cliente cliente = clienteService.obtenerClientePorRut(rutCliente);
        return cliente != null && cliente.getSuscripcion() != null;
    }
}
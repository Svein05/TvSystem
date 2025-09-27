package tvsystem.service;

import tvsystem.model.*;
import tvsystem.config.AppConstants;
import tvsystem.util.LoggerHelper;
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
    
    public CaptacionService(SectorService sectorService, 
                           PlanService planService,
                           ClienteService clienteService) {
        this.sectorService = sectorService;
        this.planService = planService;
        this.clienteService = clienteService;
    }
    
    public List<Sector> identificarSectoresParaCaptacion() {
        return sectorService.identificarSectoresDebiles(AppConstants.THRESHOLDS.UMBRAL_SECTOR_DEBIL);
    }
    
    /**
     * Identifica sectores para captación basado en un umbral personalizado
     */
    public List<Sector> identificarSectoresParaCaptacion(int umbral) {
        return sectorService.identificarSectoresDebiles(umbral);
    }
    
    /**
     * Calcula el descuento apropiado basado en qué tan por debajo del umbral está el sector
     */
    private double calcularDescuentoPorUmbral(int clientesSector, int umbral) {
        if (clientesSector >= umbral) {
            return 0.0; // Sin descuento si está en o por encima del umbral
        }
        
        double porcentajeDelUmbral = (double) clientesSector / umbral;
        
        if (porcentajeDelUmbral <= 0.33) {
            return AppConstants.DISCOUNTS.DESCUENTO_CRITICO;      // 30% - Muy crítico
        } else if (porcentajeDelUmbral <= 0.66) {
            return AppConstants.DISCOUNTS.DESCUENTO_ALTO;         // 20% - Crítico
        } else {
            return AppConstants.DISCOUNTS.DESCUENTO_MODERADO;     // 15% - Moderado
        }
    }
    
    public void ejecutarCampanaCaptacion() {
        ejecutarCampanaCaptacionConUmbral(AppConstants.THRESHOLDS.UMBRAL_SECTOR_DEBIL);
    }
    
    /**
     * Ejecuta campaña de captación con un umbral personalizado
     */
    public void ejecutarCampanaCaptacionConUmbral(int umbral) {
        List<Sector> sectoresDebiles = identificarSectoresParaCaptacion(umbral);
        
        LoggerHelper.info("=== CAMPAÑA DE CAPTACIÓN AUTOMÁTICA ===");
        LoggerHelper.info("Umbral utilizado: " + umbral);
        LoggerHelper.info("Sectores identificados para captación: " + sectoresDebiles.size());
        
        for (Sector sector : sectoresDebiles) {
            activarOfertaCaptacionConUmbral(sector, umbral);
        }
        
        if (sectoresDebiles.isEmpty()) {
            LoggerHelper.info("No se encontraron sectores que requieran captación.");
        }
    }
    
    private void activarOfertaCaptacion(Sector sector) {
        LoggerHelper.info("\n--- Activando oferta en sector: " + sector.getNombre() + " ---");
        LoggerHelper.info("Clientes actuales: " + sector.contarClientes());
        
        // Activar descuento en todos los planes del sector con descuento fijo del 15%
        planService.activarOfertaPorSector(sector.getNombre(), AppConstants.DISCOUNTS.DESCUENTO_MODERADO);
        
        LoggerHelper.info("✓ Oferta del " + (AppConstants.DISCOUNTS.DESCUENTO_MODERADO * 100) + "% activada en todos los planes");
        
        // Mostrar planes con ofertas activas
        List<PlanSector> planes = planService.obtenerPlanesPorSector(sector.getNombre());
        for (PlanSector plan : planes) {
            LoggerHelper.info("  - " + plan.getNombrePlan() + 
                             ": $" + plan.getPrecioMensual() + 
                             " → $" + plan.calcularPrecioFinal() + 
                             " (Oferta activa)");
        }
    }
    
    /**
     * Activa oferta con descuento escalonado basado en el umbral
     */
    private void activarOfertaCaptacionConUmbral(Sector sector, int umbral) {
        int clientesActuales = sector.contarClientes();
        double descuentoCalculado = calcularDescuentoPorUmbral(clientesActuales, umbral);
        
        LoggerHelper.info("\n--- Activando oferta escalonada en sector: " + sector.getNombre() + " ---");
        LoggerHelper.info("Clientes actuales: " + clientesActuales + " / Umbral: " + umbral);
        LoggerHelper.info(String.format("Porcentaje del umbral: %.1f%% → Descuento: %.0f%%", 
                         (double) clientesActuales / umbral * 100, descuentoCalculado * 100));
        
        // Activar descuento calculado en todos los planes del sector
        planService.activarOfertaPorSector(sector.getNombre(), descuentoCalculado);
        
        LoggerHelper.success("✓ Oferta del " + (descuentoCalculado * 100) + "% activada en todos los planes");
        
        // Mostrar planes con ofertas activas
        List<PlanSector> planes = planService.obtenerPlanesPorSector(sector.getNombre());
        for (PlanSector plan : planes) {
            LoggerHelper.info("  - " + plan.getNombrePlan() + 
                             ": $" + plan.getPrecioMensual() + 
                             " → $" + plan.calcularPrecioFinal() + 
                             " (Descuento " + (descuentoCalculado * 100) + "%)");
        }
    }
    
    public void desactivarOfertasCaptacion() {
        List<Sector> todosLosSectores = sectorService.obtenerTodosLosSectores();
        
        LoggerHelper.info("=== DESACTIVANDO OFERTAS DE CAPTACIÓN ===");
        
        for (Sector sector : todosLosSectores) {
            planService.desactivarOfertaPorSector(sector.getNombre());
            LoggerHelper.success("✓ Ofertas desactivadas en sector: " + sector.getNombre());
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
        
        LoggerHelper.info("\n=== REPORTE DE CAPTACIÓN ===");
        LoggerHelper.info("Sectores débiles identificados: " + reporte.get("cantidadSectoresDebiles"));
        LoggerHelper.info("Planes con ofertas activas: " + reporte.get("cantidadPlanesConOferta"));
        LoggerHelper.info("Potencial de captación estimado: " + reporte.get("potencialCaptacion") + " nuevos clientes");
        
        @SuppressWarnings("unchecked")
        List<Sector> sectoresPrioritarios = (List<Sector>) reporte.get("sectoresPrioritarios");
        if (!sectoresPrioritarios.isEmpty()) {
            LoggerHelper.info("\nSectores prioritarios (≤1 cliente):");
            for (Sector sector : sectoresPrioritarios) {
                LoggerHelper.info("  - " + sector.getNombre() + " (" + sector.contarClientes() + " clientes)");
            }
        }
        
        // Mostrar penetración por sector
        LoggerHelper.info("\nPenetración por sector:");
        Map<String, Integer> estadisticas = sectorService.obtenerEstadisticasPorSector();
        for (Map.Entry<String, Integer> entry : estadisticas.entrySet()) {
            String estado = entry.getValue() < AppConstants.THRESHOLDS.UMBRAL_SECTOR_DEBIL ? "DÉBIL" : "FUERTE";
            LoggerHelper.info("  - " + entry.getKey() + ": " + entry.getValue() + " clientes [" + estado + "]");
        }
    }
    
    public boolean requiereCaptacion(String nombreSector) {
        Sector sector = sectorService.buscarSectorPorNombre(nombreSector);
        return sector != null && sector.esSectorDebil(AppConstants.THRESHOLDS.UMBRAL_SECTOR_DEBIL);
    }
    
    public void configurarUmbralCaptacion(int nuevoUmbral) {
        // En una implementación real, esto se guardaría en configuración
        LoggerHelper.info("Umbral de captación configurado a: " + nuevoUmbral + " clientes");
    }
    
    public boolean validarElegibilidadCliente(String rutCliente) {
        // Verificar si un cliente específico es elegible para ofertas de captación
        Cliente cliente = clienteService.obtenerClientePorRut(rutCliente);
        return cliente != null && cliente.getSuscripcion() != null;
    }
}
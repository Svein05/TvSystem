package tvsystem.service;

import tvsystem.model.*;
import tvsystem.config.AppConstants;
import tvsystem.util.LoggerHelper;
import java.util.*;

/**
 * Servicio para gestionar la captación automatica de clientes en sectores débiles.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class CaptacionService {
    private SectorService sectorService;
    private PlanService planService;
    private ClienteService clienteService;
    
    // Constructor
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
    
    // Identifica sectores para captacion basado en un umbral personalizado
    public List<Sector> identificarSectoresParaCaptacion(int umbral) {
        return sectorService.identificarSectoresDebiles(umbral);
    }
    
    // Calcula el descuento apropiado basado en que tan por debajo del umbral está el sector
    private double calcularDescuentoPorUmbral(int clientesSector, int umbral) {
        if (clientesSector >= umbral) {
            return 0.0;
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
    
    // Ejecuta campaña de captación con un umbral personalizado
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
    
    // Activa oferta con descuento escalonado basado en el umbral
    private void activarOfertaCaptacionConUmbral(Sector sector, int umbral) {
        int clientesActuales = sector.contarClientes();
        double descuentoCalculado = calcularDescuentoPorUmbral(clientesActuales, umbral);
        
        LoggerHelper.info("\n--- Activando oferta escalonada en sector: " + sector.getNombre() + " ---");
        LoggerHelper.info("Clientes actuales: " + clientesActuales + " / Umbral: " + umbral);
        LoggerHelper.info(String.format("Porcentaje del umbral: %.1f%% → Descuento: %.0f%%", 
                         (double) clientesActuales / umbral * 100, descuentoCalculado * 100));
        
        // Activar descuento calculado en todos los planes del sector
        planService.activarOfertaPorSector(sector.getNombre(), descuentoCalculado);
        
        LoggerHelper.success("Oferta del " + (descuentoCalculado * 100) + "% activada en todos los planes");
        
        // Mostrar planes con ofertas activas
        List<PlanSector> planes = planService.obtenerPlanesPorSector(sector.getNombre());
        for (PlanSector plan : planes) {
            LoggerHelper.info("  - " + plan.getNombrePlan() + 
                             ": $" + plan.getPrecioMensual() + 
                             " → $" + plan.calcularPrecioFinal() + 
                             " (Descuento " + (descuentoCalculado * 100) + "%)");
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
        
        int potencialCaptacion = sectoresDebiles.size() * 3;
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
}
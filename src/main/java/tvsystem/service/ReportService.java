package tvsystem.service;

import tvsystem.model.*;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servicio especializado en la generación de reportes y análisis del sistema.
 * Extrae toda la lógica de análisis de datos de la capa de presentación.
 * 
 * @author Elias Manriquez
 */
public class ReportService {
    
    private final SectorService sectorService;
    private final ClienteService clienteService;
    private final PlanService planService;
    
    public ReportService(SectorService sectorService, 
                        ClienteService clienteService, 
                        PlanService planService) {
        this.sectorService = sectorService;
        this.clienteService = clienteService;
        this.planService = planService;
    }
    
    /**
     * Genera el contenido completo del reporte de análisis
     */
    public void generarReporteCompleto(PrintWriter writer) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date fechaActual = new Date();
        
        // Cabecera del reporte
        writer.println("═════════════════════════════════════════════════════════════");
        writer.println("           REPORTE DE ANÁLISIS DE SECTORES - TV SYSTEM");
        writer.println("═════════════════════════════════════════════════════════════");
        writer.println("Fecha de generación: " + dateFormat.format(fechaActual));
        writer.println("═════════════════════════════════════════════════════════════");
        writer.println();
        
        // Generar secciones del reporte
        generarResumenEjecutivo(writer);
        generarAnalisisDetallado(writer);
        generarAnalisisPlanes(writer);
        generarRecomendaciones(writer);
        
        writer.println();
        writer.println("═════════════════════════════════════════════════════════════");
        writer.println("                    FIN DEL REPORTE");
        writer.println("═════════════════════════════════════════════════════════════");
    }
    
    /**
     * Genera el resumen ejecutivo con métricas clave
     */
    public void generarResumenEjecutivo(PrintWriter writer) {
        writer.println("📊 RESUMEN EJECUTIVO");
        writer.println("─────────────────────────────────────────────────────────────");
        
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        int totalClientes = clienteService.contarClientesTotales();
        int totalSectores = sectores.size();
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        
        writer.println("• Total de sectores activos: " + totalSectores);
        writer.println("• Total de clientes registrados: " + totalClientes);
        writer.println("• Promedio de clientes por sector: " + (totalSectores > 0 ? (totalClientes / totalSectores) : 0));
        writer.println("• Planes con ofertas activas: " + planesConOferta.size());
        
        // Identificar sector más y menos poblado
        if (!sectores.isEmpty()) {
            SectorMetrics metrics = analizarSectores(sectores);
            writer.println("• Sector con mayor penetración: " + metrics.sectorMayor.getNombre() + 
                          " (" + metrics.sectorMayor.contarClientes() + " clientes)");
            writer.println("• Sector con menor penetración: " + metrics.sectorMenor.getNombre() + 
                          " (" + metrics.sectorMenor.contarClientes() + " clientes)");
        }
        
        writer.println();
    }
    
    /**
     * Genera análisis detallado por sector
     */
    public void generarAnalisisDetallado(PrintWriter writer) {
        writer.println("🏘️ ANÁLISIS DETALLADO POR SECTOR");
        writer.println("─────────────────────────────────────────────────────────────");
        
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        sectores.sort((a, b) -> Integer.compare(b.contarClientes(), a.contarClientes())); // Ordenar por clientes desc
        
        for (Sector sector : sectores) {
            writer.println();
            writer.println("🌍 SECTOR: " + sector.getNombre());
            writer.println("   ├── Clientes activos: " + sector.contarClientes());
            
            // Análisis de planes en el sector
            List<PlanSector> planesSector = planService.obtenerPlanesPorSector(sector.getNombre());
            writer.println("   ├── Planes disponibles: " + planesSector.size());
            
            if (!planesSector.isEmpty()) {
                SectorFinancialMetrics metrics = calcularMetricasFinancieras(planesSector);
                
                writer.printf("   ├── Ingresos estimados del sector: $%,d/mes%n", metrics.ingresoTotal);
                writer.println("   ├── Planes con ofertas activas: " + metrics.planesConDescuento + "/" + planesSector.size());
                
                // Mostrar detalles de cada plan
                for (PlanMetrics planMetric : metrics.detallesPlanes) {
                    writer.printf("   │   ├── %s: %d clientes, $%,d c/u → $%,d total%n", 
                        planMetric.nombrePlan, 
                        planMetric.clientesCount,
                        planMetric.precioFinal,
                        planMetric.ingresos);
                        
                    if (planMetric.tieneOferta) {
                        writer.printf("   │   │   └── 🏷️ OFERTA: %.0f%% descuento (Precio original: $%,d)%n",
                            planMetric.descuento * 100,
                            planMetric.precioOriginal);
                    }
                }
                
                // Estado del sector
                String estadoSector = determinarEstadoSector(sector.contarClientes());
                writer.println("   └── Estado: " + estadoSector);
            }
        }
        
        writer.println();
    }
    
    /**
     * Genera análisis de planes y ofertas
     */
    public void generarAnalisisPlanes(PrintWriter writer) {
        writer.println("📋 ANÁLISIS DE PLANES Y OFERTAS");
        writer.println("─────────────────────────────────────────────────────────────");
        
        List<PlanSector> todosLosPlanes = planService.obtenerTodosLosPlanes();
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        
        // Estadísticas generales de ofertas
        writer.println("📊 Estadísticas de Ofertas:");
        writer.println("   ├── Total de planes: " + todosLosPlanes.size());
        writer.println("   ├── Planes con ofertas: " + planesConOferta.size());
        writer.printf("   └── Porcentaje de penetración de ofertas: %.1f%%%n", 
            todosLosPlanes.size() > 0 ? (planesConOferta.size() * 100.0 / todosLosPlanes.size()) : 0);
        
        writer.println();
        
        // Análisis de ofertas por categoría
        if (!planesConOferta.isEmpty()) {
            Map<String, List<PlanSector>> ofertasPorCategoria = categorizarOfertas(planesConOferta);
            
            writer.println("🏷️ Ofertas Activas por Categoría:");
            for (Map.Entry<String, List<PlanSector>> entry : ofertasPorCategoria.entrySet()) {
                writer.println("   ├── " + entry.getKey() + ": " + entry.getValue().size() + " planes");
                for (PlanSector plan : entry.getValue()) {
                    List<Cliente> clientes = clienteService.obtenerClientesPorPlan(plan.getCodigoPlan());
                    long ahorroTotal = (plan.getPrecioMensual() - plan.calcularPrecioFinal()) * clientes.size();
                    writer.printf("   │   └── %s: %.0f%% desc., %d clientes, $%,d ahorro total/mes%n",
                        plan.getCodigoPlan(),
                        plan.getDescuento() * 100,
                        clientes.size(),
                        ahorroTotal);
                }
            }
        }
        
        writer.println();
    }
    
    /**
     * Genera recomendaciones estratégicas basadas en el análisis
     */
    public void generarRecomendaciones(PrintWriter writer) {
        writer.println("💡 RECOMENDACIONES ESTRATÉGICAS");
        writer.println("─────────────────────────────────────────────────────────────");
        
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        SectorClassification classification = clasificarSectores(sectores);
        
        writer.println("📈 Recomendaciones de Crecimiento:");
        if (!classification.sectoresCriticos.isEmpty()) {
            writer.println("   ├── ALTA PRIORIDAD - Sectores críticos (" + classification.sectoresCriticos.size() + "):");
            for (Sector sector : classification.sectoresCriticos) {
                writer.println("   │   └── " + sector.getNombre() + " (" + sector.contarClientes() + " clientes)");
                writer.println("   │       → Implementar campaña de captación intensiva");
                writer.println("   │       → Considerar descuentos agresivos (20-30%)");
                writer.println("   │       → Evaluar alianzas locales o promociones dirigidas");
            }
        }
        
        writer.println("   ├── OPTIMIZACIÓN - Sectores exitosos:");
        if (!classification.sectoresExcelentes.isEmpty()) {
            for (Sector sector : classification.sectoresExcelentes) {
                writer.println("   │   └── " + sector.getNombre() + " (" + sector.contarClientes() + " clientes)");
                writer.println("   │       → Mantener calidad de servicio");
                writer.println("   │       → Considerar planes premium");
                writer.println("   │       → Usar como modelo para otros sectores");
            }
        } else {
            writer.println("   │   └── Ningún sector ha alcanzado el nivel de excelencia (100+ clientes)");
        }
        
        // Recomendaciones de ofertas
        generateOfferRecommendations(writer);
        
        writer.println();
        writer.println("🎯 Métricas Clave a Monitorear:");
        writer.println("   ├── Crecimiento mensual de clientes por sector");
        writer.println("   ├── Efectividad de ofertas y descuentos");
        writer.println("   ├── Ingresos promedio por cliente (ARPU)");
        writer.println("   ├── Tasa de retención de clientes");
        writer.println("   └── Penetración de mercado por zona geográfica");
        
        writer.println();
    }
    
    // --- MÉTODOS DE APOYO PARA ANÁLISIS ---
    
    private SectorMetrics analizarSectores(List<Sector> sectores) {
        Sector sectorMayor = sectores.get(0);
        Sector sectorMenor = sectores.get(0);
        
        for (Sector sector : sectores) {
            int clientesSector = sector.contarClientes();
            if (clientesSector > sectorMayor.contarClientes()) {
                sectorMayor = sector;
            }
            if (clientesSector < sectorMenor.contarClientes()) {
                sectorMenor = sector;
            }
        }
        
        return new SectorMetrics(sectorMayor, sectorMenor);
    }
    
    private SectorFinancialMetrics calcularMetricasFinancieras(List<PlanSector> planes) {
        long ingresoTotal = 0;
        int planesConDescuento = 0;
        List<PlanMetrics> detallesPlanes = new ArrayList<>();
        
        for (PlanSector plan : planes) {
            List<Cliente> clientesConPlan = clienteService.obtenerClientesPorPlan(plan.getCodigoPlan());
            
            // Solo contar clientes con suscripciones ACTIVAS
            int clientesActivos = 0;
            for (Cliente cliente : clientesConPlan) {
                if (cliente.getSuscripcion() != null && 
                    "ACTIVA".equalsIgnoreCase(cliente.getSuscripcion().getEstado())) {
                    clientesActivos++;
                }
            }
            
            long ingresos = plan.calcularPrecioFinal() * clientesActivos;
            ingresoTotal += ingresos;
            
            if (plan.getOfertaActiva()) {
                planesConDescuento++;
            }
            
            detallesPlanes.add(new PlanMetrics(
                plan.getNombrePlan(),
                clientesActivos, // Usar clientesActivos en lugar de clientesConPlan.size()
                plan.calcularPrecioFinal(),
                plan.getPrecioMensual(),
                ingresos,
                plan.getOfertaActiva(),
                plan.getDescuento()
            ));
        }
        
        return new SectorFinancialMetrics(ingresoTotal, planesConDescuento, detallesPlanes);
    }
    
    private String determinarEstadoSector(int clientesSector) {
        if (clientesSector >= 100) {
            return "🟢 EXCELENTE - Sector consolidado";
        } else if (clientesSector >= 50) {
            return "🟡 BUENO - Crecimiento estable";
        } else if (clientesSector >= 25) {
            return "🟠 MODERADO - Requiere atención";
        } else {
            return "🔴 CRÍTICO - Necesita intervención urgente";
        }
    }
    
    private Map<String, List<PlanSector>> categorizarOfertas(List<PlanSector> planesConOferta) {
        Map<String, List<PlanSector>> categorias = new HashMap<>();
        
        for (PlanSector plan : planesConOferta) {
            double descuento = plan.getDescuento();
            String categoria;
            if (descuento >= 0.25) {
                categoria = "Alto descuento (25%+)";
            } else if (descuento >= 0.15) {
                categoria = "Descuento moderado (15-24%)";
            } else {
                categoria = "Descuento básico (1-14%)";
            }
            
            categorias.computeIfAbsent(categoria, k -> new ArrayList<>()).add(plan);
        }
        
        return categorias;
    }
    
    private SectorClassification clasificarSectores(List<Sector> sectores) {
        List<Sector> sectoresCriticos = new ArrayList<>();
        List<Sector> sectoresExcelentes = new ArrayList<>();
        
        for (Sector sector : sectores) {
            int clientes = sector.contarClientes();
            if (clientes < 25) {
                sectoresCriticos.add(sector);
            } else if (clientes >= 100) {
                sectoresExcelentes.add(sector);
            }
        }
        
        return new SectorClassification(sectoresCriticos, sectoresExcelentes);
    }
    
    private void generateOfferRecommendations(PrintWriter writer) {
        List<PlanSector> planesConOferta = planService.obtenerPlanesConOferta();
        List<PlanSector> todosLosPlanes = planService.obtenerTodosLosPlanes();
        
        writer.println("   └── OFERTAS Y PROMOCIONES:");
        double porcentajeOfertas = todosLosPlanes.size() > 0 ? 
            (planesConOferta.size() * 100.0 / todosLosPlanes.size()) : 0;
        
        if (porcentajeOfertas < 30) {
            writer.println("       → Aumentar penetración de ofertas (actual: " + 
                          String.format("%.1f", porcentajeOfertas) + "%)");
            writer.println("       → Implementar ofertas estacionales o por sectores específicos");
        } else if (porcentajeOfertas > 70) {
            writer.println("       → Evaluar sostenibilidad de ofertas (actual: " + 
                          String.format("%.1f", porcentajeOfertas) + "%)");
            writer.println("       → Considerar segmentación más específica de descuentos");
        } else {
            writer.println("       → Nivel de ofertas adecuado (actual: " + 
                          String.format("%.1f", porcentajeOfertas) + "%)");
            writer.println("       → Mantener estrategia actual y monitorear resultados");
        }
    }
    
    // --- CLASES DE DATOS PARA MÉTRICAS ---
    
    private static class SectorMetrics {
        final Sector sectorMayor;
        final Sector sectorMenor;
        
        SectorMetrics(Sector sectorMayor, Sector sectorMenor) {
            this.sectorMayor = sectorMayor;
            this.sectorMenor = sectorMenor;
        }
    }
    
    private static class SectorFinancialMetrics {
        final long ingresoTotal;
        final int planesConDescuento;
        final List<PlanMetrics> detallesPlanes;
        
        SectorFinancialMetrics(long ingresoTotal, int planesConDescuento, List<PlanMetrics> detallesPlanes) {
            this.ingresoTotal = ingresoTotal;
            this.planesConDescuento = planesConDescuento;
            this.detallesPlanes = detallesPlanes;
        }
    }
    
    private static class PlanMetrics {
        final String nombrePlan;
        final int clientesCount;
        final long precioFinal;
        final long precioOriginal;
        final long ingresos;
        final boolean tieneOferta;
        final double descuento;
        
        PlanMetrics(String nombrePlan, int clientesCount, long precioFinal, 
                   long precioOriginal, long ingresos, boolean tieneOferta, double descuento) {
            this.nombrePlan = nombrePlan;
            this.clientesCount = clientesCount;
            this.precioFinal = precioFinal;
            this.precioOriginal = precioOriginal;
            this.ingresos = ingresos;
            this.tieneOferta = tieneOferta;
            this.descuento = descuento;
        }
    }
    
    private static class SectorClassification {
        final List<Sector> sectoresCriticos;
        final List<Sector> sectoresExcelentes;
        
        SectorClassification(List<Sector> sectoresCriticos, List<Sector> sectoresExcelentes) {
            this.sectoresCriticos = sectoresCriticos;
            this.sectoresExcelentes = sectoresExcelentes;
        }
    }
}
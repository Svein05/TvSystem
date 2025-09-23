package tvsystem.service;

import tvsystem.model.PlanSector;
import tvsystem.repository.PlanRepository;
import java.util.*;

/**
 * Servicio para gestionar la lógica de negocio relacionada con planes.
 * 
 * @author Elias Manriquez
 */
public class PlanService {
    private PlanRepository planRepository;
    
    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }
    
    public List<PlanSector> obtenerTodosLosPlanes() {
        return planRepository.findAll();
    }
    
    public List<PlanSector> obtenerPlanesPorSector(String nombreSector) {
        return planRepository.findBySector(nombreSector);
    }
    
    public PlanSector obtenerPlanPorCodigo(String codigo) {
        return planRepository.findByCodigo(codigo);
    }
    
    public Map<String, List<PlanSector>> obtenerPlanesAgrupadosPorSector() {
        return planRepository.findAllGroupedBySector();
    }
    
    public List<PlanSector> obtenerPlanesConOferta() {
        return planRepository.findByOfertaActiva();
    }
    
    public boolean existePlan(String codigo) {
        return planRepository.exists(codigo);
    }
    
    public void activarOfertaPorSector(String nombreSector, double descuento) {
        if (descuento < 0 || descuento > 1) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 1");
        }
        planRepository.activarOfertaPorSector(nombreSector, descuento);
    }
    
    public void desactivarOfertaPorSector(String nombreSector) {
        planRepository.desactivarOfertaPorSector(nombreSector);
    }
    
    public Map<String, Long> obtenerEstadisticasPrecios() {
        List<PlanSector> planes = planRepository.findAll();
        if (planes.isEmpty()) {
            return new HashMap<>();
        }
        
        OptionalDouble promedio = planes.stream()
            .mapToLong(PlanSector::getPrecioMensual)
            .average();
        
        long minimo = planes.stream()
            .mapToLong(PlanSector::getPrecioMensual)
            .min()
            .orElse(0);
        
        long maximo = planes.stream()
            .mapToLong(PlanSector::getPrecioMensual)
            .max()
            .orElse(0);
        
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("PROMEDIO", (long) promedio.orElse(0));
        estadisticas.put("MINIMO", minimo);
        estadisticas.put("MAXIMO", maximo);
        
        return estadisticas;
    }
    
    public List<PlanSector> obtenerPlanesPorRangoPrecio(long precioMinimo, long precioMaximo) {
        List<PlanSector> planesFiltrados = new ArrayList<>();
        for (PlanSector plan : planRepository.findAll()) {
            long precio = plan.calcularPrecioFinal();
            if (precio >= precioMinimo && precio <= precioMaximo) {
                planesFiltrados.add(plan);
            }
        }
        return planesFiltrados;
    }
    
    public Map<String, Integer> contarPlanesPorSector() {
        Map<String, Integer> conteo = new HashMap<>();
        Map<String, List<PlanSector>> planesPorSector = planRepository.findAllGroupedBySector();
        
        for (Map.Entry<String, List<PlanSector>> entry : planesPorSector.entrySet()) {
            conteo.put(entry.getKey(), entry.getValue().size());
        }
        
        return conteo;
    }
}
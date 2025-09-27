package tvsystem.service;

import tvsystem.model.PlanSector;
import tvsystem.repository.PlanRepository;
import java.util.*;

/**
 * Servicio para gestionar la logica de negocio relacionada con planes.
 * 
 * @author Elias Manriquez
 * @author Maximiliano Rodriguez
 */
public class PlanService {
    private PlanRepository planRepository;
    
    // Constructor
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
    
    public List<PlanSector> obtenerPlanesConOferta() {
        return planRepository.findByOfertaActiva();
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
}
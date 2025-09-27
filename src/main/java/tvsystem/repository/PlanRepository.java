package tvsystem.repository;

import tvsystem.model.PlanSector;
import tvsystem.model.Sector;
import java.util.*;

/**
 * Repositorio para gestionar el acceso a datos de planes.
 * 
 * @author Elias Manriquez
 */
public class PlanRepository {
    private SectorRepository sectorRepository;
    
    // Constructor
    public PlanRepository(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }
    
    // -- METODOS --
    
    public boolean save(PlanSector plan, String nombreSector) {
        Sector sector = sectorRepository.findByNombre(nombreSector);
        if (sector != null) {
            sector.addPlan(plan.getCodigoPlan(), plan);
            return true;
        }
        return false;
    }
    
    public PlanSector findByCodigo(String codigo) {
        for (Sector sector : sectorRepository.findAll()) {
            PlanSector plan = sector.getPlan(codigo);
            if (plan != null) {
                return plan;
            }
        }
        return null;
    }
    
    public List<PlanSector> findBySector(String nombreSector) {
        Sector sector = sectorRepository.findByNombre(nombreSector);
        if (sector != null) {
            return new ArrayList<>(sector.getPlanesDisponibles().values());
        }
        return new ArrayList<>();
    }
    
    public List<PlanSector> findAll() {
        List<PlanSector> todosLosPlanes = new ArrayList<>();
        for (Sector sector : sectorRepository.findAll()) {
            todosLosPlanes.addAll(sector.getPlanesDisponibles().values());
        }
        return todosLosPlanes;
    }
    
    public List<PlanSector> findByOfertaActiva() {
        List<PlanSector> planesConOferta = new ArrayList<>();
        for (PlanSector plan : findAll()) {
            if (plan.getOfertaActiva()) {
                planesConOferta.add(plan);
            }
        }
        return planesConOferta;
    }
    
    public Map<String, List<PlanSector>> findAllGroupedBySector() {
        Map<String, List<PlanSector>> planesPorSector = new HashMap<>();
        for (Sector sector : sectorRepository.findAll()) {
            planesPorSector.put(sector.getNombre(), 
                new ArrayList<>(sector.getPlanesDisponibles().values()));
        }
        return planesPorSector;
    }
    
    public boolean delete(String codigo) {
        for (Sector sector : sectorRepository.findAll()) {
            if (sector.getPlan(codigo) != null) {
                sector.removePlan(codigo);
                return true;
            }
        }
        return false;
    }
    
    public boolean exists(String codigo) {
        return findByCodigo(codigo) != null;
    }
    
    public void activarOfertaPorSector(String nombreSector, double descuento) {
        List<PlanSector> planes = findBySector(nombreSector);
        for (PlanSector plan : planes) {
            plan.activarOferta(descuento);
        }
    }
    
    public void desactivarOfertaPorSector(String nombreSector) {
        List<PlanSector> planes = findBySector(nombreSector);
        for (PlanSector plan : planes) {
            plan.desactivarOferta();
        }
    }
}
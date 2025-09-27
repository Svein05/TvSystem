package tvsystem.service;

import tvsystem.model.Sector;
import tvsystem.repository.SectorRepository;
import tvsystem.util.DataInitializer;
import tvsystem.exception.SectorNoEncontradoException;
import java.util.*;

/**
 * Servicio para gestionar la lógica de negocio relacionada con sectores.
 * 
 * @author Elias Manriquez
 */
public class SectorService {
    private SectorRepository sectorRepository;
    
    public SectorService(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }
    
    public void inicializarSistema() {
        Map<String, Sector> sectoresIniciales = DataInitializer.inicializarSectores();
        for (Sector sector : sectoresIniciales.values()) {
            sectorRepository.save(sector);
        }
    }
    
    public List<Sector> obtenerTodosLosSectores() {
        return sectorRepository.findAll();
    }
    
    public List<String> obtenerNombresSectores() {
        return sectorRepository.findAllNombres();
    }
    
    public Sector obtenerSectorPorNombre(String nombre) throws SectorNoEncontradoException {
        Sector sector = sectorRepository.findByNombre(nombre);
        if (sector == null) {
            int sectoresDisponibles = sectorRepository.findAll().size();
            throw new SectorNoEncontradoException(
                "Sector '" + nombre + "' no encontrado en el sistema", 
                nombre, 
                sectoresDisponibles
            );
        }
        return sector;
    }
    
    /**
     * Versión que no lanza excepción para compatibilidad hacia atrás
     */
    public Sector buscarSectorPorNombre(String nombre) {
        return sectorRepository.findByNombre(nombre);
    }
    
    public boolean existeSector(String nombre) {
        return sectorRepository.exists(nombre);
    }
    
    public List<Sector> identificarSectoresDebiles(int umbralMinimo) {
        return sectorRepository.findSectoresDebiles(umbralMinimo);
    }
    
    public Map<String, Integer> obtenerEstadisticasPorSector() {
        Map<String, Integer> estadisticas = new HashMap<>();
        for (Sector sector : sectorRepository.findAll()) {
            estadisticas.put(sector.getNombre(), sector.contarClientes());
        }
        return estadisticas;
    }
    
    public List<Sector> obtenerSectoresConClientes() {
        List<Sector> sectoresConClientes = new ArrayList<>();
        for (Sector sector : sectorRepository.findAll()) {
            if (sector.contarClientes() > 0) {
                sectoresConClientes.add(sector);
            }
        }
        return sectoresConClientes;
    }
    
    public double calcularPenetracionPromedio() {
        List<Sector> sectores = sectorRepository.findAll();
        if (sectores.isEmpty()) return 0.0;
        
        int totalClientes = 0;
        for (Sector sector : sectores) {
            totalClientes += sector.contarClientes();
        }
        
        return (double) totalClientes / sectores.size();
    }
    
    public Sector obtenerSectorConMasClientes() {
        return sectorRepository.findAll().stream()
            .max(Comparator.comparingInt(Sector::contarClientes))
            .orElse(null);
    }
    
    public Sector obtenerSectorConMenosClientes() {
        return sectorRepository.findAll().stream()
            .min(Comparator.comparingInt(Sector::contarClientes))
            .orElse(null);
    }
}
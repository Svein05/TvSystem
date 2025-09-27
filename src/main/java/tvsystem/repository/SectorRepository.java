package tvsystem.repository;

import tvsystem.model.Sector;
import java.util.*;

/**
 * Repositorio para gestionar el acceso a datos de sectores.
 * 
 * @author Elias Manriquez
 */
public class SectorRepository {
    private Map<String, Sector> sectores;
    
    // Constructor
    public SectorRepository() {
        this.sectores = new HashMap<>();
    }
    
    // -- METODOS --

    public void save(Sector sector) {
        sectores.put(sector.getNombre(), sector);
    }
    
    public Sector findByNombre(String nombre) {
        return sectores.get(nombre.toUpperCase());
    }
    
    public List<Sector> findAll() {
        return new ArrayList<>(sectores.values());
    }
    
    public List<String> findAllNombres() {
        return new ArrayList<>(sectores.keySet());
    }
    
    public boolean exists(String nombre) {
        return sectores.containsKey(nombre.toUpperCase());
    }
    
    public void delete(String nombre) {
        sectores.remove(nombre.toUpperCase());
    }
    
    public void deleteAll() {
        sectores.clear();
    }
    
    public int count() {
        return sectores.size();
    }
    
    public List<Sector> findSectoresDebiles(int umbralMinimo) {
        List<Sector> sectoresDebiles = new ArrayList<>();
        for (Sector sector : sectores.values()) {
            if (sector.esSectorDebil(umbralMinimo)) {
                sectoresDebiles.add(sector);
            }
        }
        return sectoresDebiles;
    }
    
    public Map<String, Sector> getSectoresMap() {
        return new HashMap<>(sectores);
    }
}
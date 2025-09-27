package tvsystem.service;

import tvsystem.model.Sector;
import tvsystem.repository.SectorRepository;
import tvsystem.util.DataInitializer;
import tvsystem.exception.SectorNoEncontradoException;
import java.util.*;

/**
 * Servicio para gestionar la logica de negocio relacionada con sectores.
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
    
    public List<Sector> identificarSectoresDebiles(int umbralMinimo) {
        return sectorRepository.findSectoresDebiles(umbralMinimo);
    }
}
package tvsystem.util;

import tvsystem.model.*;
import java.util.*;

/**
 * Utilidad para inicializar datos del sistema.
 * Crea sectores con sus planes específicos.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class DataInitializer {
    
    public static Map<String, Sector> inicializarSectores() {
        Map<String, Sector> sectores = new HashMap<>();
        
        String[] nombresSectores = {
            "VALPARAISO", "VINA_DEL_MAR", "QUILPUE", "VILLA_ALEMANA", 
            "LIMACHE", "OLMUE", "SAN_ANTONIO", "CARTAGENA", "EL_QUISCO", 
            "ALGARROBO", "SAN_FELIPE", "LOS_ANDES", "QUINTERO", "CONCON"
        };
        
        for (String nombreSector : nombresSectores) {
            Sector sector = new Sector(nombreSector);
            inicializarPlanesParaSector(sector);
            sectores.put(nombreSector, sector);
        }
        
        return sectores;
    }
    
    private static void inicializarPlanesParaSector(Sector sector) {
        String nombreSector = sector.getNombre();
        
        // Crear planes especificos para cada sector con variaciones de precio
        Map<String, Object[]> baseData = getBasePlanData();
        
        for (Map.Entry<String, Object[]> entry : baseData.entrySet()) {
            String codigo = entry.getKey();
            Object[] datos = entry.getValue();
            
            String nombre = (String) datos[0];
            int precioBase = (Integer) datos[1];
            double descuentoBase = (Double) datos[2];
            
            // Crear código unico para el sector
            String codigoSector = codigo + "_" + nombreSector;
            
            PlanSector plan = new PlanSector(
                codigoSector, 
                nombre + " (" + nombreSector + ")", 
                precioBase, 
                false, // ofertaActiva = false por defecto
                descuentoBase, 
                nombreSector
            );
            
            sector.addPlan(codigoSector, plan);
        }
    }
    
    private static Map<String, Object[]> getBasePlanData() {
        Map<String, Object[]> planes = new HashMap<>();
        // Formato: {nombre, precio, descuento}
        
        planes.put("BASICO", new Object[]{"Plan Básico", 15000, 0.0});
        planes.put("PREMIUM", new Object[]{"Plan Premium", 25000, 0.0});
        planes.put("FAMILIAR", new Object[]{"Plan Familiar", 35000, 0.0});
        return planes;
    }
}
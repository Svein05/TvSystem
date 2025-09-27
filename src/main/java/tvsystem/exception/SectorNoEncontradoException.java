package tvsystem.exception;

/**
 * Excepcion para errores cuando no se encuentra un sector especifico.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class SectorNoEncontradoException extends Exception {
    
    private String nombreSector;
    private int sectoresDisponibles;
    
    // Constructor
    public SectorNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    // Constructor Sobrecarga
    public SectorNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    // Constructor Sobrecarga
    public SectorNoEncontradoException(String mensaje, String nombreSector, int sectoresDisponibles) {
        super(mensaje);
        this.nombreSector = nombreSector;
        this.sectoresDisponibles = sectoresDisponibles;
    }
    
    // Constructor Sobrecarga
    public SectorNoEncontradoException(String mensaje, Throwable causa, String nombreSector, int sectoresDisponibles) {
        super(mensaje, causa);
        this.nombreSector = nombreSector;
        this.sectoresDisponibles = sectoresDisponibles;
    }
    
    // Getters
    public String getNombreSector() {
        return nombreSector;
    }
    
    public int getSectoresDisponibles() {
        return sectoresDisponibles;
    }
    
    @Override
    public String toString() {
        return "SectorNoEncontradoException{" +
                "mensaje='" + getMessage() + '\'' +
                ", nombreSector='" + nombreSector + '\'' +
                ", sectoresDisponibles=" + sectoresDisponibles +
                '}';
    }
}
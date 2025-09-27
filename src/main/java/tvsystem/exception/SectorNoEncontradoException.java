package tvsystem.exception;

/**
 * Excepción personalizada para errores cuando no se encuentra un sector específico.
 * Se lanza cuando se intenta acceder a un sector que no existe en el sistema.
 * 
 * @author Elias Manriquez
 */
public class SectorNoEncontradoException extends Exception {
    
    private String nombreSector;
    private int sectoresDisponibles;
    
    /**
     * Constructor básico con mensaje
     */
    public SectorNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa
     */
    public SectorNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    /**
     * Constructor completo con información adicional
     */
    public SectorNoEncontradoException(String mensaje, String nombreSector, int sectoresDisponibles) {
        super(mensaje);
        this.nombreSector = nombreSector;
        this.sectoresDisponibles = sectoresDisponibles;
    }
    
    /**
     * Constructor con mensaje, causa e información adicional
     */
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
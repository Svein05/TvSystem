package tvsystem.exception;

/**
 * Excepción personalizada para errores relacionados con operaciones de suscripción.
 * Se lanza cuando hay problemas con la creación, modificación o validación de suscripciones.
 * 
 * @author Elias Manriquez
 */
public class SuscripcionInvalidaException extends Exception {
    
    private String tipoError;
    private String rutCliente;
    private String codigoPlan;
    
    /**
     * Constructor básico con mensaje
     */
    public SuscripcionInvalidaException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa
     */
    public SuscripcionInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    /**
     * Constructor completo con información adicional
     */
    public SuscripcionInvalidaException(String mensaje, String tipoError, String rutCliente, String codigoPlan) {
        super(mensaje);
        this.tipoError = tipoError;
        this.rutCliente = rutCliente;
        this.codigoPlan = codigoPlan;
    }
    
    /**
     * Constructor con mensaje, causa e información adicional
     */
    public SuscripcionInvalidaException(String mensaje, Throwable causa, String tipoError, String rutCliente, String codigoPlan) {
        super(mensaje, causa);
        this.tipoError = tipoError;
        this.rutCliente = rutCliente;
        this.codigoPlan = codigoPlan;
    }
    
    // Getters
    public String getTipoError() {
        return tipoError;
    }
    
    public String getRutCliente() {
        return rutCliente;
    }
    
    public String getCodigoPlan() {
        return codigoPlan;
    }
    
    @Override
    public String toString() {
        return "SuscripcionInvalidaException{" +
                "mensaje='" + getMessage() + '\'' +
                ", tipoError='" + tipoError + '\'' +
                ", rutCliente='" + rutCliente + '\'' +
                ", codigoPlan='" + codigoPlan + '\'' +
                '}';
    }
}
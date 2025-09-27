package tvsystem.exception;

/**
 * Excepción personalizada para errores relacionados con datos inválidos de clientes.
 * Se lanza cuando un cliente no cumple con los requisitos de validación.
 * 
 * @author Elias Manriquez
 */
public class ClienteInvalidoException extends Exception {
    
    private String codigoError;
    private String rutCliente;
    
    /**
     * Constructor básico con mensaje
     */
    public ClienteInvalidoException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa
     */
    public ClienteInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    /**
     * Constructor completo con información adicional
     */
    public ClienteInvalidoException(String mensaje, String codigoError, String rutCliente) {
        super(mensaje);
        this.codigoError = codigoError;
        this.rutCliente = rutCliente;
    }
    
    /**
     * Constructor con mensaje, causa e información adicional
     */
    public ClienteInvalidoException(String mensaje, Throwable causa, String codigoError, String rutCliente) {
        super(mensaje, causa);
        this.codigoError = codigoError;
        this.rutCliente = rutCliente;
    }
    
    // Getters
    public String getCodigoError() {
        return codigoError;
    }
    
    public String getRutCliente() {
        return rutCliente;
    }
    
    @Override
    public String toString() {
        return "ClienteInvalidoException{" +
                "mensaje='" + getMessage() + '\'' +
                ", codigoError='" + codigoError + '\'' +
                ", rutCliente='" + rutCliente + '\'' +
                '}';
    }
}
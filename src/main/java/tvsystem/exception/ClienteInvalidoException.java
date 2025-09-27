package tvsystem.exception;

/**
 * Excepcion para errores relacionados con datos invalidos de clientes.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class ClienteInvalidoException extends Exception {
    
    private String codigoError;
    private String rutCliente;
    
    // Constructor
    public ClienteInvalidoException(String mensaje) {
        super(mensaje);
    }
    
    // Constructor Sobrecarga
    public ClienteInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    // Constructor Sobrecarga
    public ClienteInvalidoException(String mensaje, String codigoError, String rutCliente) {
        super(mensaje);
        this.codigoError = codigoError;
        this.rutCliente = rutCliente;
    }
    
    // Constructor Sobrecarga
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
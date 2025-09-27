package tvsystem.model;

/**
 * Clase para cada Cliente individual
 * 
 * @author Elias Manriquez
 * @author Maximiliano Rodriguez
 */
public class Cliente {
    private String nombre;
    private String rut;
    private String domicilio;
    private Suscripcion suscripcion;
    
    // Constructor
    public Cliente(){
        this.nombre = "";
        this.rut = "";
        this.domicilio = "";
        this.suscripcion = null;
    }
    
    // Constructor Sobrecarga
    public Cliente(String nombre, String rut, String domicilio){
        this.nombre = nombre;
        this.rut = rut;
        this.domicilio = domicilio;
        suscripcion = null;
    }
    
    // -- SETTERS --

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setRut(String rut){
        this.rut = rut;
    }
    
    public void setDomicilio(String domicilio){
        this.domicilio = domicilio;
    }
    
    public void setSuscripcion(Suscripcion suscripcion){
        this.suscripcion = suscripcion;
    }

    // -- GETTERS --
    
    public String getNombre(){
        return nombre;
    }
    
    public String getRut(){
        return rut;
    }
    
    public String getDomicilio(){
        return domicilio;
    }
    
    public Suscripcion getSuscripcion(){
        return suscripcion;
    }
}
package tvsystem.model;

/**
 * @author Elias Manriquez
 * @author Maximiliano Rodriguez
 */
public class Cliente {
    private String nombre;
    private String rut;
    private String domicilio;
    private Suscripcion suscripcion;
    
    public Cliente(){
        this.nombre = "";
        this.rut = "";
        this.domicilio = "";
        this.suscripcion = null;
    }
    
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

    // -- METODOS --
    
    public String mostrarInfo(){
        return "Cliente: " + nombre + "\n" + "Rut: " + rut; 
    }
    
    public String mostrarInfo(boolean incluirDomicilio){
        if(incluirDomicilio){
            return "Cliente: " + nombre + "\n" + "Rut: " + rut + "\n" + "Domicilio: " + domicilio;
        }
        return mostrarInfo();
    }
    
    public String mostrarInfo(boolean incluirDomicilio, boolean incluirSuscripcion){
        String info = mostrarInfo(incluirDomicilio);
        if(incluirSuscripcion && suscripcion != null){
            info += "\n" + "Suscripción: " + this.suscripcion.getEstado();
        }
        return info;
    }
    
    // -- SOBREESCRITURA DE MÉTODOS --
    
    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", rut='" + rut + '\'' +
                ", domicilio='" + domicilio + '\'' +
                ", suscripcion=" + (suscripcion != null ? suscripcion.getEstado() : "Sin suscripción") +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cliente cliente = (Cliente) obj;
        return rut != null ? rut.equals(cliente.rut) : cliente.rut == null;
    }
    
    @Override
    public int hashCode() {
        return rut != null ? rut.hashCode() : 0;
    }
}
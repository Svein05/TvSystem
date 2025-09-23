package tvsystem.model;

import java.util.*;

/**
 * Representa un sector.
 * Cada sector tiene sus propios planes específicos y clientes.
 * 
 * @author Elias Manriquez
 * @author Maximiliano Rodriguez
 */
public class Sector {
    private String nombre;
    private Map<String, Cliente> clientes;
    private Map<String, PlanSector> planesDisponibles;
    
    public Sector(){
        nombre = "";
        this.clientes = new HashMap<>();
        this.planesDisponibles = new HashMap<>();
    }
    
    public Sector(String nombre){
        this.nombre = nombre;
        this.clientes = new HashMap<>();
        this.planesDisponibles = new HashMap<>();
    }

    // -- SETTERS Y GETTERS --

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setClientes(Map<String, Cliente> clientes){
        this.clientes = new HashMap<>(clientes);
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public List<Cliente> getClientes(){
        return new ArrayList<>(clientes.values());
    }
    
    public Map<String, Cliente> getClientesMap() {
        return new HashMap<>(clientes);
    }
    
    public Map<String, PlanSector> getPlanesDisponibles() {
        return new HashMap<>(planesDisponibles);
    }

    // -- METODOS --
    
    // Gestion de clientes
    public void addCliente(Cliente cliente){
        this.clientes.put(cliente.getRut(), cliente);
    }
    
    public void removeCliente(String rut) {
        this.clientes.remove(rut);
    }
    
    public Cliente getCliente(String rut) {
        return this.clientes.get(rut);
    }
    
    // Gestion de planes
    public void addPlan(String codigo, PlanSector plan) {
        this.planesDisponibles.put(codigo, plan);
    }
    
    public void removePlan(String codigo) {
        this.planesDisponibles.remove(codigo);
    }
    
    public PlanSector getPlan(String codigo) {
        return this.planesDisponibles.get(codigo);
    }
    
    // Métodos de análisis
    public int contarClientes() {
        return clientes.size();
    }
    
    public int contarClientes(String estado) {
        int contador = 0;
        for (Cliente cliente : clientes.values()) {
            if (cliente.getSuscripcion() != null && 
                cliente.getSuscripcion().getEstado().equalsIgnoreCase(estado)) {
                contador++;
            }
        }
        return contador;
    }
    
    public boolean esSectorDebil() {
        return contarClientes() < 5;
    }
    
    public boolean esSectorDebil(int umbralMinimo) {
        return contarClientes() < umbralMinimo;
    }
    
    public double calcularPenetracionMercado(int poblacionTotal) {
        return (double) contarClientes() / poblacionTotal * 100;
    }
    
    public List<Cliente> getClientesPorPlan(String codigoPlan) {
        List<Cliente> clientesDelPlan = new ArrayList<>();
        for (Cliente cliente : clientes.values()) {
            if (cliente.getSuscripcion() != null && 
                cliente.getSuscripcion().getPlan().getCodigoPlan().equals(codigoPlan)) {
                clientesDelPlan.add(cliente);
            }
        }
        return clientesDelPlan;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

/**
 *
 * @author Maxi
 */

import java.util.*;

public class SectorDeCobertura {
    private String nombre;
    private Map<String, Cliente> clientes;
    
    // Constructores
    public SectorDeCobertura(){
        nombre = "";
        this.clientes = new HashMap<>();
    }
    
    public SectorDeCobertura(String nombre){
        this.nombre = nombre;
        this.clientes = new HashMap<>();
    }
    
    // Setters y Getters
    public void addClient(Cliente cliente){
        this.clientes.put(cliente.getRut(), cliente);
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setClientes(Map<String, Cliente> clientes){
        this.clientes = clientes;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public List<Cliente> getClientes(){
        return new ArrayList<>(clientes.values());
    }
    
    public Map<String, Cliente> getClientesMap() {
        return clientes;
    }
    
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
        return contarClientes() < 5; // Sector débil si tiene menos de 5 clientes
    }
    
    public boolean esSectorDebil(int umbralMinimo) {
        return contarClientes() < umbralMinimo;
    }
}
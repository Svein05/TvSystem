/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

/**
 *
 * @author Maxi
 */

import java.util.ArrayList;
import java.util.List;

public class SectorDeCobertura {
    private String nombre;
    private List<Cliente> clientes;
    
    public SectorDeCobertura(){
        nombre = "";
        this.clientes = new ArrayList<>();
    }
    
    public SectorDeCobertura(String nombre){
        this.nombre = nombre;
        this.clientes = new ArrayList<>();
    }
    
    public void addClient(Cliente cliente){
        this.clientes.add(cliente);
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setClientes(List<Cliente> clientes){
        this.clientes = clientes;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public List<Cliente> getClientes(){
        return clientes;
    }
}

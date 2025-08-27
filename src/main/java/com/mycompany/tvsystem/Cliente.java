/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

/**
 *
 * @author Maxi
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
}

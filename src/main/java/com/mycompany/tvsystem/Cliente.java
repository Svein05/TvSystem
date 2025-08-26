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
    private Suscripcion sub;
    
    public Cliente(){
        nombre = "";
        rut = "";
        domicilio = "";
    }
    
    public Cliente(String nombre, String rut, String domicilio){
        this.nombre = nombre;
        this.rut = rut;
        this.domicilio = domicilio;
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
    
    public String getNombre(){
        return nombre;
    }
    
    public String getRut(){
        return rut;
    }
    
    public String getDomicilio(){
        return domicilio;
    }
}

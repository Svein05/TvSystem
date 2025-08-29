/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

import java.util.*;

/**
 *
 * @author elias
 */
public class GestionCobertura {
    private Map<String, SectorDeCobertura> sectoresCobertura;
    private Map<String, PlanTelevision> planesDisponibles;
    
    // Constructor
    public GestionCobertura() {
        this.sectoresCobertura = new HashMap<>();
        this.planesDisponibles = new HashMap<>();
    }

    // Setters y Getters
    public Map<String, SectorDeCobertura> getSectoresCobertura() {
        return sectoresCobertura;
    }

    public void setSectoresCobertura(Map<String, SectorDeCobertura> sectoresCobertura) {
        this.sectoresCobertura = sectoresCobertura;
    }

    public Map<String, PlanTelevision> getPlanesDisponibles() {
        return planesDisponibles;
    }

    public void setPlanesDisponibles(Map<String, PlanTelevision> planesDisponibles) {
        this.planesDisponibles = planesDisponibles;
    }
    
    // Metodos
    public boolean agregarCliente(String nombreSector, Cliente cliente) {
        SectorDeCobertura sector = sectoresCobertura.get(nombreSector.toUpperCase());
        
        if (sector != null) {
            sector.addClient(cliente);
            return true;
        }
        return false;
    }
    
    public boolean agregarCliente(String nombreSector, String nombre, String rut, String domicilio, String codigoPlan) {
        PlanTelevision plan = planesDisponibles.get(codigoPlan.toUpperCase());
        if (plan == null) return false;
        
        Cliente nuevoCliente = new Cliente(nombre, rut, domicilio);
        
        Date fechaInicio = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 12);
        Date fechaTermino = cal.getTime();
        
        Suscripcion nuevaSuscripcion = new Suscripcion(fechaInicio, fechaTermino,"ACTIVA" , nuevoCliente, plan);
        nuevoCliente.setSuscripcion(nuevaSuscripcion);
        
        return agregarCliente(nombreSector, nuevoCliente);
    }
    
    public List<String> obtenerNombresSectores() {
        return new ArrayList<>(sectoresCobertura.keySet());
    }
    
    public List<SectorDeCobertura> obtenerSectores() {
        return new ArrayList<>(sectoresCobertura.values());
    }
    
    public List<SectorDeCobertura> identificarSectoresDebiles(int umbralMinimo) {
        List<SectorDeCobertura> sectoresDebiles = new ArrayList<>();
        for (SectorDeCobertura sector : sectoresCobertura.values()) {
            if (sector.esSectorDebil(umbralMinimo)) {
                sectoresDebiles.add(sector);
            }
        }
        return sectoresDebiles;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elias
 */
public class PlanTelevision {
    private String nombrePlan;
    private long precioMensual;
    private List<String> canalesIncluidos;
    private boolean ofertaActiva;
    private double descuento;

    public PlanTelevision(String nombrePlan, long precioMensual, ArrayList canalesIncluidos, boolean ofertaActiva, double descuento) {
        this.nombrePlan = nombrePlan;
        this.precioMensual = precioMensual;
        this.canalesIncluidos = canalesIncluidos;
        this.ofertaActiva = ofertaActiva;
        this.descuento = descuento;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public long getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(long precioMensual) {
        this.precioMensual = precioMensual;
    }

    public List getCanalesIncluidos() {
        return canalesIncluidos;
    }

    public void setCanalesIncluidos(ArrayList canalesIncluidos) {
        this.canalesIncluidos = canalesIncluidos;
    }

    public boolean isOfertaActiva() {
        return ofertaActiva;
    }

    public void setOfertaActiva(boolean ofertaActiva) {
        this.ofertaActiva = ofertaActiva;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
    public long calcularPrecioTotal(){
        return precioMensual;
    }
    
    public long calcularPrecioTotal(boolean aplicarDescuento){
        return (long) (precioMensual * (1 - descuento));
    }
}

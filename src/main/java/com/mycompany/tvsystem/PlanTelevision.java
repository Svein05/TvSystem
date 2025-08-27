/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

/**
 *
 * @author elias
 */
public class PlanTelevision {
    private String nombrePlan;
    private long precioMensual;
    private boolean ofertaActiva;
    private double descuento;

    public PlanTelevision(String nombrePlan, long precioMensual, boolean ofertaActiva, double descuento) {
        this.nombrePlan = nombrePlan;
        this.precioMensual = precioMensual;
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

    public boolean getOfertaActiva() {
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

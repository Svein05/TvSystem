/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tvsystem;

import java.util.Date;

/**
 *
 * @author elias
 */
public class Suscripcion {
    private Date fechaInicio;
    private Date fechaTermino;
    private String estado;
    private Cliente cliente;
    private PlanTelevision plan;

    public Suscripcion(Date fechaInicio, Date fechaTermino, String estado, Cliente cliente, PlanTelevision plan) {
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.estado = estado;
        this.cliente = cliente;
        this.plan = plan;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(Date fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public PlanTelevision getPlan() {
        return plan;
    }

    public void setPlan(PlanTelevision plan) {
        this.plan = plan;
    }
    
}
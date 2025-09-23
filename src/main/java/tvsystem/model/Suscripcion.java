package tvsystem.model;

import java.util.Date;

/**
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class Suscripcion {
    private Date fechaInicio;
    private Date fechaTermino;
    private String estado;
    private Cliente cliente;
    private PlanSector plan;

    public Suscripcion(Date fechaInicio, Date fechaTermino, String estado, Cliente cliente, PlanSector plan) {
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.estado = estado;
        this.cliente = cliente;
        this.plan = plan;
    }

    // -- SETTERS Y GETTERS --

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

    public PlanSector getPlan() {
        return plan;
    }

    public void setPlan(PlanSector plan) {
        this.plan = plan;
    }
}
package tvsystem.model;

import java.time.LocalDate;
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
    private boolean pagado;
    private LocalDate ultimaFechaPago;
    private LocalDate proximoVencimiento;

    public Suscripcion(Date fechaInicio, Date fechaTermino, String estado, Cliente cliente, PlanSector plan) {
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.estado = estado;
        this.cliente = cliente;
        this.plan = plan;
        this.pagado = false;
        this.ultimaFechaPago = null;
        
        // Calcular próximo vencimiento (1 mes desde fecha inicio)
        if (fechaInicio != null) {
            LocalDate inicio = new java.sql.Date(fechaInicio.getTime()).toLocalDate();
            this.proximoVencimiento = inicio.plusMonths(1);
        }
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
        // Si se marca como CANCELADA, limpiar fecha de vencimiento
        if ("CANCELADA".equalsIgnoreCase(estado)) {
            this.proximoVencimiento = null;
            this.pagado = false;
        }
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
    
    public boolean isPagado() {
        return pagado;
    }
    
    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
    
    public LocalDate getUltimaFechaPago() {
        return ultimaFechaPago;
    }
    
    public void setUltimaFechaPago(LocalDate ultimaFechaPago) {
        this.ultimaFechaPago = ultimaFechaPago;
    }
    
    public LocalDate getProximoVencimiento() {
        return proximoVencimiento;
    }
    
    public void setProximoVencimiento(LocalDate proximoVencimiento) {
        this.proximoVencimiento = proximoVencimiento;
    }
    
    // Métodos de negocio
    
    /**
     * Registra un pago y extiende la suscripción por 1 mes
     */
    public void registrarPago() {
        this.pagado = true;
        this.ultimaFechaPago = LocalDate.now();
        
        // Extender la suscripción por 1 mes desde la fecha actual
        this.proximoVencimiento = LocalDate.now().plusMonths(1);
        
        // Actualizar estado a ACTIVA
        this.estado = "ACTIVA";
        
        // Actualizar fechaTermino (mantenemos compatibilidad con Date)
        java.sql.Date nuevaFechaTermino = java.sql.Date.valueOf(this.proximoVencimiento);
        this.fechaTermino = nuevaFechaTermino;
    }
    
    /**
     * Determina el estado actual de la suscripción basado en fechas, pagos y estado manual
     */
    public String obtenerEstadoActual() {
        // Verificar si fue marcado manualmente (respetar estados manuales)
        if ("CANCELADA".equalsIgnoreCase(this.estado) || 
            "SUSPENDIDA".equalsIgnoreCase(this.estado)) {
            return this.estado;
        }
        
        LocalDate hoy = LocalDate.now();
        
        if (proximoVencimiento == null) {
            return "CANCELADA";
        }
        
        // Si está vencida (fecha pasada)
        if (hoy.isAfter(proximoVencimiento)) {
            // Actualizar el estado interno también
            this.estado = "SUSPENDIDA";
            return "SUSPENDIDA";
        }
        
        // Si faltan 2 semanas o menos y no está pagado el próximo mes
        LocalDate dosSemantasAntes = proximoVencimiento.minusWeeks(2);
        if (!pagado && (hoy.isEqual(dosSemantasAntes) || hoy.isAfter(dosSemantasAntes))) {
            this.estado = "PROXIMA_A_VENCER";
            return "PROXIMA_A_VENCER";
        }
        
        // Si todo está bien, está activa
        this.estado = "ACTIVA";
        return "ACTIVA";
    }
    
    // -- SOBREESCRITURA DE MÉTODOS --
    
    @Override
    public String toString() {
        return "Suscripcion{" +
                "fechaInicio=" + fechaInicio +
                ", fechaTermino=" + fechaTermino +
                ", estado='" + estado + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "Sin cliente") +
                ", plan=" + (plan != null ? plan.getNombrePlan() : "Sin plan") +
                ", pagado=" + pagado +
                ", proximoVencimiento=" + proximoVencimiento +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Suscripcion that = (Suscripcion) obj;
        
        if (cliente != null ? !cliente.getRut().equals(that.cliente != null ? that.cliente.getRut() : null) : that.cliente != null) return false;
        if (plan != null ? !plan.getCodigoPlan().equals(that.plan != null ? that.plan.getCodigoPlan() : null) : that.plan != null) return false;
        return fechaInicio != null ? fechaInicio.equals(that.fechaInicio) : that.fechaInicio == null;
    }
    
    @Override
    public int hashCode() {
        int result = fechaInicio != null ? fechaInicio.hashCode() : 0;
        result = 31 * result + (cliente != null ? cliente.getRut().hashCode() : 0);
        result = 31 * result + (plan != null ? plan.getCodigoPlan().hashCode() : 0);
        return result;
    }
}
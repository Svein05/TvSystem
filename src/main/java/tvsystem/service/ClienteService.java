package tvsystem.service;

import tvsystem.model.*;
import tvsystem.repository.*;
import tvsystem.util.RutValidator;
import tvsystem.util.CsvManager;
import tvsystem.util.LoggerHelper;
import tvsystem.exception.ClienteInvalidoException;
import tvsystem.exception.SectorNoEncontradoException;
import tvsystem.exception.SuscripcionInvalidaException;
import java.util.*;

/**
 * Servicio para gestionar la lOgica de negocio relacionada con clientes.
 * 
 * @author Elias Manriquez
 * @author Maximiliano Rodriguez
 */
public class ClienteService {
    private ClienteRepository clienteRepository;
    private SectorRepository sectorRepository;
    private PlanRepository planRepository;
    private SectorService sectorService;
    private PlanService planService;
    
    // Constructor
    public ClienteService(ClienteRepository clienteRepository, 
                         SectorRepository sectorRepository,
                         PlanRepository planRepository) {
        this.clienteRepository = clienteRepository;
        this.sectorRepository = sectorRepository;
        this.planRepository = planRepository;
    }
    
    // Configura las referencias a otros servicios
    public void configurarServicios(SectorService sectorService, PlanService planService) {
        this.sectorService = sectorService;
        this.planService = planService;
    }
    
    public boolean agregarCliente(String nombreSector, String nombre, String rut, 
                                String domicilio, String codigoPlan) throws ClienteInvalidoException, SectorNoEncontradoException {
        // Validar RUT
        if (!RutValidator.validarRut(rut)) {
            throw new ClienteInvalidoException("RUT inválido: " + rut, "RUT_INVALIDO", rut);
        }
        
        // Verificar que no exista el cliente
        if (clienteRepository.exists(rut)) {
            throw new ClienteInvalidoException("Ya existe un cliente con RUT: " + rut, "RUT_DUPLICADO", rut);
        }
        
        // Validar datos básicos
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ClienteInvalidoException("El nombre del cliente no puede estar vacío", "NOMBRE_VACIO", rut);
        }
        
        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new ClienteInvalidoException("El domicilio del cliente no puede estar vacío", "DOMICILIO_VACIO", rut);
        }
        
        // Verificar que exista el sector
        if (!sectorRepository.exists(nombreSector)) {
            int sectoresDisponibles = sectorRepository.findAll().size();
            throw new SectorNoEncontradoException("Sector no encontrado: " + nombreSector, nombreSector, sectoresDisponibles);
        }
        
        // Verificar que exista el plan
        PlanSector plan = planRepository.findByCodigo(codigoPlan);
        if (plan == null) {
            throw new ClienteInvalidoException("Plan no encontrado: " + codigoPlan, "PLAN_NO_ENCONTRADO", rut);
        }
        
        // Crear cliente
        Cliente nuevoCliente = new Cliente(nombre, RutValidator.formatearRut(rut), domicilio);
        
        // Crear suscripción con duración de 1 mes
        Date fechaInicio = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date fechaTermino = cal.getTime();
        
        Suscripcion nuevaSuscripcion = new Suscripcion(fechaInicio, fechaTermino, "ACTIVA", nuevoCliente, plan);
        nuevoCliente.setSuscripcion(nuevaSuscripcion);
        
        // Guardar cliente
        return clienteRepository.save(nuevoCliente, nombreSector);
    }
    
    // Sobrecarga
    public boolean agregarCliente(String nombreSector, String nombre, String rut, 
                                String domicilio, String codigoPlan, String estadoSuscripcion,
                                java.time.LocalDate proximoVencimiento, boolean pagado) throws ClienteInvalidoException, SectorNoEncontradoException {
        // Validar RUT
        if (!RutValidator.validarRut(rut)) {
            throw new ClienteInvalidoException("RUT inválido: " + rut, "RUT_INVALIDO", rut);
        }
        
        // Verificar que no exista el cliente
        if (clienteRepository.exists(rut)) {
            throw new ClienteInvalidoException("Ya existe un cliente con RUT: " + rut, "RUT_DUPLICADO", rut);
        }
        
        // Validar datos básicos
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ClienteInvalidoException("El nombre del cliente no puede estar vacío", "NOMBRE_VACIO", rut);
        }
        
        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new ClienteInvalidoException("El domicilio del cliente no puede estar vacío", "DOMICILIO_VACIO", rut);
        }
        
        // Verificar que exista el sector
        if (!sectorRepository.exists(nombreSector)) {
            int sectoresDisponibles = sectorRepository.findAll().size();
            throw new SectorNoEncontradoException("Sector no encontrado: " + nombreSector, nombreSector, sectoresDisponibles);
        }
        
        // Verificar que exista el plan
        PlanSector plan = planRepository.findByCodigo(codigoPlan);
        if (plan == null) {
            throw new ClienteInvalidoException("Plan no encontrado: " + codigoPlan, "PLAN_NO_ENCONTRADO", rut);
        }
        
        // Crear cliente
        Cliente nuevoCliente = new Cliente(nombre, RutValidator.formatearRut(rut), domicilio);
        
        // Crear suscripcion con parametros personalizados
        Date fechaInicio = new Date();
        Date fechaTermino = proximoVencimiento != null ? 
            java.sql.Date.valueOf(proximoVencimiento) : 
            new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)); // +30 dias por defecto
        
        Suscripcion nuevaSuscripcion = new Suscripcion(fechaInicio, fechaTermino, estadoSuscripcion, nuevoCliente, plan);
        
        // Configurar parametros adicionales
        if (proximoVencimiento != null) {
            nuevaSuscripcion.setProximoVencimiento(proximoVencimiento);
        }
        nuevaSuscripcion.setPagado(pagado);
        
        if (pagado) {
            nuevaSuscripcion.setUltimaFechaPago(java.time.LocalDate.now());
        }
        
        // Recalcular estado basado en logica de negocio
        String estadoCalculado = nuevaSuscripcion.obtenerEstadoActual();
        
        nuevoCliente.setSuscripcion(nuevaSuscripcion);
        
        // Guardar cliente
        return clienteRepository.save(nuevoCliente, nombreSector);
    }
    
    public Cliente obtenerClientePorRut(String rut) {
        return clienteRepository.findByRut(rut);
    }
    
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    public List<Cliente> obtenerClientesPorPlan(String codigoPlan) {
        return clienteRepository.findByPlan(codigoPlan);
    }
    
    public boolean eliminarCliente(String rut) {
        return clienteRepository.delete(rut);
    }
    
    public int contarClientesTotales() {
        return clienteRepository.countTotal();
    }
    
    public boolean actualizarEstadoSuscripcion(String rut, String nuevoEstado) {
        Cliente cliente = clienteRepository.findByRut(rut);
        if (cliente != null && cliente.getSuscripcion() != null) {
            cliente.getSuscripcion().setEstado(nuevoEstado);
            
            if (sectorService != null && planService != null) {
                boolean guardado = CsvManager.guardarDatos(sectorService, this, planService);
                
                if (guardado) {
                    LoggerHelper.success("Estado de suscripción actualizado y guardado para: " + cliente.getNombre());
                } else {
                    LoggerHelper.error("Error al guardar cambios de estado para: " + cliente.getNombre());
                }
                return guardado;
            } else {
                LoggerHelper.warning("Servicios no configurados, cambios no guardados en CSV");
                return false;
            }
        }
        return false;
    }
    
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        List<Cliente> clientesFiltrados = new ArrayList<>();
        for (Cliente cliente : clienteRepository.findAll()) {
            if (cliente.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                clientesFiltrados.add(cliente);
            }
        }
        return clientesFiltrados;
    }
}
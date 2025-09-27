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
 * Servicio para gestionar la lógica de negocio relacionada con clientes.
 * 
 * @author Elias Manriquez
 */
public class ClienteService {
    private ClienteRepository clienteRepository;
    private SectorRepository sectorRepository;
    private PlanRepository planRepository;
    private SectorService sectorService;
    private PlanService planService;
    
    public ClienteService(ClienteRepository clienteRepository, 
                         SectorRepository sectorRepository,
                         PlanRepository planRepository) {
        this.clienteRepository = clienteRepository;
        this.sectorRepository = sectorRepository;
        this.planRepository = planRepository;
    }
    
    /**
     * Configura las referencias a otros servicios (evita dependencias circulares)
     */
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
        cal.add(Calendar.MONTH, 1); // Cambio: 1 mes en lugar de 12
        Date fechaTermino = cal.getTime();
        
        Suscripcion nuevaSuscripcion = new Suscripcion(fechaInicio, fechaTermino, "ACTIVA", nuevoCliente, plan);
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
    
    public List<Cliente> obtenerClientesPorSector(String nombreSector) {
        return clienteRepository.findBySector(nombreSector);
    }
    
    public List<Cliente> obtenerClientesPorEstado(String estado) {
        return clienteRepository.findByEstadoSuscripcion(estado);
    }
    
    public List<Cliente> obtenerClientesPorPlan(String codigoPlan) {
        return clienteRepository.findByPlan(codigoPlan);
    }
    
    public boolean eliminarCliente(String rut) {
        return clienteRepository.delete(rut);
    }
    
    public boolean existeCliente(String rut) {
        return clienteRepository.exists(rut);
    }
    
    public int contarClientesTotales() {
        return clienteRepository.countTotal();
    }
    
    public int contarClientesPorSector(String nombreSector) {
        return clienteRepository.countBySector(nombreSector);
    }
    
    public Map<String, Integer> obtenerEstadisticasClientes() {
        Map<String, Integer> estadisticas = new HashMap<>();
        estadisticas.put("TOTAL", clienteRepository.countTotal());
        estadisticas.put("ACTIVOS", clienteRepository.findByEstadoSuscripcion("ACTIVA").size());
        estadisticas.put("SUSPENDIDOS", clienteRepository.findByEstadoSuscripcion("SUSPENDIDA").size());
        estadisticas.put("CANCELADOS", clienteRepository.findByEstadoSuscripcion("CANCELADA").size());
        return estadisticas;
    }
    
    public boolean actualizarEstadoSuscripcion(String rut, String nuevoEstado) {
        Cliente cliente = clienteRepository.findByRut(rut);
        if (cliente != null && cliente.getSuscripcion() != null) {
            cliente.getSuscripcion().setEstado(nuevoEstado);
            
            // IMPORTANTE: Guardar los cambios en el CSV usando servicios inyectados
            if (sectorService != null && planService != null) {
                boolean guardado = CsvManager.guardarDatos(sectorService, this, planService);
                
                if (guardado) {
                    LoggerHelper.success("✅ Estado de suscripción actualizado y guardado para: " + cliente.getNombre());
                } else {
                    LoggerHelper.error("❌ Error al guardar cambios de estado para: " + cliente.getNombre());
                }
                return guardado;
            } else {
                LoggerHelper.warning("⚠️ Servicios no configurados, cambios no guardados en CSV");
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
    
    /**
     * Valida una suscripción antes de crearla o modificarla
     */
    public void validarSuscripcion(String rutCliente, String codigoPlan) throws SuscripcionInvalidaException {
        Cliente cliente = clienteRepository.findByRut(rutCliente);
        if (cliente == null) {
            throw new SuscripcionInvalidaException(
                "Cliente no encontrado para crear suscripción", 
                "CLIENTE_NO_ENCONTRADO", 
                rutCliente, 
                codigoPlan
            );
        }
        
        PlanSector plan = planRepository.findByCodigo(codigoPlan);
        if (plan == null) {
            throw new SuscripcionInvalidaException(
                "Plan no encontrado para crear suscripción", 
                "PLAN_NO_ENCONTRADO", 
                rutCliente, 
                codigoPlan
            );
        }
        
        // Verificar si ya tiene una suscripción activa
        if (cliente.getSuscripcion() != null && 
            "ACTIVA".equals(cliente.getSuscripcion().getEstado())) {
            throw new SuscripcionInvalidaException(
                "Cliente ya tiene una suscripción activa", 
                "SUSCRIPCION_DUPLICADA", 
                rutCliente, 
                codigoPlan
            );
        }
    }
}
package tvsystem.service;

import tvsystem.model.*;
import tvsystem.repository.*;
import tvsystem.util.RutValidator;
import tvsystem.util.CsvManager;
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
    
    public ClienteService(ClienteRepository clienteRepository, 
                         SectorRepository sectorRepository,
                         PlanRepository planRepository) {
        this.clienteRepository = clienteRepository;
        this.sectorRepository = sectorRepository;
        this.planRepository = planRepository;
    }
    
    public boolean agregarCliente(String nombreSector, String nombre, String rut, 
                                String domicilio, String codigoPlan) {
        // Validar RUT
        if (!RutValidator.validarRut(rut)) {
            throw new IllegalArgumentException("RUT inválido: " + rut);
        }
        
        // Verificar que no exista el cliente
        if (clienteRepository.exists(rut)) {
            throw new IllegalArgumentException("Ya existe un cliente con RUT: " + rut);
        }
        
        // Verificar que exista el sector
        if (!sectorRepository.exists(nombreSector)) {
            throw new IllegalArgumentException("Sector no encontrado: " + nombreSector);
        }
        
        // Verificar que exista el plan
        PlanSector plan = planRepository.findByCodigo(codigoPlan);
        if (plan == null) {
            throw new IllegalArgumentException("Plan no encontrado: " + codigoPlan);
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
            
            // IMPORTANTE: Guardar los cambios en el CSV
            SectorService sectorService = new SectorService(sectorRepository);
            PlanService planService = new PlanService(planRepository);
            boolean guardado = CsvManager.guardarDatos(sectorService, this, planService);
            
            if (guardado) {
                System.out.println("✅ Estado de suscripción actualizado y guardado para: " + cliente.getNombre());
            } else {
                System.out.println("❌ Error al guardar cambios de estado para: " + cliente.getNombre());
            }
            return guardado;
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
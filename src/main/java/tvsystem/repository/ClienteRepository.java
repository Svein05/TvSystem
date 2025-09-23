package tvsystem.repository;

import tvsystem.model.Cliente;
import tvsystem.model.Sector;
import java.util.*;

/**
 * Repository para gestionar el acceso a datos de clientes.
 * 
 * @author Elias Manriquez
 */
public class ClienteRepository {
    private SectorRepository sectorRepository;
    
    public ClienteRepository(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }
    
    public boolean save(Cliente cliente, String nombreSector) {
        Sector sector = sectorRepository.findByNombre(nombreSector);
        if (sector != null) {
            sector.addCliente(cliente);
            return true;
        }
        return false;
    }
    
    public Cliente findByRut(String rut) {
        for (Sector sector : sectorRepository.findAll()) {
            Cliente cliente = sector.getCliente(rut);
            if (cliente != null) {
                return cliente;
            }
        }
        return null;
    }
    
    public List<Cliente> findAll() {
        List<Cliente> todosLosClientes = new ArrayList<>();
        for (Sector sector : sectorRepository.findAll()) {
            todosLosClientes.addAll(sector.getClientes());
        }
        return todosLosClientes;
    }
    
    public List<Cliente> findBySector(String nombreSector) {
        Sector sector = sectorRepository.findByNombre(nombreSector);
        if (sector != null) {
            return sector.getClientes();
        }
        return new ArrayList<>();
    }
    
    public List<Cliente> findByEstadoSuscripcion(String estado) {
        List<Cliente> clientesFiltrados = new ArrayList<>();
        for (Cliente cliente : findAll()) {
            if (cliente.getSuscripcion() != null && 
                cliente.getSuscripcion().getEstado().equalsIgnoreCase(estado)) {
                clientesFiltrados.add(cliente);
            }
        }
        return clientesFiltrados;
    }
    
    public List<Cliente> findByPlan(String codigoPlan) {
        List<Cliente> clientesFiltrados = new ArrayList<>();
        for (Cliente cliente : findAll()) {
            if (cliente.getSuscripcion() != null && 
                cliente.getSuscripcion().getPlan().getCodigoPlan().equals(codigoPlan)) {
                clientesFiltrados.add(cliente);
            }
        }
        return clientesFiltrados;
    }
    
    public boolean delete(String rut) {
        for (Sector sector : sectorRepository.findAll()) {
            if (sector.getCliente(rut) != null) {
                sector.removeCliente(rut);
                return true;
            }
        }
        return false;
    }
    
    public int countTotal() {
        return findAll().size();
    }
    
    public int countBySector(String nombreSector) {
        return findBySector(nombreSector).size();
    }
    
    public boolean exists(String rut) {
        return findByRut(rut) != null;
    }
}
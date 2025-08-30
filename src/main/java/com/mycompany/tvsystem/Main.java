/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tvsystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Maxi
 * @author Elias
 */
public class Main {

    private static GestionCobertura gestionCobertura = new GestionCobertura();
    private static BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        inicializarDatos();
        mostrarMenu();
    }
    
    private static void inicializarDatos() {
        inicializarSectores();
        inicializarPlanes();
    }
    
    private static void mostrarMenu() {
        int opcion;
        try {
            do {
                System.out.println("\n=== SISTEMA DE TELEVISIÓN ===");
                System.out.println("1. Asignar cliente a sector");
                System.out.println("2. Mostrar clientes por sector");
                System.out.println("3. Identificar sectores débiles para ofertas automáticas");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                
                opcion = Integer.parseInt(lector.readLine());
                
                switch (opcion) {
                    case 1:
                        asignarClienteASector();
                        break;
                    case 2:
                        mostrarClientesPorSector();
                        break;
                    case 3:
                        System.out.println("Funcionalidad próximamente disponible...");
                        break;
                    case 0:
                        System.out.println("¡Gracias por usar el sistema!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } while (opcion != 0);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private static void asignarClienteASector() {
        try {
            System.out.println("\n--- ASIGNAR CLIENTE A SECTOR ---");
            
            // Mostrar sectores disponibles
            System.out.println("Sectores disponibles:");
            List<String> sectores = gestionCobertura.obtenerNombresSectores();
            for (String sector : sectores) {
                System.out.println("- " + sector);
            }
            
            System.out.print("Ingrese el sector: ");
            String nombreSector = lector.readLine().toUpperCase();
            
            // Mostrar planes disponibles
            System.out.println("Planes disponibles:");
            for (String codigoPlan : gestionCobertura.getPlanesDisponibles().keySet()) {
                PlanTelevision plan = gestionCobertura.getPlanesDisponibles().get(codigoPlan);
                System.out.println("- " + codigoPlan + ": " + plan.getNombrePlan() + " ($" + plan.getPrecioMensual() + ")");
            }
            
            System.out.print("Ingrese el código del plan: ");
            String codigoPlan = lector.readLine().toUpperCase();
            
            // Solicitar datos del cliente
            System.out.print("Ingrese el nombre del cliente: ");
            String nombre = lector.readLine();
            
            System.out.print("Ingrese el RUT del cliente: ");
            String rut = lector.readLine();
            
            System.out.print("Ingrese el domicilio del cliente: ");
            String domicilio = lector.readLine();
            
            // Intentar agregar el cliente
            boolean exitoso = gestionCobertura.agregarCliente(nombreSector, nombre, rut, domicilio, codigoPlan);
            
            if (exitoso) {
                System.out.println("¡Cliente agregado exitosamente al sector " + nombreSector + "!");
            } else {
                System.out.println("Error: No se pudo agregar el cliente. Verifique el sector y el plan.");
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private static void mostrarClientesPorSector() {
        try {
            System.out.println("\n--- CLIENTES POR SECTOR ---");
            System.out.println("1. Mostrar General");
            System.out.println("2. Filtrar por Comuna");
            System.out.println("3. Filtrar por Plan");
            System.out.print("Seleccione una opción: ");
            
            int opcion = Integer.parseInt(lector.readLine());
            
            switch (opcion) {
                case 1:
                    mostrarGeneral();
                    break;
                case 2:
                    filtrarPorComuna();
                    break;
                case 3:
                    filtrarPorPlan();
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private static void mostrarGeneral() {
        System.out.println("\n--- LISTADO GENERAL DE CLIENTES ---");
        
        List<SectorDeCobertura> sectores = gestionCobertura.obtenerSectores();
        
        for (SectorDeCobertura sector : sectores) {
            // Solo mostrar sectores que tienen clientes
            if (sector.contarClientes() > 0) {
                System.out.println("\nSector: " + sector.getNombre());
                System.out.println("Cantidad de clientes: " + sector.contarClientes());
                
                System.out.println("Clientes:");
                List<Cliente> clientes = sector.getClientes();
                for (Cliente cliente : clientes) {
                    System.out.println("  - " + cliente.mostrarInfo(true, true));
                    System.out.println("    Plan: " + cliente.getSuscripcion().getPlan().getNombrePlan());
                    System.out.println();
                }
                System.out.println("----------------------------------------");
            }
        }
    }
    
    private static void filtrarPorComuna() {
        try {
            System.out.println("\n--- FILTRAR POR COMUNA ---");
            
            // Mostrar solo comunas con clientes
            System.out.println("Comunas con clientes:");
            List<SectorDeCobertura> sectoresConClientes = new ArrayList<>();
            for (SectorDeCobertura sector : gestionCobertura.obtenerSectores()) {
                if (sector.contarClientes() > 0) {
                    sectoresConClientes.add(sector);
                    System.out.println("- " + sector.getNombre());
                }
            }
            
            if (sectoresConClientes.isEmpty()) {
                System.out.println("No hay clientes registrados en ninguna comuna.");
                return;
            }
            
            System.out.print("Ingrese la comuna: ");
            String nombreComuna = lector.readLine().toUpperCase();
            
            SectorDeCobertura sector = gestionCobertura.getSectoresCobertura().get(nombreComuna);
            
            if (sector != null && sector.contarClientes() > 0) {
                System.out.println("\nClientes en " + sector.getNombre() + ":");
                List<Cliente> clientes = sector.getClientes();
                for (Cliente cliente : clientes) {
                    System.out.println("  - " + cliente.mostrarInfo(true, true));
                    System.out.println("    Plan: " + cliente.getSuscripcion().getPlan().getNombrePlan());
                    System.out.println();
                }
            } else {
                System.out.println("No hay clientes en la comuna especificada o la comuna no existe.");
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private static void filtrarPorPlan() {
        try {
            System.out.println("\n--- FILTRAR POR PLAN ---");
            
            // Mostrar planes disponibles
            System.out.println("Planes disponibles:");
            for (String codigoPlan : gestionCobertura.getPlanesDisponibles().keySet()) {
                PlanTelevision plan = gestionCobertura.getPlanesDisponibles().get(codigoPlan);
                System.out.println("- " + codigoPlan + ": " + plan.getNombrePlan());
            }
            
            System.out.print("Ingrese el código del plan: ");
            String codigoPlan = lector.readLine().toUpperCase();
            
            PlanTelevision planBuscado = gestionCobertura.getPlanesDisponibles().get(codigoPlan);
            
            if (planBuscado != null) {
                System.out.println("\nClientes con plan " + planBuscado.getNombrePlan() + ":");
                boolean encontroClientes = false;
                
                for (SectorDeCobertura sector : gestionCobertura.obtenerSectores()) {
                    if (sector.contarClientes() > 0) {
                        List<Cliente> clientesDelPlan = new ArrayList<>();
                        
                        for (Cliente cliente : sector.getClientes()) {
                            if (cliente.getSuscripcion().getPlan().getNombrePlan().equals(planBuscado.getNombrePlan())) {
                                clientesDelPlan.add(cliente);
                            }
                        }
                        
                        if (!clientesDelPlan.isEmpty()) {
                            if (!encontroClientes) {
                                encontroClientes = true;
                            }
                            System.out.println("\nEn " + sector.getNombre() + ":");
                            for (Cliente cliente : clientesDelPlan) {
                                System.out.println("  - " + cliente.mostrarInfo(true, true));
                                System.out.println();
                            }
                        }
                    }
                }
                
                if (!encontroClientes) {
                    System.out.println("No hay clientes con este plan.");
                }
            } else {
                System.out.println("Plan no encontrado.");
            }
        } catch (IOException e) {
            System.out.println("Error al leer entrada: " + e.getMessage());
        }
    }
    
    private static void inicializarSectores() {
        String[] nombresSectores = {
            "VALPARAISO", "VINA_DEL_MAR", "QUILPUE", "VILLA_ALEMANA", 
            "LIMACHE", "OLMUE", "SAN_ANTONIO", "CARTAGENA", "EL_QUISCO", 
            "ALGARROBO", "SAN_FELIPE", "LOS_ANDES", "LLAY_LLAY", 
            "QUINTERO", "CONCON"
        };
        
        for (String nombreSector : nombresSectores) {
            SectorDeCobertura sector = new SectorDeCobertura(nombreSector);
            gestionCobertura.getSectoresCobertura().put(nombreSector, sector);
        }
    }
    
    private static void inicializarPlanes() {
        // Datos de los planes: {codigo, nombre, precio, incluye premium, descuento}
        Object[][] datosPlanes = {
            {"BASICO", "Plan Básico", 15000, false, 0.0},
            {"PREMIUM", "Plan Premium", 25000, true, 0.1},
            {"FAMILIAR", "Plan Familiar", 35000, false, 0.0}
        };
        
        for (Object[] datos : datosPlanes) {
            String codigo = (String) datos[0];
            String nombre = (String) datos[1];
            int precio = (Integer) datos[2];
            boolean incluyePremium = (Boolean) datos[3];
            double descuento = (Double) datos[4];
            
            PlanTelevision plan = new PlanTelevision(nombre, precio, incluyePremium, descuento);
            gestionCobertura.getPlanesDisponibles().put(codigo, plan);
        }
    }
}

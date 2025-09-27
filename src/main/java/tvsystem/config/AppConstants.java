package tvsystem.config;

import java.awt.Color;

/**
 * Constantes de la aplicación.
 * 
 * @author Elias Manriquez
 */
public final class AppConstants {
    
    // Prevenir instanciacion
    private AppConstants() {}
    
    // --- CLASE ANIDADA PARA UMBRALES ---
    public static final class THRESHOLDS {
        private THRESHOLDS() {}
        
        public static final int UMBRAL_SECTOR_DEBIL = 5;
        public static final int UMBRAL_SECTOR_CRITICO = 25;
        public static final int UMBRAL_SECTOR_MODERADO = 50;
        public static final int UMBRAL_SECTOR_EXCELENTE = 100;
    }
    
    // --- CLASE ANIDADA PARA DESCUENTOS ---
    public static final class DISCOUNTS {
        private DISCOUNTS() {}
        
        public static final double DESCUENTO_CRITICO = 0.30;      // 30%
        public static final double DESCUENTO_ALTO = 0.20;         // 20%
        public static final double DESCUENTO_MODERADO = 0.15;     // 15%
    }
    
    // --- CONFIGURACIÓN DE REPORTES ---
    public static final String NOMBRE_REPORTE_DEFAULT = "reporte_analisis_sectores.txt";
    public static final String NOMBRE_CSV_DEFAULT = "datos_tv_system.csv";
    
    // --- COLORES DE LA INTERFAZ ---
    public static final Color COLOR_BOTON_GUARDAR = new Color(76, 175, 80);    // Verde
    public static final Color COLOR_BOTON_REPORTE = new Color(33, 150, 243);   // Azul
    public static final Color COLOR_BOTON_DANGER = new Color(244, 67, 54);     // Rojo
    public static final Color COLOR_BOTON_WARNING = new Color(255, 152, 0);    // Naranja
    public static final Color COLOR_TEXTO_BLANCO = Color.WHITE; // Blanco
    public static final Color COLOR_FONDO_INFO = new Color(240, 248, 255);     // Azul claro
    
    // --- COLORES PARA GRÁFICOS ---
    public static final Color[] COLORES_GRAFICOS = {
        new Color(33, 150, 243),   // Azul
        new Color(76, 175, 80),    // Verde
        new Color(255, 152, 0),    // Naranja
        new Color(156, 39, 176),   // Purpura
        new Color(244, 67, 54),    // Rojo
        new Color(0, 150, 136),    // Verde azulado
        new Color(255, 193, 7),    // Amarillo
        new Color(96, 125, 139),   // Azul gris
        new Color(205, 220, 57),   // Lima
        new Color(255, 87, 34),    // Naranja profundo
        new Color(121, 85, 72),    // Marrón
        new Color(63, 81, 181),    // Indigo
        new Color(233, 30, 99),    // Rosa
        new Color(0, 188, 212),    // Cian
        new Color(139, 195, 74),   // Verde claro
        new Color(255, 111, 97),   // Coral
        new Color(126, 87, 194)    // Violeta
    };
    
    // --- CONFIGURACIÓN DE SUSCRIPCIONES ---
    public static final int DURACION_SUSCRIPCION_MESES = 1;
    public static final String ESTADO_SUSCRIPCION_ACTIVA = "ACTIVA";
    public static final String ESTADO_SUSCRIPCION_SUSPENDIDA = "SUSPENDIDA";
    public static final String ESTADO_SUSCRIPCION_CANCELADA = "CANCELADA";
    
    // --- MENSAJES DE LA APLICACIÓN ---
    public static final String MSG_DATOS_GUARDADOS = "Datos guardados correctamente";
    public static final String MSG_ERROR_GUARDAR = "Error al guardar los datos";
    public static final String MSG_SIN_ARCHIVO = "No hay archivo seleccionado para guardar";
    public static final String MSG_REPORTE_GENERADO = "Reporte generado exitosamente";
    public static final String MSG_ERROR_REPORTE = "Error al generar el reporte";
    public static final String MSG_OPERACION_CANCELADA = "Operación cancelada por el usuario";
    
    // --- TITULOS DE VENTANAS Y DIALOGOS ---
    public static final String TITULO_APLICACION = "Sistema de Gestión de Televisión";
    public static final String TITULO_REPORTE = "Reporte de Análisis";
    public static final String TITULO_ERROR = "Error";
    public static final String TITULO_CONFIRMACION = "Confirmación";
    public static final String TITULO_GUARDAR_CAMBIOS = "Guardar cambios";
    
    // --- SECTORES POR DEFECTO ---
    public static final String[] SECTORES_DEFAULT = {
        "VALPARAISO", "VINA_DEL_MAR", "QUILPUE", "VILLA_ALEMANA", 
        "LIMACHE", "OLMUE", "SAN_ANTONIO", "CARTAGENA", "EL_QUISCO", 
        "ALGARROBO", "SAN_FELIPE", "LOS_ANDES", "QUINTERO", "CONCON"
    };
    
    // --- CONFIGURACION DE TABLAS ---
    public static final String[] COLUMNAS_TABLA_CLIENTES = {
        "RUT", "Nombre", "Sector", "Plan", "Estado", "Precio Final"
    };
    
    // --- FILTROS DE BUSQUEDA ---
    public static final String[] OPCIONES_FILTRO = {"Todos", "Sector", "Plan", "Estado"};
    public static final String FILTRO_TODOS = "Todos";
    
    // --- CONFIGURACION DE UI ---
    public static final int TOOLTIP_DELAY_MS = 500;
    public static final int TOOLTIP_DISMISS_MS = 10000;
    
    // --- DIMENSIONES DE BOTONES ---
    public static final int BOTON_ANCHO_PEQUEÑO = 100;
    public static final int BOTON_ALTO_PEQUEÑO = 35;
    public static final int BOTON_ANCHO_MEDIANO = 120;
    public static final int BOTON_ALTO_MEDIANO = 40;
    public static final int BOTON_ANCHO_GRANDE = 180;
    public static final int BOTON_ALTO_GRANDE = 45;
    
    // --- VALIDACION ---
    public static final String REGEX_RUT_FORMATO = "^[0-9]+-[0-9Kk]$";
    
    // --- ARCHIVOS Y EXTENSIONES ---
    public static final String EXTENSION_CSV = ".csv";
    public static final String EXTENSION_TXT = ".txt";
    public static final String DESCRIPCION_CSV = "Archivos CSV (*.csv)";
    public static final String DESCRIPCION_TXT = "Archivos TXT (*.txt)";
}
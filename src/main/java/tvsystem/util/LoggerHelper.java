package tvsystem.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper centralizado para logging del sistema.
 * 
 * @author Elias Manriquez
 */
public class LoggerHelper {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static boolean debugEnabled = true;
    private static boolean infoEnabled = true;
    
    public enum LogLevel {
        DEBUG("[DEBUG]"), INFO("[INFO]"), SUCCESS("[SUCCESS]"), WARNING("[WARNING]"), ERROR("[ERROR]");
        
        private final String prefix;
        
        LogLevel(String prefix) {
            this.prefix = prefix;
        }
        
        public String getPrefix() {
            return prefix;
        }
    }
    
    // Log de informacion general
    public static void info(String mensaje) {
        if (infoEnabled) {
            log(LogLevel.INFO, mensaje);
        }
    }
    
    // Log de depuracion
    public static void debug(String mensaje) {
        if (debugEnabled) {
            log(LogLevel.DEBUG, mensaje);
        }
    }
    
    // Log de exito
    public static void success(String mensaje) {
        log(LogLevel.SUCCESS, mensaje);
    }
    
    // Log de advertencia
    public static void warning(String mensaje) {
        log(LogLevel.WARNING, mensaje);
    }
    
    // Log de error
    public static void error(String mensaje) {
        log(LogLevel.ERROR, mensaje);
    }
    
    // Log con excepcion
    public static void error(String mensaje, Exception e) {
        log(LogLevel.ERROR, mensaje + ": " + e.getMessage());
    }
    
    // Log de operaciones del sistema
    public static void system(String mensaje) {
        System.out.println(formatMessage("[SYSTEM]", mensaje));
    }
    
    // Log de inicializacion del sistema
    public static void init(String mensaje) {
        System.out.println(formatMessage("[INIT]", mensaje));
    }
    
    // Log de datos/estadisticas
    public static void data(String mensaje) {
        if (infoEnabled) {
            log(LogLevel.INFO, "[DATA] " + mensaje);
        }
    }
    
    // --- METODOS PRIVADOS ---
    
    private static void log(LogLevel level, String mensaje) {
        System.out.println(formatMessage(level.getPrefix(), mensaje));
    }
    
    private static String formatMessage(String prefix, String mensaje) {
        return String.format("[%s] %s %s", 
            LocalDateTime.now().format(TIMESTAMP_FORMAT), 
            prefix, 
            mensaje);
    }
    
    // Log de servicio inicializado
    public static void serviceInitialized(String serviceName) {
        init("Servicio inicializado: " + serviceName);
    }
}
package tvsystem.util;

/**
 * Utilidad para validar RUT chilenos.
 * Implementa el algoritmo de validación del dígito verificador.
 * 
 * @author Elias Manriquez
 */
public class RutValidator {
    
    /**
     * Valida un RUT completo en formato "12345678-9" o "12345678-K"
     */
    public static boolean validarRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        
        // Limpiar el RUT
        rut = limpiarRut(rut);
        
        // Verificar longitud mínima
        if (rut.length() < 2) {
            return false;
        }
        
        // Separar número y dígito verificador
        String numero = rut.substring(0, rut.length() - 1);
        char digitoVerificador = rut.charAt(rut.length() - 1);
        
        // Verificar que el número contenga solo dígitos
        if (!numero.matches("\\d+")) {
            return false;
        }
        
        // Calcular dígito verificador esperado
        char dvEsperado = calcularDigitoVerificador(numero);
        
        return digitoVerificador == dvEsperado;
    }
    
    /**
     * Limpia el RUT removiendo puntos, guiones y espacios, y convierte a mayúsculas
     */
    public static String limpiarRut(String rut) {
        return rut.replaceAll("[.\\-\\s]", "").toUpperCase();
    }
    
    /**
     * Calcula el dígito verificador para un número de RUT
     */
    private static char calcularDigitoVerificador(String numero) {
        int suma = 0;
        int multiplicador = 2;
        
        // Recorrer el número de derecha a izquierda
        for (int i = numero.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(numero.charAt(i)) * multiplicador;
            multiplicador++;
            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }
        
        int resto = suma % 11;
        int dv = 11 - resto;
        
        if (dv == 11) {
            return '0';
        } else if (dv == 10) {
            return 'K';
        } else {
            return Character.forDigit(dv, 10);
        }
    }
    
    /**
     * Formatea un RUT en el formato estándar "12.345.678-9"
     */
    public static String formatearRut(String rut) {
        if (!validarRut(rut)) {
            return rut; // Retorna el original si no es válido
        }
        
        rut = limpiarRut(rut);
        String numero = rut.substring(0, rut.length() - 1);
        char dv = rut.charAt(rut.length() - 1);
        
        // Agregar puntos cada 3 dígitos desde la derecha
        StringBuilder numeroFormateado = new StringBuilder();
        for (int i = 0; i < numero.length(); i++) {
            if (i > 0 && (numero.length() - i) % 3 == 0) {
                numeroFormateado.append('.');
            }
            numeroFormateado.append(numero.charAt(i));
        }
        
        return numeroFormateado.toString() + '-' + dv;
    }
}
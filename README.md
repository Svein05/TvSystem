# 📺 TvSystem - Guía de Uso

Sistema de gestión para televisión por cable que administra clientes, sectores y planes de servicio con captación automática.

## 🚀 Ejecución

1. Abrir proyecto en NetBeans IDE
2. Clic derecho → "Clean and Build" 
3. Clic derecho → "Run"

## 📖 Uso del Sistema

### Inicio
- Al ejecutar se solicita archivo CSV (abrir existente o crear nuevo)
- Se inicializa automáticamente con datos base

### Gestión de Clientes
**Agregar:**
- Pestaña "Gestión de Clientes" → "Agregar Cliente"
- Completar campos (Nombre, RUT, Domicilio, Sector, Plan)
- El sistema valida RUT chileno automáticamente

**Buscar/Modificar:**
- Usar barra de búsqueda para filtrar
- Seleccionar cliente de la tabla
- Editar en panel de detalles o "Ver detalles" → "Suscripción"

### Gestión de Sectores
- Vista en cuadrícula con botones por sector
- Cada botón muestra: nombre, clientes e ingresos
- Clic en sector para ver detalles y lista de clientes

### Sistema de Captación
- Identifica sectores con pocos clientes (umbral configurable)
- Aplicar descuentos automáticos por categoría:
  - Muy críticos (0-33%): 30% descuento
  - Críticos (34-66%): 20% descuento
  - Moderados (67-99%): 15% descuento

### Reportes
- Gráficos: distribución por sector y ingresos
- Estadísticas: totales, sectores populares, efectividad
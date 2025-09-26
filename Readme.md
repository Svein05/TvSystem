# 📺 Sistema de Gestión de Televisión

## 📋 Descripción

TvSystem es una aplicación integral de gestión para empresas de televisión por cable que permite administrar clientes, sectores, planes de servicio y realizar captación automática de clientes. El sistema ofrece tanto una interfaz gráfica de usuario moderna como funcionalidades de importación/expor- **Fechas**: Validación de coherencia temporal

## 👥 Autores

- 👨‍💻 **Elias Manriquez** - Desarrollo backend y servicios
- 🎨 **Maximiliano Rodriguez** - Desarrollo frontend y arquitecturaión de datos mediante archivos CSV.

### ⭐ Características Principales

- 👥 **Gestión de Clientes**: Registro, modificación y consulta de clientes con validación de RUT chileno
- 🏘️ **Administración de Sectores**: Organización territorial de clientes y planes específicos por zona en la región de Valparaíso. 
- 📋 **Gestión de Planes**: Creación y administración de planes de televisión con ofertas dinámicas
- 🎯 **Captación Automática**: Sistema inteligente de identificación de sectores débiles y activación de ofertas
- 🖥️ **Interfaz Gráfica**: Aplicación desktop con visualización de datos mediante gráficos y tablas
- 📂 **Importación/Exportación**: Manejo de datos mediante archivos CSV
- ✅ **Validación de Datos**: Sistema robusto de validación incluye RUTs chilenos

## 💻 Requisitos del Sistema

### 🛠️ Software Necesario

- ☕ **Java 11** o superior
- 🔧 **Apache Maven 3.6+** (para compilación)
- 🌟 **NetBeans IDE** (recomendado para desarrollo)
- 🖥️ Sistema operativo: Windows, macOS, o Linux

### 📦 Dependencias

El proyecto utiliza las siguientes tecnologías:
- 🖼️ Java Swing para la interfaz gráfica
- 🔗 Maven para gestión de dependencias
- 🎨 AbsoluteLayout de NetBeans para diseño de ventanas

## ⚙️ Instalación y Ejecución

### 🌟 Ejecución en NetBeans IDE

1. **📥 Descargar NetBeans IDE**: Asegúrese de tener NetBeans instalado (versión 12 o superior recomendada)
2. **📁 Abrir el proyecto**: Seleccionar "Open Project" y navegar hasta la carpeta del proyecto TvSystem
3. **🔨 Compilar**: Hacer clic derecho en el proyecto → "Clean and Build"
4. **▶️ Ejecutar**: Hacer clic derecho en el proyecto → "Run"

> 💡 **Nota**: NetBeans manejará automáticamente las dependencias de Maven y la configuración del proyecto.

## 📖 Guía de Uso

### 🚀 Inicio de la Aplicación

Al ejecutar TvSystem, el sistema:

1. ⚡ **Inicializa automáticamente** repositorios y servicios
2. 📊 **Carga datos base** del sistema (sectores y planes predefinidos)
3. 📂 **Solicita archivo CSV** para cargar datos existentes o crear uno nuevo
4. 🖥️ **Abre la interfaz gráfica** principal

### 📄 Gestión de Archivos CSV

#### 🔄 Al Iniciar
- 📋 El sistema presenta opciones para abrir un archivo existente o crear uno nuevo
- 📝 Los archivos CSV deben seguir el formato: `SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL`

#### 📊 Formato del Archivo CSV
```csv
SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL
CARTAGENA,Carmen Garci­a,30.251.795-9,Avenida Libertad 147,BASICO_CARTAGENA,Fri Sep 26 12:53:30 CEST 2025,Sun Oct 26 12:53:30 CET 2025,ACTIVA,15000,0,00,15000
VILLA_ALEMANA,Maria Gonzalez,78.314.061-6,Calle del Carmen 741,PREMIUM_VILLA_ALEMANA,Fri Sep 26 12:53:30 CEST 2025,Sun Oct 26 12:53:30 CET 2025,ACTIVA,25000,0,00,25000
```

### ⚡ Funcionalidades Principales

#### 👥 1. Gestión de Clientes

**➕ Agregar Cliente:**
- 🖱️ haz click en la pestaña "Gestion de Clientes" ubicada en la esquina superior izquierda
- 🖱️ haz click en la pestaña "Agregar Cliente"
- ✍️ Complete los campos: Nombre, RUT, Domicilio, Sector y Plan
- 💾 Haga clic en "Aceptar"
- ✅ El sistema validará automáticamente el RUT chileno e indicará que ha sido guardado con exito

**🔍 Buscar y Modificar:**
- 🔎 Use la barra de búsqueda para filtrar clientes por zonas especificas, planes o estado. Tambien se puede ordenar clientes por RUT, nombre, zona o planes
- 👆 Seleccione un cliente de la tabla para ver/editar/eliminar
- ✏️ Modifique información en el panel de detalles
> Para modificar nombre o domicilio haz click en editar (botón ubicado abajo en el centro) si deseas editar el estado del plan haz click en ver detalles y luego en suscripción.  

#### 🏘️ 2. Visualización de Sectores

**📋 Panel de Sectores:**
- 🗂️ Vista organizada en cuadrícula con botones para cada sector
- 📊 Cada botón muestra: nombre del sector, número de clientes e ingresos
- 🎨 Indicadores visuales de colores para identificar sectores con problemas
- 🖱️ Doble clic en cualquier sector para ver información detallada

**📝 Detalles del Sector:**
- 📋 Lista completa de clientes del sector
- 📦 Planes disponibles y sus características
- 📈 Estadísticas de ocupación y ofertas activas

#### 📋 3. Gestión de Planes

**📊 Consultar Planes:**
- 📑 Pestaña "Planes" muestra todos los planes disponibles
- 🔽 Filtros por sector y estado de oferta
- 💰 Información detallada de precios y descuentos

**🔄 Activar/Desactivar Ofertas:**
- 👆 Seleccione un plan de la lista
- ⚙️ Use los controles para modificar ofertas y descuentos
- ⚡ Los cambios se aplican inmediatamente

#### 🎯 4. Sistema de Captación

**🤖 Captación Automática:**
- 🔍 El sistema identifica sectores con menos de 5 clientes como "débiles"
- 💸 Activa automáticamente descuentos del 15% en planes de esos sectores
- 🎯 Accesible desde el menú "Captación"

**📊 Monitoreo:**
- 👀 Vista de sectores que requieren captación
- 📈 Seguimiento de efectividad de las ofertas
- 📋 Reportes de crecimiento por sector

#### 📊 5. Reportes y Estadísticas

**📈 Gráficos Disponibles:**
- 🥧 Distribución de clientes por sector (gráfico circular)
- 📊 Comparación de precios entre planes (gráfico de barras)
- 📈 Evolución de suscripciones por período

**📊 Datos Estadísticos:**
- 👥 Total de clientes registrados
- 🏆 Sectores más y menos populares
- 💰 Ingresos estimados por sector
- 🎯 Efectividad de ofertas activas

## 🏗️ Estructura del Proyecto

### 🏛️ Arquitectura del Sistema

El proyecto sigue una arquitectura en capas organizada de la siguiente manera:

```
src/main/java/tvsystem/
├── app/                    # Punto de entrada de la aplicación
│   └── TvSystemApplication.java
├── model/                  # Modelos de datos
│   ├── Cliente.java
│   ├── Pago.java
│   ├── PlanSector.java
│   ├── Sector.java
│   └── Suscripcion.java
├── repository/             # Capa de acceso a datos
│   ├── ClienteRepository.java
│   ├── PlanRepository.java
│   └── SectorRepository.java
├── service/                # Lógica de negocio
│   ├── CaptacionService.java
│   ├── ClienteService.java
│   ├── PlanService.java
│   └── SectorService.java
├── util/                   # Utilidades del sistema
│   ├── CsvManager.java
│   ├── DataInitializer.java
│   ├── DatosFicticiosGenerator.java
│   └── RutValidator.java
└── view/                   # Interfaces de usuario
    ├── cli/
    │   └── ConsoleInterface.java
    └── gui/
        ├── ClienteDetailDialog.java
        ├── MainWindow.java
        └── SectorDetailDialog.java
```

### 🧩 Componentes Principales

#### 📊 Modelos de Datos
- 👤 **Cliente**: Representa un cliente con información personal y suscripción
- 🏘️ **Sector**: Agrupa clientes por zona geográfica con planes específicos
- 📋 **PlanSector**: Define planes de televisión con precios y ofertas por sector
- 📝 **Suscripcion**: Gestiona el estado de suscripción de un cliente
- 💳 **Pago**: Registra información de pagos (Muestra si pagó o no)

#### ⚙️ Servicios de Negocio
- 👥 **ClienteService**: Gestión integral de clientes incluyendo validaciones
- 🏘️ **SectorService**: Administración de sectores y análisis de ocupación
- 📋 **PlanService**: Manejo de planes, precios y ofertas
- 🎯 **CaptacionService**: Lógica de captación automática y ofertas estratégicas

## 🔧 Solución de Problemas

### ⚠️ Problemas Comunes

#### 🚫 Error de Inicialización
**🔍 Síntoma**: La aplicación no inicia o falla en el arranque
**🔧 Solución**:
1. ☕ Verificar versión de Java (mínimo Java 11)
2. 📋 Revisar logs en consola para errores específicos
3. 📦 Asegurar que todas las dependencias estén disponibles

#### 📄 Error de Archivo CSV
**🔍 Síntoma**: No se pueden cargar datos desde CSV
**🔧 Solución**:
1. 📊 Verificar formato del archivo CSV
2. 🔤 Comprobar codificación del archivo (debe ser UTF-8)
3. 🆔 Validar que no existan RUTs duplicados o inválidos

#### 🐌 Problemas de Rendimiento
**🔍 Síntoma**: La aplicación se vuelve lenta con muchos datos
**🔧 Solución**:
1. ⚡ Limitar la cantidad de datos cargados inicialmente
2. 🔽 Usar filtros en las búsquedas y visualizaciones
3. 🚀 Considerar aumentar memoria JVM: `java -Xmx512m -jar...`

#### 🖥️ Interfaz Gráfica No Responde
**🔍 Síntoma**: La ventana principal no responde
**🔧 Solución**:
1. 🔄 Forzar cierre y reiniciar aplicación
2. 💾 Verificar disponibilidad de memoria del sistema
3. 🚫 Comprobar que no hay procesos bloqueantes en segundo plano

### 📊 Logs y Diagnóstico

El sistema proporciona información detallada de diagnóstico:

```java
// Para ejecutar diagnóstico del sistema
TvSystemApplication app = new TvSystemApplication();
app.inicializar();
app.diagnosticarSistema();
app.mostrarInformacionSistema();
```

## 🤝 Contribución

### 👨‍💻 Guías para Desarrolladores

1. 🏗️ **Seguir la arquitectura en capas** establecida
2. 🎯 **Mantener separación de responsabilidades** entre servicios
3. ✅ **Implementar validaciones** en la capa de servicio
4. 📚 **Documentar cambios** en interfaces públicas
5. 🧪 **Probar funcionalidades** con datos de prueba

### 📝 Estándares de Código

- 🇪🇸 Nomenclatura en español para elementos de negocio
- 📖 Comentarios JavaDoc para métodos públicos
- ⚠️ Manejo de excepciones informativo
- ✅ Validación de parámetros de entrada


## � Tecnologías Utilizadas

### 🔧 Backend
- **Java 11**: Lenguaje de programación principal
- **Maven**: Gestión de dependencias y construcción del proyecto
- **Arquitectura MVC**: Separación clara entre modelo, vista y controlador

### 🎨 Frontend
- **Java Swing**: Biblioteca para interfaz gráfica de usuario
- **AbsoluteLayout**: Layout manager de NetBeans para diseño preciso
- **Graphics2D**: Para elementos gráficos avanzados y visualizaciones

### 🏗️ Arquitectura
- **Patrón Repository**: Para acceso y gestión de datos
- **Patrón Service**: Para lógica de negocio
- **Inyección de dependencias**: Manual mediante constructores
- **Almacenamiento en memoria**: Con persistencia opcional en CSV

## 🌟 Características Técnicas Avanzadas

### 🎯 Sistema de Captación Inteligente
```java
// Configuración automática de ofertas por sectores débiles
private static final int UMBRAL_SECTOR_DEBIL = 5;
private static final double DESCUENTO_CAPTACION = 0.15;
```

### 📊 Análisis de Sectores en Tiempo Real
- **Detección automática** de sectores con baja captación (< 5 clientes)
- **Ofertas dinámicas** activadas automáticamente
- **Cálculo de penetración de mercado** por sector
- **Identificación de sectores prioritarios** (≤ 1 cliente)

### ✅ Validaciones Robustas
- **Validación de RUT chileno** con algoritmo de dígito verificador
- **Formato CSV flexible** con manejo de errores
- **Validación de datos de entrada** en todas las capas
- **Manejo de excepciones** informativo y detallado

### 📈 Reportes y Estadísticas
- **14 sectores geográficos** preconfigurados (Región de Valparaíso)
- **3 tipos de planes base**: Básico ($15.000), Premium ($25.000), Familiar ($35.000)
- **Gráficos interactivos** con colores personalizados
- **Exportación/importación** de datos en formato CSV

## 🔍 Detalles Técnicos de Implementación

### 📁 Gestión de Archivos CSV
El sistema maneja archivos CSV con el siguiente formato:
```csv
sector,nombre,rut,domicilio,plan,precioMensual,ofertaActiva,descuento
VALPARAISO,Juan Pérez,12345678-9,Av. Brasil 123,BASICO_VALPARAISO,15000,false,0.0
VINA_DEL_MAR,María González,87654321-K,Calle Libertad 456,PREMIUM_VINA_DEL_MAR,25000,true,0.15
```

### 🎯 Algoritmo de Captación Automática
1. **Identificación**: Detecta sectores con menos de 5 clientes
2. **Activación**: Aplica descuento del 15% automáticamente
3. **Monitoreo**: Genera reportes de efectividad
4. **Escalado**: Descuentos progresivos según criticidad del sector

### 🏛️ Patrones de Diseño Implementados
- **Repository Pattern**: Abstracción de acceso a datos
- **Service Layer**: Encapsulación de lógica de negocio
- **Observer Pattern**: Para actualizaciones de interfaz
- **Strategy Pattern**: Para diferentes tipos de reportes
- **Factory Pattern**: Para creación de entidades

## 🧪 Testing y Diagnóstico

### 🔬 Herramientas de Diagnóstico
```java
TvSystemApplication app = new TvSystemApplication();
app.inicializar();
app.diagnosticarSistema();      // Verifica estado del sistema
app.mostrarInformacionSistema(); // Muestra estadísticas
```

### ⚡ Verificaciones Automáticas
- **Integridad de repositorios**: Verifica inicialización correcta
- **Validación de servicios**: Comprueba dependencias
- **Estado de datos**: Confirma carga de sectores y planes
- **Memoria del sistema**: Monitorea uso de recursos

## 📊 Métricas del Sistema

### 📈 Estadísticas de Rendimiento
- **Tiempo de inicialización**: < 2 segundos promedio
- **Capacidad de clientes**: Ilimitada (limitado por memoria)
- **Sectores soportados**: Configurable (14 por defecto)
- **Planes por sector**: 3 planes base + personalizados

### 🎯 KPIs de Captación
- **Tasa de captación**: Clientes nuevos por sector/mes
- **Efectividad de ofertas**: % de conversión con descuentos
- **Penetración de mercado**: % de población cubierta por sector
- **Sectores críticos**: Número de sectores con ≤ 1 cliente

## 🔒 Seguridad y Validaciones

### 🛡️ Controles de Seguridad
- **Validación de entrada**: Todos los datos se validan antes del procesamiento
- **Prevención de duplicados**: Control de RUTs únicos por sector
- **Manejo de excepciones**: Error handling robusto en todas las capas
- **Logs detallados**: Trazabilidad completa de operaciones

### ✅ Validaciones Específicas
- **RUT chileno**: Algoritmo de dígito verificador
- **Rangos de precios**: Validación de montos positivos
- **Descuentos**: Rango válido entre 0% y 100%
- **Fechas**: Validación de coherencia temporal

## 👥 Autores

- 👨‍💻 **Elias Manriquez** - Desarrollo backend y servicios
- 🎨 **Maximiliano Rodriguez** - Desarrollo frontend y arquitectura

## � Autores

Este proyecto está bajo la **Licencia MIT** - ver el archivo `LICENSE` para más detalles.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. 🍴 Fork del proyecto
2. 🔄 Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. ✅ Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. 📤 Push a la rama (`git push origin feature/AmazingFeature`)
5. 🔃 Abrir un Pull Request

### 📋 Guidelines de Contribución
- Seguir los estándares de código establecidos
- Incluir tests para nuevas funcionalidades
- Documentar cambios en el README si es necesario
- Mantener la arquitectura en capas existente

## �👥 Autores

- 👨‍💻 **Elias Manriquez** - Desarrollo backend y servicios
- 🎨 **Maximiliano Rodriguez** - Desarrollo frontend y arquitectura




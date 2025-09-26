# 📺 Sistema de Gestión de Televisión

## 📋 Descripción

TvSystem es una aplicación integral de gestión para empresas de televisión por cable que permite administrar clientes, sectores, planes de servicio y realizar captación automática de clientes. El sistema ofrece tanto una interfaz gráfica de usuario moderna como funcionalidades de importación/exportación de datos mediante archivos CSV.

### ⭐ Características Principales

- 👥 **Gestión de Clientes**: Registro, modificación y consulta de clientes con validación de RUT chileno
- 🏘️ **Administración de Sectores**: Organización territorial de clientes y planes específicos por zona
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

## ⚙️ Instalación

### 🚀 Opción 1: Descarga y Ejecución Directa

1. **📥 Clonar el repositorio:**
```bash
git clone https://github.com/Svein05/TvSystem.git
cd TvSystem
```

2. **🔨 Compilar el proyecto:**
```bash
mvn clean compile
```

3. **▶️ Ejecutar la aplicación:**
```bash
mvn exec:java -Dexec.mainClass="tvsystem.app.TvSystemApplication"
```

### 📦 Opción 2: Generar JAR Ejecutable

1. **🏗️ Crear JAR con dependencias:**
```bash
mvn clean package
```

2. **🚀 Ejecutar el JAR generado:**
```bash
java -jar target/tv-system-1.0-SNAPSHOT.jar
```

### 🔧 Opción 3: Desarrollo en NetBeans

1. 🌟 Abrir NetBeans IDE
2. 📁 Seleccionar "Open Project" y navegar hasta la carpeta del proyecto
3. 🔨 Hacer clic derecho en el proyecto → "Clean and Build"
4. ▶️ Hacer clic derecho en el proyecto → "Run"

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
- 📝 Los archivos CSV deben seguir el formato: `sector,nombre,rut,domicilio,plan,precioMensual,ofertaActiva,descuento`

#### 📊 Formato del Archivo CSV
```csv
sector,nombre,rut,domicilio,plan,precioMensual,ofertaActiva,descuento
Centro,Juan Pérez,12345678-9,Av. Principal 123,PLAN-CENTRO-BASICO,25000,false,0.0
Norte,María González,87654321-K,Calle Norte 456,PLAN-NORTE-PREMIUM,45000,true,0.15
```

### ⚡ Funcionalidades Principales

#### 👥 1. Gestión de Clientes

**➕ Agregar Cliente:**
- 🖱️ Navegue a la pestaña "Clientes"
- ✍️ Complete los campos: Nombre, RUT, Domicilio, Sector y Plan
- ✅ El sistema validará automáticamente el RUT chileno
- 💾 Haga clic en "Agregar Cliente"

**🔍 Buscar y Modificar:**
- 🔎 Use la barra de búsqueda para encontrar clientes por RUT o nombre
- 👆 Seleccione un cliente del árbol para ver/editar detalles
- ✏️ Modifique información en el panel de detalles

#### 🏘️ 2. Visualización de Sectores

**📋 Panel de Sectores:**
- 🗂️ Vista en grid de todos los sectores configurados
- 📊 Información resumida: nombre, cantidad de clientes, planes disponibles
- 🖱️ Doble clic para acceder a detalles del sector

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

### ⌨️ Atajos de Teclado

- ➕ **Ctrl + N**: Nuevo cliente
- 💾 **Ctrl + S**: Guardar datos en CSV
- 📂 **Ctrl + O**: Abrir archivo CSV
- 🔄 **F5**: Actualizar datos
- ❌ **Alt + F4**: Cerrar aplicación

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
- 💳 **Pago**: Registra información de pagos (preparado para funcionalidad futura)

#### ⚙️ Servicios de Negocio
- 👥 **ClienteService**: Gestión integral de clientes incluyendo validaciones
- 🏘️ **SectorService**: Administración de sectores y análisis de ocupación
- 📋 **PlanService**: Manejo de planes, precios y ofertas
- 🎯 **CaptacionService**: Lógica de captación automática y ofertas estratégicas

#### 🗄️ Repositorios
- 📚 Capa de abstracción para acceso a datos en memoria
- 🔄 Operaciones CRUD para cada entidad principal
- 🔍 Consultas especializadas para reportes y análisis

## ⚙️ Configuración Avanzada

### 🔧 Personalización de Parámetros

Los siguientes parámetros pueden modificarse en el código fuente:

#### 🎯 CaptacionService.java
```java
private static final int UMBRAL_SECTOR_DEBIL = 5;        // Clientes mínimos por sector
private static final double DESCUENTO_CAPTACION = 0.15;  // 15% de descuento automático
```

#### 🏗️ DataInitializer.java
- 🏘️ Configuración de sectores predefinidos
- 📋 Planes base del sistema
- 🧪 Datos de prueba iniciales

### 📄 Formato de Archivos CSV

#### 📊 Estructura Requerida
```csv
sector,nombre,rut,domicilio,plan,precioMensual,ofertaActiva,descuento
```

#### ✅ Validaciones Aplicadas
- 🆔 **RUT**: Debe cumplir algoritmo de validación chileno
- 🏘️ **Sector**: Debe existir en el sistema o se crea automáticamente
- 📋 **Plan**: Se valida existencia o se crea con valores proporcionados
- 💰 **Precio**: Debe ser numérico positivo
- 💸 **Descuento**: Valor decimal entre 0.0 y 1.0

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


## 👥 Autores

- 👨‍💻 **Elias Manriquez** - Desarrollo backend y servicios
- 🎨 **Maximiliano Rodriguez** - Desarrollo frontend y arquitectura



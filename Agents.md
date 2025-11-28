# Contexto del Proyecto: FlagKit

## Descripción General
FlagKit es una librería de Android diseñada para gestionar la activación y desactivación de funcionalidades (feature flags) de manera agnóstica al proveedor de flags subyacente.

## Objetivo Principal
El objetivo es abstraer la lógica de feature flags de la aplicación principal, permitiendo cambiar o utilizar múltiples proveedores de flags (como Firebase Remote Config, GrowthBook, LaunchDarkly, PostHog, Amplitude, etc.) sin impactar la lógica de negocio de la app.

## Funcionalidades Clave
1.  **Agnosticismo**: La librería no depende directamente de ningún SDK de terceros para feature flags en su núcleo. Actúa como una capa de abstracción.
2.  **Lectura de Flags**: Su responsabilidad principal es leer el estado de los flags y responder si una funcionalidad específica está activada o desactivada.
3.  **Configuración**:
    *   Se configurará mediante un archivo o mecanismo de inicialización donde se define el "Cliente" o adaptador específico.
    *   Permitirá definir cómo leer los valores de los flags (mapeo de claves, valores por defecto, etc.).

## Principios de Diseño y Buenas Prácticas

### 1. Arquitectura Clean (Clean Architecture)
Para garantizar que la librería sea mantenible, escalable y testeable, seguiremos los principios de Clean Architecture, separando las responsabilidades en capas:

*   **Capa de Dominio (Core)**: Contendrá las interfaces (`FlagKitProvider`), las entidades del negocio (definición de un `Flag`) y los casos de uso (ej. `GetFlagStatusUseCase`). Esta capa NO tendrá dependencias de frameworks externos (como Firebase o Android SDKs complejos).
*   **Capa de Datos (Data)**: Implementaciones concretas de los proveedores (`FirebaseFlagProvider`, `GrowthBookFlagProvider`). Aquí se interactúa con los SDKs de terceros.
*   **Capa de Presentación/API**: La interfaz pública que utiliza la app cliente (`FlagKitManager`). Debe ser simple y fácil de usar.

### 2. Principios SOLID
Se hará especial énfasis en:
*   **Principio de Responsabilidad Única (SRP)**: Cada clase debe tener una única razón para cambiar. Por ejemplo, el gestor de caché no debe saber cómo parsear la respuesta de Firebase.
*   **Principio de Abierto/Cerrado (OCP)**: La librería debe estar abierta a la extensión (añadir nuevos proveedores) pero cerrada a la modificación. Usaremos interfaces para permitir que los clientes inyecten sus propios adaptadores sin cambiar el código fuente de FlagKit.
*   **Inyección de Dependencias (DI)**: Permitiremos que las dependencias (como el proveedor de flags) se inyecten en el `FlagKitManager`, facilitando el testing y la configuración.

### 3. Concurrencia y Reactividad (Coroutines & Flow)
Dado que la obtención de flags puede requerir operaciones asíncronas (red, disco):
*   **Coroutines**: Usaremos `suspend functions` para operaciones que puedan bloquear el hilo principal (IO), permitiendo un manejo eficiente de hilos sin callbacks anidados.
*   **StateFlow / SharedFlow**: Para la observación de cambios en los flags en tiempo real. Si un flag cambia en el servidor, la app debería poder reaccionar reactivamente.
    *   `Flow<Boolean>` para observar el estado de un flag específico.

### 4. Mejores Prácticas para Librerías Android
*   **Inicialización Eficiente**: Evitar inicializaciones pesadas en el `Application.onCreate()` a menos que sea estrictamente necesario. Usar `Startup` library o inicialización lazy si es posible.
*   **Minimizar Dependencias Transitivas**: Evitar exponer dependencias de terceros a la app cliente (`api` vs `implementation` en Gradle).
*   **Compatibilidad**: Mantener la compatibilidad binaria y usar `Semantic Versioning`.
*   **Documentación KDoc**: Documentar todas las clases y funciones públicas.
*   **Proguard/R8**: Incluir reglas de consumo (`consumer-rules.pro`) si la librería usa reflexión o serialización específica, para que el cliente no tenga que configurarlas manualmente.

## Estrategia de Testing
Para asegurar la calidad y el correcto funcionamiento de la librería, se implementará una estrategia de testing robusta utilizando las siguientes herramientas y prácticas:

*   **Frameworks de Testing**:
    *   **JUnit 4/5**: Framework base para la ejecución de pruebas unitarias.
    *   **Mockk**: Librería de mocking nativa de Kotlin, ideal para simular el comportamiento de dependencias (como los proveedores de flags o callbacks) y verificar interacciones.
    *   **Kotlin Coroutines Test**: Librería (`kotlinx-coroutines-test`) para probar `suspend functions` y `Flows`. Utilizaremos `runTest` y `TestDispatcher` para controlar el tiempo de ejecución de las corrutinas en los tests.

*   **Enfoque de las Pruebas**:
    *   **Unit Tests (Pruebas Unitarias)**:
        *   Se probará exhaustivamente la lógica de negocio en la capa de Dominio.
        *   Se mockearán las interfaces (`FlagKitProvider`) para probar el `FlagKitManager` de forma aislada, verificando que delega correctamente las llamadas y maneja los valores por defecto.
        *   Se probará el comportamiento de los `Flows` para asegurar que emiten los valores correctos ante cambios.
    *   **Integration Tests (Pruebas de Integración)**:
        *   (Opcional) Pruebas que verifiquen la integración con implementaciones reales o simuladas de proveedores externos en un entorno controlado.

## Arquitectura (Idea General Actualizada)
*   **FlagKitManager**: Punto de entrada (Singleton o instancia inyectada). Expone métodos `suspend` y `Flows`.
*   **IFlagProvider**: Interface que define `getBoolean(key: String): Boolean`, `getString(key: String): String`, etc.
*   **CachedFlagProvider**: Una implementación (Decorator) que envuelve un proveedor real y añade caché en memoria o disco para evitar llamadas de red excesivas.

## Flujo de Uso
1.  La App inicializa FlagKit con una configuración específica (ej. usando un adaptador para Firebase).
2.  FlagKit configura el `FlagProvider` seleccionado.
3.  La App consulta a FlagKit:
    *   Síncrono (cache): `flagkit.isFeatureEnabled("new_checkout_flow")` (si ya está inicializado).
    *   Asíncrono: `flagkit.fetchAndActivate()` (para forzar actualización).
    *   Reactivo: `flagkit.observeFeature("new_checkout_flow").collect { isEnabled -> ... }`.
4.  El adaptador obtiene el valor real del proveedor (o caché).
5.  FlagKit devuelve el resultado a la App.

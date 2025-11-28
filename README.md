# FlagKit

FlagKit es una librería de Android diseñada para gestionar la activación y desactivación de funcionalidades (feature flags) de manera agnóstica al proveedor de flags subyacente. Su objetivo es abstraer la lógica de negocio de la implementación específica de servicios como Firebase Remote Config, GrowthBook, etc.

## Características Principales

*   **Agnóstico al Proveedor**: Funciona con cualquier servicio de feature flags o incluso con configuraciones locales.
*   **Arquitectura Clean**: Construida siguiendo principios SOLID y Clean Architecture.
*   **Reactivo**: Soporte nativo para observar cambios en los flags utilizando Kotlin Flows.
*   **Cache-First**: Estrategia de lectura rápida desde caché local, con actualizaciones en segundo plano.
*   **Simple**: API sencilla centrada en valores booleanos (Activado/Desactivado).

## Instalación

Agrega la dependencia a tu archivo `build.gradle.kts` del módulo de tu aplicación:

```kotlin
dependencies {
    implementation("es.joshluq.flagkit:flagkit:1.0.0") // Reemplaza con la versión actual
}
```

## Cómo Usar

### 1. Configuración Inicial (Application Class)

Lo ideal es inicializar FlagKit en tu clase `Application` o mediante tu sistema de inyección de dependencias (Hilt, Koin).

```kotlin
class MyApplication : Application() {

    lateinit var flagKitManager: FlagKitManager
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Crear el Caché (ej. en memoria)
        val cache = InMemoryFlagCache()

        // 2. Configurar el Proveedor (ej. MapBased para pruebas o tu implementación de Firebase)
        val provider = MapBasedFlagProvider(cache)
        
        // (Opcional) Configurar flags iniciales si usas MapBasedProvider
        provider.setRemoteFlags(mapOf("new_feature" to true))

        // 3. Construir la instancia de FlagKit
        flagKitManager = FlagKitBuilder()
            .withProvider(provider)
            .build()

        // 4. (Opcional) Forzar actualización de flags al inicio
        CoroutineScope(Dispatchers.IO).launch {
            flagKitManager.fetchAndActivate()
        }
    }
}
```

### 2. Consultar Flags

#### Síncrono (Recomendado para lógica imperativa)
```kotlin
if (flagKitManager.isFeatureEnabled("new_feature")) {
    // Mostrar la nueva funcionalidad
} else {
    // Mostrar la versión antigua
}
```

#### Reactivo (Recomendado para Jetpack Compose)
```kotlin
val isEnabled by flagKitManager.observeFeature("new_feature", defaultValue = false)
    .collectAsState(initial = false)

if (isEnabled) {
    NewFeatureUI()
}
```

## Creación de un Proveedor Personalizado

Para integrar servicios como Firebase Remote Config, debes crear una clase que extienda de `CachedFlagProvider` e implemente el método `fetchFlagsFromRemote`.

```kotlin
class FirebaseFlagProvider(
    cache: FlagCache
) : CachedFlagProvider(cache) {

    override suspend fun fetchFlagsFromRemote(): Map<String, Boolean> {
        // Lógica para obtener flags de Firebase y retornarlos como Map<String, Boolean>
        // Ejemplo pseudocódigo:
        // await firebase.fetchAndActivate()
        // return firebase.all.map { it.key to it.asBoolean() }
        return emptyMap() 
    }
}
```

## Licencia

[Tu Licencia Aquí]

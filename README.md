# 🚩 FlagKit

**FlagKit** es una librería de Android moderna, reactiva y agnóstica diseñada para simplificar la gestión de *Feature Flags* (funcionalidades activables/desactivables).

Su principal objetivo es **desacoplar** la lógica de negocio de tu aplicación de los SDKs de terceros (como Firebase Remote Config, GrowthBook, LaunchDarkly, etc.), proporcionando una capa de abstracción limpia, testear y fácil de usar.

---

## 🚀 Características Principales

*   **🔌 Agnóstico al Proveedor**: Define tu lógica una vez, cambia de proveedor (Firebase, local, custom) sin tocar tu código de negocio.
*   **🏛️ Clean Architecture**: Diseñada siguiendo principios SOLID, con capas claras (Presentación, Dominio, Data) y patrón Repository.
*   **⚡ Cache-First & Performance**: Lecturas síncronas inmediatas desde caché (memoria) para evitar bloqueos en la UI. Actualizaciones en segundo plano.
*   **🌊 Reactivo (Kotlin Flows)**: Observa cambios en los flags en tiempo real. Ideal para Jetpack Compose.
*   **🧪 Testeable**: Arquitectura basada en interfaces e inyección de dependencias, facilitando el mocking y los unit tests.
*   **✅ Solo Booleanos**: API simplificada y directa (`true`/`false`) para reducir la complejidad cognitiva.

---

## 🛠️ Instalación

Agrega la dependencia a tu archivo `build.gradle.kts` del módulo de tu aplicación:

```kotlin
dependencies {
    implementation("es.joshluq.flagkit:flagkit:1.0.0") // Reemplaza con la versión más reciente
}
```

---

## 📖 Cómo Usar

### 1. Configuración Inicial

Recomendamos configurar FlagKit en tu clase `Application` o mediante un módulo de Inyección de Dependencias (Hilt/Koin).

```kotlin
class MyApplication : Application() {

    lateinit var flagKitManager: FlagKitManager
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Elige tu estrategia de Caché (actualmente en memoria)
        val cache = InMemoryFlagCache()

        // 2. Elige tu Proveedor (ej. MapBased para desarrollo/tests, o tu implementación de Firebase)
        val provider = MapBasedFlagProvider(cache)
        
        // (Opcional) Configura flags iniciales para desarrollo
        provider.setRemoteFlags(mapOf(
            "new_checkout_flow" to true,
            "dark_mode_enabled" to false
        ))

        // 3. Construye la instancia de FlagKit
        flagKitManager = FlagKitBuilder()
            .withProvider(provider)
            .build()

        // 4. (Opcional) Fuerza una actualización inicial
        CoroutineScope(Dispatchers.IO).launch {
            flagKitManager.fetchAndActivate()
        }
    }
}
```

### 2. Consumiendo Flags

#### 🔹 Estilo Imperativo (Síncrono)
Ideal para lógica de negocio condicional o vistas clásicas.

```kotlin
if (flagKitManager.isFeatureEnabled("new_checkout_flow")) {
    showNewCheckout()
} else {
    showLegacyCheckout()
}
```

#### 🔹 Estilo Reactivo (Jetpack Compose)
Observa cambios en tiempo real. Si el flag cambia en el servidor, tu UI se actualiza automáticamente.

```kotlin
@Composable
fun MyScreen(flagKitManager: FlagKitManager) {
    // Se suscribe al Flow y convierte el valor a State de Compose
    val isNewCheckoutEnabled by flagKitManager
        .observeFeature("new_checkout_flow", defaultValue = false)
        .collectAsState(initial = false)

    if (isNewCheckoutEnabled) {
        NewCheckoutComponent()
    } else {
        LegacyCheckoutComponent()
    }
}
```

---

## 🧩 Creando un Proveedor Personalizado

Para integrar un servicio real (como Firebase Remote Config), simplemente implementa `CachedFlagProvider`.

```kotlin
class FirebaseFlagProvider(
    cache: FlagCache
) : CachedFlagProvider(cache) {

    private val remoteConfig = Firebase.remoteConfig

    init {
        // Configura tu fetch interval, defaults, etc.
    }

    override suspend fun fetchFlagsFromRemote(): Map<String, Boolean> {
        // 1. Forzamos el fetch del SDK nativo
        remoteConfig.fetchAndActivate().await() 
        
        // 2. Convertimos todos los valores a un Map<String, Boolean>
        return remoteConfig.all.mapValues { entry ->
            entry.value.asBoolean()
        }
    }
}
```

---

## 🏗️ Arquitectura y Desarrollo

Este proyecto sigue una arquitectura modular y limpia:

*   **`es.joshluq.flagkit.domain`**: Contiene los **Casos de Uso** (`GetFlagStatus`, `ObserveFlagStatus`) y la interfaz del **Repositorio**. Es código Kotlin puro.
*   **`es.joshluq.flagkit.data`**: Implementación del **Repositorio**, manejo de **Caché** (`InMemoryFlagCache`) y **Proveedores** (`CachedFlagProvider`).
*   **`es.joshluq.flagkit` (Root)**: Capa de presentación/API (`FlagKitManager`, `FlagKitBuilder`).

### CI/CD & Calidad
*   **GitHub Actions**: Pipelines automatizados para PRs y Releases.
*   **Fastlane**: Orquestación de tareas de testeo y publicación.
*   **SonarQube**: Análisis de calidad de código estático.
*   **Kover**: Reportes de cobertura de código (integrado con SonarQube).

---

## 📄 Licencia

```text
Copyright 2024 Joshua Luque

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

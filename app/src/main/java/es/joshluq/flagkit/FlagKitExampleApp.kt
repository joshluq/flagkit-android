package es.joshluq.flagkit

import android.app.Application
import es.joshluq.flagkit.data.cache.InMemoryFlagCache
import es.joshluq.flagkit.data.provider.MapBasedFlagProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlagKitExampleApp : Application() {

    lateinit var flagKitManager: FlagKitManager
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Configurar el Cache (Memoria)
        val cache = InMemoryFlagCache()

        // 2. Configurar el Proveedor (MapBased para el ejemplo, podría ser Firebase)
        val provider = MapBasedFlagProvider(cache)
        
        // Simulamos flags remotos para el ejemplo
        provider.setRemoteFlags(
            mapOf(
                "show_greeting" to true,
                "new_ui_enabled" to false
            )
        )

        // 3. Construir FlagKit
        flagKitManager = FlagKitBuilder()
            .withProvider(provider)
            .build()

        // Opcional: Hacer un fetch inicial
        CoroutineScope(Dispatchers.IO).launch {
            flagKitManager.fetchAndActivate()
        }
    }
}

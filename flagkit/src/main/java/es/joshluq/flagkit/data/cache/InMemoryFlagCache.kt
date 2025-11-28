package es.joshluq.flagkit.data.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

/**
 * A simple in-memory implementation of [FlagCache].
 * Uses a thread-safe map for storage and a StateFlow for reactivity.
 */
class InMemoryFlagCache : FlagCache {

    private val cache = ConcurrentHashMap<String, Boolean>()
    private val cacheUpdates = MutableStateFlow<Long>(0) // Signal for updates

    override fun get(key: String, defaultValue: Boolean): Boolean {
        return cache[key] ?: defaultValue
    }

    override fun put(key: String, value: Boolean) {
        cache[key] = value
        notifyUpdate()
    }

    override fun putAll(flags: Map<String, Boolean>) {
        cache.putAll(flags)
        notifyUpdate()
    }

    override fun observe(key: String, defaultValue: Boolean): Flow<Boolean> {
        return cacheUpdates.map { 
            get(key, defaultValue)
        }
    }

    override fun clear() {
        cache.clear()
        notifyUpdate()
    }
    
    private fun notifyUpdate() {
        cacheUpdates.value = System.currentTimeMillis()
    }
}

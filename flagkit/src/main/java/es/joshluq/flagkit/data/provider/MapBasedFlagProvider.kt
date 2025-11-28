package es.joshluq.flagkit.data.provider

import es.joshluq.flagkit.data.cache.FlagCache
import kotlinx.coroutines.delay

/**
 * A concrete implementation of [CachedFlagProvider] backed by a static Map.
 * Useful for testing, debug builds, or offline configurations.
 */
class MapBasedFlagProvider(
    cache: FlagCache,
    private var remoteFlags: Map<String, Boolean> = emptyMap()
) : CachedFlagProvider(cache) {

    /**
     * Updates the "remote" flags source.
     * In a real provider (e.g. Firebase), this would happen internally via SDK callbacks.
     */
    fun setRemoteFlags(flags: Map<String, Boolean>) {
        this.remoteFlags = flags
    }

    /**
     * Simulates fetching flags from a remote source.
     */
    override suspend fun fetchFlagsFromRemote(): Map<String, Boolean> {
        // Simulate network delay
        delay(100) 
        return remoteFlags
    }
}

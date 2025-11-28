package es.joshluq.flagkit.data.provider

import es.joshluq.flagkit.data.cache.FlagCache
import es.joshluq.flagkit.domain.FlagKitProvider
import kotlinx.coroutines.flow.Flow

/**
 * Abstract implementation of [FlagKitProvider] that uses a [FlagCache].
 *
 * Strategy:
 * - Reads (get/observe) always come from the cache (fast, synchronous).
 * - Updates (fetchAndActivate) fetch from the remote source and update the cache.
 */
abstract class CachedFlagProvider(
    private val cache: FlagCache
) : FlagKitProvider {

    override fun configure(configuration: Any?) {
        // Optional hook for subclasses
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return cache.get(key, defaultValue)
    }

    override fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> {
        return cache.observe(key, defaultValue)
    }

    override suspend fun fetchAndActivate() {
        val newFlags = fetchFlagsFromRemote()
        cache.putAll(newFlags)
    }

    /**
     * Abstract method to be implemented by specific providers (Firebase, GrowthBook, etc.).
     * Must return a map of all current flag values.
     */
    protected abstract suspend fun fetchFlagsFromRemote(): Map<String, Boolean>
}

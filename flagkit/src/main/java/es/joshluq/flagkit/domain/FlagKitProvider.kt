package es.joshluq.flagkit.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface that defines the contract for feature flag providers in the Domain layer.
 * It ensures the application is agnostic to the underlying implementation (Firebase, GrowthBook, etc.).
 */
interface FlagKitProvider {

    /**
     * Configures the provider with a specific client or settings.
     * This allows initializing the underlying SDK (e.g. passing the FirebaseRemoteConfig instance or API keys).
     *
     * @param configuration A generic configuration object (can be a Map, a Context, or a specific SDK client instance).
     */
    fun configure(configuration: Any? = null)

    /**
     * Retrieves the boolean value of a feature flag.
     *
     * @param key The unique key of the flag.
     * @param defaultValue The value to return if the flag is missing or retrieval fails.
     * @return The boolean value of the flag.
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Observes the value of a feature flag reactively.
     * Emits a new value whenever the flag changes in the source.
     *
     * @param key The unique key of the flag.
     * @param defaultValue The value to use if the flag is missing.
     * @return A Flow emitting the flag values.
     */
    fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean>

    /**
     * Forces a fetch of the latest flags from the remote source and activates them.
     * This operation is asynchronous.
     */
    suspend fun fetchAndActivate()
}

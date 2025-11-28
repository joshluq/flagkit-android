package es.joshluq.flagkit

import es.joshluq.flagkit.domain.usecase.FetchFlagsUseCase
import es.joshluq.flagkit.domain.usecase.GetFlagStatusUseCase
import es.joshluq.flagkit.domain.usecase.ObserveFlagStatusUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Manager class for FlagKit.
 * This class acts as a facade for the application to interact with feature flags.
 * It delegates operations to the domain Use Cases.
 */
class FlagKitManager(
    private val getFlagStatusUseCase: GetFlagStatusUseCase,
    private val observeFlagStatusUseCase: ObserveFlagStatusUseCase,
    private val fetchFlagsUseCase: FetchFlagsUseCase
) {

    /**
     * Checks if a feature is enabled synchronously (typically from cache).
     *
     * @param key The key of the feature flag.
     * @param defaultValue The default value to return if the flag is not found.
     * @return True if the feature is enabled, false otherwise.
     */
    fun isFeatureEnabled(key: String, defaultValue: Boolean = false): Boolean {
        return getFlagStatusUseCase(key, defaultValue)
    }

    /**
     * Observes the state of a feature flag reactively.
     *
     * @param key The key of the feature flag.
     * @param defaultValue The default value.
     * @return A Flow emitting the current and future values of the flag.
     */
    fun observeFeature(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        return observeFlagStatusUseCase(key, defaultValue)
    }

    /**
     * Forces a fetch of the latest flags from the remote source.
     */
    suspend fun fetchAndActivate() {
        fetchFlagsUseCase()
    }
}

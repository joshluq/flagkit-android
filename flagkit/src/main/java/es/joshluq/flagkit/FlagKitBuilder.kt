package es.joshluq.flagkit

import es.joshluq.flagkit.data.repository.FlagRepositoryImpl
import es.joshluq.flagkit.domain.FlagKitProvider
import es.joshluq.flagkit.domain.usecase.FetchFlagsUseCase
import es.joshluq.flagkit.domain.usecase.GetFlagStatusUseCase
import es.joshluq.flagkit.domain.usecase.ObserveFlagStatusUseCase

/**
 * Builder for creating instances of [FlagKitManager].
 * Simplifies the configuration and dependency injection of the library components.
 */
class FlagKitBuilder {

    private var provider: FlagKitProvider? = null

    /**
     * Sets the specific flag provider (e.g., Firebase, MapBased, etc.).
     * This is mandatory.
     */
    fun withProvider(provider: FlagKitProvider): FlagKitBuilder {
        this.provider = provider
        return this
    }

    /**
     * Builds the [FlagKitManager] instance.
     * @throws IllegalStateException if no provider is set.
     */
    fun build(): FlagKitManager {
        val finalProvider = provider ?: throw IllegalStateException("FlagKitProvider must be set using withProvider()")
        
        // Create Repository
        val repository = FlagRepositoryImpl(finalProvider)

        // Create Use Cases
        val getFlagStatusUseCase = GetFlagStatusUseCase(repository)
        val observeFlagStatusUseCase = ObserveFlagStatusUseCase(repository)
        val fetchFlagsUseCase = FetchFlagsUseCase(repository)

        // Create Manager
        return FlagKitManager(
            getFlagStatusUseCase = getFlagStatusUseCase,
            observeFlagStatusUseCase = observeFlagStatusUseCase,
            fetchFlagsUseCase = fetchFlagsUseCase
        )
    }
}

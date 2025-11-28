package es.joshluq.flagkit.data.repository

import es.joshluq.flagkit.domain.FlagKitProvider
import es.joshluq.flagkit.domain.repository.FlagRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [FlagRepository].
 * It delegates the operations to the [FlagKitProvider] (which handles caching and remote fetching).
 */
class FlagRepositoryImpl(
    private val provider: FlagKitProvider
) : FlagRepository {

    override fun getFlagValue(key: String, defaultValue: Boolean): Boolean {
        return provider.getBoolean(key, defaultValue)
    }

    override fun observeFlagValue(key: String, defaultValue: Boolean): Flow<Boolean> {
        return provider.observeBoolean(key, defaultValue)
    }

    override suspend fun refreshFlags() {
        provider.fetchAndActivate()
    }
}

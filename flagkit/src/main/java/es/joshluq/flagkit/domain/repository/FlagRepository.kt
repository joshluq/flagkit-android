package es.joshluq.flagkit.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the repository for accessing feature flags.
 * This acts as the single source of truth for the Domain layer.
 */
interface FlagRepository {

    /**
     * Gets the current boolean value of a flag.
     */
    fun getFlagValue(key: String, defaultValue: Boolean): Boolean

    /**
     * Observes the changes of a flag value.
     */
    fun observeFlagValue(key: String, defaultValue: Boolean): Flow<Boolean>

    /**
     * Refreshes the flags from the remote source.
     */
    suspend fun refreshFlags()
}

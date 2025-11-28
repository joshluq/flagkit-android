package es.joshluq.flagkit.domain.usecase

import es.joshluq.flagkit.domain.repository.FlagRepository

/**
 * Use case to retrieve the current synchronous status of a feature flag.
 */
class GetFlagStatusUseCase(private val repository: FlagRepository) {

    operator fun invoke(key: String, defaultValue: Boolean = false): Boolean {
        return repository.getFlagValue(key, defaultValue)
    }
}

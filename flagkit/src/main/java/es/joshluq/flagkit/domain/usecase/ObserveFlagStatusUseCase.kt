package es.joshluq.flagkit.domain.usecase

import es.joshluq.flagkit.domain.repository.FlagRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe the status of a feature flag reactively.
 */
class ObserveFlagStatusUseCase(private val repository: FlagRepository) {

    operator fun invoke(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        return repository.observeFlagValue(key, defaultValue)
    }
}

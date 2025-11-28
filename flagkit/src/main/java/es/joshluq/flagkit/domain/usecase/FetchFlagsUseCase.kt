package es.joshluq.flagkit.domain.usecase

import es.joshluq.flagkit.domain.repository.FlagRepository

/**
 * Use case to force fetch flags from the remote source.
 */
class FetchFlagsUseCase(private val repository: FlagRepository) {

    suspend operator fun invoke() {
        repository.refreshFlags()
    }
}

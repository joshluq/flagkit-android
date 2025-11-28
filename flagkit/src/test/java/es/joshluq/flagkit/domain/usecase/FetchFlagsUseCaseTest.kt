package es.joshluq.flagkit.domain.usecase

import es.joshluq.flagkit.domain.repository.FlagRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FetchFlagsUseCaseTest {

    private val mockRepository: FlagRepository = mockk(relaxed = true)
    private val useCase = FetchFlagsUseCase(mockRepository)

    @Test
    fun `invoke delegates to repository`() = runTest {
        useCase()

        coVerify { mockRepository.refreshFlags() }
    }
}

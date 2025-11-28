package es.joshluq.flagkit.domain.usecase

import es.joshluq.flagkit.domain.repository.FlagRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveFlagStatusUseCaseTest {

    private val mockRepository: FlagRepository = mockk()
    private val useCase = ObserveFlagStatusUseCase(mockRepository)

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val key = "feature"
        every { mockRepository.observeFlagValue(key, false) } returns flowOf(true)

        val result = useCase(key, false).first()

        assertTrue(result)
    }
}

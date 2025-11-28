package es.joshluq.flagkit.domain.usecase

import es.joshluq.flagkit.domain.repository.FlagRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Test

class GetFlagStatusUseCaseTest {

    private val mockRepository: FlagRepository = mockk()
    private val useCase = GetFlagStatusUseCase(mockRepository)

    @Test
    fun `invoke delegates to repository`() {
        val key = "feature"
        every { mockRepository.getFlagValue(key, false) } returns true

        val result = useCase(key, false)

        assertTrue(result)
        verify { mockRepository.getFlagValue(key, false) }
    }
}

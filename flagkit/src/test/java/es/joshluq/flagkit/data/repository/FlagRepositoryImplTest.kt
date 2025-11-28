package es.joshluq.flagkit.data.repository

import es.joshluq.flagkit.domain.FlagKitProvider
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class FlagRepositoryImplTest {

    private val mockProvider: FlagKitProvider = mockk(relaxed = true)
    private val repository = FlagRepositoryImpl(mockProvider)

    @Test
    fun `getFlagValue delegates to provider`() {
        val key = "feature"
        every { mockProvider.getBoolean(key, false) } returns true

        val result = repository.getFlagValue(key, false)

        assertTrue(result)
        verify { mockProvider.getBoolean(key, false) }
    }

    @Test
    fun `observeFlagValue delegates to provider`() = runTest {
        val key = "feature"
        every { mockProvider.observeBoolean(key, false) } returns flowOf(true)

        val result = repository.observeFlagValue(key, false).first()

        assertTrue(result)
    }

    @Test
    fun `refreshFlags delegates to provider`() = runTest {
        repository.refreshFlags()

        coVerify { mockProvider.fetchAndActivate() }
    }
}

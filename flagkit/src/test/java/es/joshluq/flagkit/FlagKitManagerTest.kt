package es.joshluq.flagkit

import es.joshluq.flagkit.domain.usecase.FetchFlagsUseCase
import es.joshluq.flagkit.domain.usecase.GetFlagStatusUseCase
import es.joshluq.flagkit.domain.usecase.ObserveFlagStatusUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlagKitManagerTest {

    private val getFlagStatusUseCase: GetFlagStatusUseCase = mockk()
    private val observeFlagStatusUseCase: ObserveFlagStatusUseCase = mockk()
    private val fetchFlagsUseCase: FetchFlagsUseCase = mockk()
    
    private lateinit var flagKitManager: FlagKitManager

    @Before
    fun setUp() {
        flagKitManager = FlagKitManager(
            getFlagStatusUseCase,
            observeFlagStatusUseCase,
            fetchFlagsUseCase
        )
    }

    @Test
    fun `isFeatureEnabled delegates to use case`() {
        val key = "feature"
        every { getFlagStatusUseCase(key, false) } returns true

        val result = flagKitManager.isFeatureEnabled(key, false)

        assertTrue(result)
        verify { getFlagStatusUseCase(key, false) }
    }

    @Test
    fun `observeFeature delegates to use case`() = runTest {
        val key = "feature"
        every { observeFlagStatusUseCase(key, false) } returns flowOf(true)

        val result = flagKitManager.observeFeature(key, false).first()

        assertTrue(result)
    }

    @Test
    fun `fetchAndActivate delegates to use case`() = runTest {
        io.mockk.coEvery { fetchFlagsUseCase() } returns Unit

        flagKitManager.fetchAndActivate()

        coVerify { fetchFlagsUseCase() }
    }
}

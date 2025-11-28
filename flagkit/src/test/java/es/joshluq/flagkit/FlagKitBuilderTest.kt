package es.joshluq.flagkit

import es.joshluq.flagkit.domain.FlagKitProvider
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Test

class FlagKitBuilderTest {

    @Test
    fun `build returns FlagKitManager when provider is set`() {
        val mockProvider: FlagKitProvider = mockk(relaxed = true)
        
        val manager = FlagKitBuilder()
            .withProvider(mockProvider)
            .build()

        assertNotNull(manager)
    }

    @Test(expected = IllegalStateException::class)
    fun `build throws exception when provider is missing`() {
        FlagKitBuilder().build()
    }
}

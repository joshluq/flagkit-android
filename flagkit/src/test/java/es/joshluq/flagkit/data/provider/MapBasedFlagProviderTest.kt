package es.joshluq.flagkit.data.provider

import es.joshluq.flagkit.data.cache.FlagCache
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MapBasedFlagProviderTest {

    private val mockCache: FlagCache = mockk(relaxed = true)
    private val provider = MapBasedFlagProvider(mockCache)

    @Test
    fun `fetchFlagsFromRemote returns empty map by default`() = runTest {
        provider.fetchAndActivate()
        // Since fetchAndActivate calls cache.putAll with the result of fetchFlagsFromRemote,
        // and default is empty, we expect cache.putAll(emptyMap())
        verify { mockCache.putAll(emptyMap()) }
    }

    @Test
    fun `fetchFlagsFromRemote returns configured flags`() = runTest {
        val flags = mapOf("feature1" to true, "feature2" to false)
        provider.setRemoteFlags(flags)

        provider.fetchAndActivate()

        verify { mockCache.putAll(flags) }
    }

    @Test
    fun `setRemoteFlags updates the source for next fetch`() = runTest {
        // Initial fetch
        val initialFlags = mapOf("f1" to true)
        provider.setRemoteFlags(initialFlags)
        provider.fetchAndActivate()
        verify { mockCache.putAll(initialFlags) }

        // Update source
        val newFlags = mapOf("f1" to false, "f2" to true)
        provider.setRemoteFlags(newFlags)
        provider.fetchAndActivate()
        verify { mockCache.putAll(newFlags) }
    }
}

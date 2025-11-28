package es.joshluq.flagkit.data.provider

import es.joshluq.flagkit.data.cache.FlagCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class CachedFlagProviderTest {

    private val mockCache: FlagCache = mockk(relaxed = true)
    
    // Create a concrete implementation of the abstract class for testing
    private val provider = object : CachedFlagProvider(mockCache) {
        var fetchResult: Map<String, Boolean> = emptyMap()
        
        override suspend fun fetchFlagsFromRemote(): Map<String, Boolean> {
            return fetchResult
        }
    }

    @Test
    fun `getBoolean delegates to cache`() {
        every { mockCache.get("key", false) } returns true
        
        val result = provider.getBoolean("key", false)
        
        assertTrue(result)
        verify { mockCache.get("key", false) }
    }
    
    @Test
    fun `observeBoolean delegates to cache`() = runTest {
        every { mockCache.observe("key", false) } returns flowOf(true)
        
        val result = provider.observeBoolean("key", false).first()
        
        assertTrue(result)
    }

    @Test
    fun `fetchAndActivate fetches from remote and updates cache`() = runTest {
        val newFlags = mapOf("key1" to true, "key2" to false)
        provider.fetchResult = newFlags
        
        provider.fetchAndActivate()
        
        verify { mockCache.putAll(newFlags) }
    }
}

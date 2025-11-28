package es.joshluq.flagkit.data.cache

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class InMemoryFlagCacheTest {

    private lateinit var cache: InMemoryFlagCache

    @Before
    fun setUp() {
        cache = InMemoryFlagCache()
    }

    @Test
    fun `get returns value if exists`() {
        cache.put("key1", true)
        assertEquals(true, cache.get("key1", false))
    }

    @Test
    fun `get returns default if not exists`() {
        assertEquals(false, cache.get("key1", false))
    }

    @Test
    fun `putAll adds multiple values`() {
        val flags = mapOf("key1" to true, "key2" to false)
        cache.putAll(flags)

        assertEquals(true, cache.get("key1", false))
        assertEquals(false, cache.get("key2", true))
    }

    @Test
    fun `clear removes all values`() {
        cache.put("key1", true)
        cache.clear()
        assertFalse(cache.get("key1", false))
    }

    @Test
    fun `observe emits initial value`() = runTest {
        cache.put("key1", true)
        val value = cache.observe("key1", false).first()
        assertEquals(true, value)
    }

    @Test
    fun `observe emits default value if key missing`() = runTest {
        val value = cache.observe("missing_key", false).first()
        assertEquals(false, value)
    }
}

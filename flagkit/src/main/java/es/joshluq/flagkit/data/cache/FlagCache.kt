package es.joshluq.flagkit.data.cache

import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for a local cache mechanism.
 * It supports storing and retrieving flag values and observing changes.
 */
interface FlagCache {
    
    fun get(key: String, defaultValue: Boolean): Boolean
    
    fun put(key: String, value: Boolean)
    
    fun putAll(flags: Map<String, Boolean>)
    
    fun observe(key: String, defaultValue: Boolean): Flow<Boolean>
    
    fun clear()
}

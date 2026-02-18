package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.model.PlaybackItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueManager @Inject constructor() {

    private val _queue = MutableStateFlow<List<PlaybackItem>>(emptyList())
    val queue: StateFlow<List<PlaybackItem>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun setQueue(items: List<PlaybackItem>, startIndex: Int = 0) {
        _queue.value = items
        _currentIndex.value = if (items.isNotEmpty()) startIndex.coerceIn(0, items.lastIndex) else -1
    }

    fun current(): PlaybackItem? {
        val index = _currentIndex.value
        val items = _queue.value
        return if (index in items.indices) items[index] else null
    }

    fun next(): PlaybackItem? {
        val items = _queue.value
        val nextIndex = _currentIndex.value + 1
        return if (nextIndex in items.indices) {
            _currentIndex.value = nextIndex
            items[nextIndex]
        } else {
            null
        }
    }

    fun previous(): PlaybackItem? {
        val items = _queue.value
        val prevIndex = _currentIndex.value - 1
        return if (prevIndex in items.indices) {
            _currentIndex.value = prevIndex
            items[prevIndex]
        } else {
            null
        }
    }

    fun hasNext(): Boolean = _currentIndex.value + 1 in _queue.value.indices

    fun hasPrevious(): Boolean = _currentIndex.value - 1 in _queue.value.indices

    fun clear() {
        _queue.value = emptyList()
        _currentIndex.value = -1
    }

    fun size(): Int = _queue.value.size
}

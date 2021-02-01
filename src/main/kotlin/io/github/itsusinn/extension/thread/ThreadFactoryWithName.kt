package io.github.itsusinn.extension.thread

import java.lang.Runnable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Modified from https://my.oschina.net/u/4477286/blog/4274564
 * @param name human-readable
 */
internal class ThreadFactoryWithName(
    name: String,
    private val asDaemon: Boolean = true
) : ThreadFactory {

    companion object {
        private val exist = ConcurrentHashMap<String, AtomicInteger>()
    }

    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String = kotlin.run {
        val counter = exist.getOrPut(name) { AtomicInteger(1) }
        val num = counter.getAndIncrement()
        return@run if (num == 1) {
            "$name-thread"
        } else {
            "$name-$num-thread"
        }
    }
    private val group: ThreadGroup = kotlin.run {
        val s = System.getSecurityManager()
        if (s != null) s.threadGroup else Thread.currentThread().threadGroup
    }

    override fun newThread(r: Runnable): Thread {
        val t = kotlin.run {
            val num = threadNumber.getAndIncrement()
            if (num == 1) {
                return@run Thread(group, r, namePrefix, 0)
            } else {
                return@run Thread(group, r, "$namePrefix-$num", 0)
            }
        }
        if (t.isDaemon != asDaemon) t.isDaemon = asDaemon
        if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
        return t
    }
}

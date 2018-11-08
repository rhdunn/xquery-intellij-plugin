/*
 * Copyright (C) 2018 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.core.async

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import java.util.concurrent.*
import kotlin.reflect.KProperty

interface FutureException<T> : Future<T> {
    fun onException(f: (Throwable) -> Unit)
}

interface ExecutableOnPooledThread<T> {
    fun execute(): FutureException<T>

    fun execute(later: (T) -> Unit): FutureException<T>

    fun execute(modalityState: ModalityState, later: (T) -> Unit): FutureException<T>

    fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U>
}

operator fun <T> ExecutableOnPooledThread<T>.getValue(ref: Any?, property: KProperty<*>): ExecutableOnPooledThread<T> {
    return this
}

fun <T> pooled_thread(f: () -> T): ExecutableOnPooledThread<T> {
    return ExecuteOnPooledThread(f)
}

fun <T> local_thread(f: () -> T): ExecutableOnPooledThread<T> {
    return ExecuteOnLocalThread(f)
}

fun <T> forwarded(f: () -> ExecutableOnPooledThread<T>): ExecutableOnPooledThread<T> {
    return f()
}

fun <T> cached(f: () -> ExecutableOnPooledThread<T>): ExecutableOnPooledThread<T> {
    return CachedExecutableOnLocalThread(f())
}

class FutureExceptionTask<T>(callable: Callable<T>) : FutureTask<T>(callable), FutureException<T> {
    private var onerror: ((Throwable) -> Unit)? = null
    private var onerrorCalled: Boolean = false

    override fun onException(f: (Throwable) -> Unit) {
        onerror = f
    }

    override fun run() {
        super.run()
        // Ensure the onException callback gets invoked on errors, as
        // ApplicationManager.executeOnPooledThread may not call get.
        if (isDone) get()
    }

    override fun get(): T? {
        return try {
            super.get()
        } catch (e: ExecutionException) {
            if (onerror == null)
                throw e
            else if (!onerrorCalled) {
                onerrorCalled = true
                onerror!!.invoke(e.cause!!)
            }
            return null
        }
    }

    override fun get(timeout: Long, unit: TimeUnit?): T? {
        return try {
            super.get(timeout, unit)
        } catch (e: ExecutionException) {
            if (onerror == null)
                throw e
            else if (!onerrorCalled) {
                onerrorCalled = true
                onerror!!.invoke(e.cause!!)
            }
            return null
        }
    }
}

// region pooled thread

private class ExecuteOnPooledThread<T>(val f: () -> T) : ExecutableOnPooledThread<T> {
    override fun execute(): FutureException<T> {
        val task = FutureExceptionTask<T>(Callable(f))
        ApplicationManager.getApplication().executeOnPooledThread(task)
        return task
    }

    override fun execute(later: (T) -> Unit): FutureException<T> {
        return execute(ModalityState.defaultModalityState(), later)
    }

    override fun execute(modalityState: ModalityState, later: (T) -> Unit): FutureException<T> {
        val task = FutureExceptionTask<T>(Callable {
            val ret = f()
            ApplicationManager.getApplication().invokeLater({ later(ret) }, modalityState)
            ret
        })
        ApplicationManager.getApplication().executeOnPooledThread(task)
        return task
    }

    override fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U> = ExecuteOnPooledThread { g(f()) }
}

// endregion
// region local thread

private class LocalFuture<T>(val value: T?, val error: Throwable?) : FutureException<T> {
    var onerror: ((Throwable) -> Unit)? = null

    override fun onException(f: (Throwable) -> Unit) {
        onerror = f
    }

    override fun isDone(): Boolean = true

    override fun get(): T? {
        if (error == null)
            return value
        if (onerror == null)
            throw ExecutionException(error)
        onerror!!.invoke(error)
        return null
    }

    override fun get(timeout: Long, unit: TimeUnit?): T? = get()

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false

    override fun isCancelled(): Boolean = false
}

private class ExecuteOnLocalThread<T>(val f: () -> T) : ExecutableOnPooledThread<T> {
    override fun execute(): FutureException<T> {
        return try {
            LocalFuture(f(), null)
        } catch (e: Exception) {
            LocalFuture(null, e)
        }
    }

    override fun execute(later: (T) -> Unit): FutureException<T> {
        return execute(ModalityState.defaultModalityState(), later)
    }

    override fun execute(modalityState: ModalityState, later: (T) -> Unit): FutureException<T> {
        return try {
            val ret = f()
            later(ret)
            LocalFuture(ret, null)
        } catch (e: Exception) {
            LocalFuture(null, e)
        }
    }

    override fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U> = ExecuteOnLocalThread { g(f()) }
}

// endregion
// region caching

private class CachedExecutableOnLocalThread<T>(val e: ExecutableOnPooledThread<T>) : ExecutableOnPooledThread<T> {
    private var cached: FutureException<T>? = null

    override fun execute(): FutureException<T> {
        if (cached == null) {
            cached = e.execute()
        }
        return cached!!
    }

    override fun execute(later: (T) -> Unit): FutureException<T> {
        return execute(ModalityState.defaultModalityState(), later)
    }

    override fun execute(modalityState: ModalityState, later: (T) -> Unit): FutureException<T> {
        if (cached == null) {
            cached = e.execute(later)
        } else if (cached!!.get() != null) {
            later(cached!!.get())
        }
        return cached!!
    }

    override fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U> = TODO()
}

// endregion

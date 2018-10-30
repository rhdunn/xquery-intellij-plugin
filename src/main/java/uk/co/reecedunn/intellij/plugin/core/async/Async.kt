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
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

interface ExecutableOnPooledThread<T> {
    fun execute(): Future<T>

    fun execute(later: (T) -> Unit): Future<T>

    fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U>
}

// region ExecuteOnPooledThread

class ExecuteOnPooledThread<T>(val f: () -> T) : ExecutableOnPooledThread<T> {
    override fun execute(): Future<T> = ApplicationManager.getApplication().executeOnPooledThread(f)

    override fun execute(later: (T) -> Unit): Future<T> = ApplicationManager.getApplication().executeOnPooledThread<T> {
        val ret = f()
        ApplicationManager.getApplication().invokeLater { later(ret) }
        ret
    }

    override fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U> = ExecuteOnPooledThread { g(f()) }
}

// endregion
// region ExecuteOnLocalThread

private class LocalFuture<T>(val f: () -> T) : Future<T> {
    override fun isDone(): Boolean = true

    override fun get(): T = f()

    override fun get(timeout: Long, unit: TimeUnit?): T = get()

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false

    override fun isCancelled(): Boolean = false
}

class ExecuteOnLocalThread<T>(val f: () -> T) : ExecutableOnPooledThread<T> {
    override fun execute(): Future<T> = LocalFuture(f)

    override fun execute(later: (T) -> Unit): Future<T> = LocalFuture {
        val ret = f()
        later(ret)
        ret
    }

    override fun <U> then(g: (T) -> U): ExecutableOnPooledThread<U> = ExecuteOnLocalThread { g(f()) }
}

// endregion

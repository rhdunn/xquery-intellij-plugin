/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.event

import com.intellij.openapi.application.ex.ApplicationManagerEx
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread

abstract class Stopwatch {
    var startTime: Long = 0
        private set

    var endTime: Long = 0
        private set

    val elapsedTime: Long get() = endTime - startTime

    fun start(interval: Long) = timer(interval).execute()

    private fun timer(interval: Long): ExecutableOnPooledThread<Unit> = pooled_thread {
        startTime = System.nanoTime()
        while (isRunning()) {
            ApplicationManagerEx.getApplication().invokeLater {
                if (isRunning()) {
                    endTime = System.nanoTime()
                    onInterval()
                }
            }
            Thread.sleep(interval)
        }
    }

    abstract fun isRunning(): Boolean

    abstract fun onInterval()
}

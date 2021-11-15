/*
 * Copyright (C) 2019, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.progress

import com.intellij.openapi.progress.ProgressManager

fun <T> Sequence<T>.forEachCancellable(checkEvery: Int, action: (T) -> Unit) {
    var seldomCounter = 0
    for (element in this) {
        seldomCounter++
        action(element)
        if (seldomCounter % checkEvery == 0) {
            ProgressManager.checkCanceled()
        }
    }
}

fun <T> Sequence<T>.forEachCancellable(action: (T) -> Unit): Unit = forEachCancellable(1000, action)

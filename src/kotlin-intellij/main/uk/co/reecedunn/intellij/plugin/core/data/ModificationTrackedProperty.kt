/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.data

import com.intellij.openapi.util.SimpleModificationTracker

class ModificationTrackedProperty<Tracker : SimpleModificationTracker, out T>(private val compute: (Tracker) -> T?) {
    private var modificationCount: Long = -1
    private var cachedValue: T? = null

    fun invalidate() {
        modificationCount = -1
    }

    @Synchronized
    fun get(tracker: Tracker): T? {
        if (modificationCount != tracker.modificationCount) {
            modificationCount = tracker.modificationCount
            cachedValue = compute(tracker)
        }
        return cachedValue
    }
}

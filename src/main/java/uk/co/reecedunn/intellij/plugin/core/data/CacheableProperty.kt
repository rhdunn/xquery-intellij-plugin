/*
 * Copyright (C) 2017 Reece H. Dunn
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

enum class CachingBehaviour {
    /**
     * The computed property value can be cached.
     */
    Cache,
    /**
     * The computed property value cannot be cached.
     */
    DoNotCache,
    /**
     * The value has not been calculated yet, so its cacheability cannot be determined.
     */
    Undecided
}

val Cacheable = CachingBehaviour.Cache
val NotCacheable = CachingBehaviour.DoNotCache

infix fun <T> T?.`is`(cacheable: CachingBehaviour): Pair<T?, CachingBehaviour> {
    return Pair(this, cacheable)
}

class CacheableProperty<out T>(private val compute: () -> Pair<T?, CachingBehaviour>) {
    private var cachedValue: Pair<T?, CachingBehaviour> = Pair(null, CachingBehaviour.Undecided)

    val cachingBehaviour: CachingBehaviour = cachedValue.second

    fun invalidate() {
        cachedValue = Pair(null, CachingBehaviour.Undecided)
    }

    fun get(): T? {
        if (cachedValue.second != CachingBehaviour.Cache) {
            cachedValue = compute()
        }
        return cachedValue.first
    }
}

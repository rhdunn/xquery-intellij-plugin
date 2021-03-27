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
package uk.co.reecedunn.intellij.plugin.core.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key

open class ASTWrapperPsiElement(node: ASTNode) : com.intellij.extapi.psi.ASTWrapperPsiElement(node) {
    fun <T> clearUserData(key: Key<T>): Boolean {
        val map = userMap
        val newMap = map.minus(key)
        return map === newMap || changeUserMap(map, map.minus(key))
    }

    fun <T> computeUserDataIfAbsent(key: Key<T>, provider: () -> T): T {
        while (true) {
            val map = userMap
            val oldValue = map[key]
            if (oldValue != null) {
                return oldValue
            }
            val value = provider()!!
            val newMap = map.plus(key, value)
            if (newMap === map || changeUserMap(map, newMap)) {
                return value
            }
        }
    }
}

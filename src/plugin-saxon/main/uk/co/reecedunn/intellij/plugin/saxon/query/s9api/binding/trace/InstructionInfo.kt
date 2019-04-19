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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.type.Type

open class InstructionInfo(val saxonObject: Any, private val `class`: Class<*>) {
    fun getSystemId(): String? {
        return `class`.getMethod("getSystemId").invoke(saxonObject) as String?
    }

    fun getLineNumber(): Int {
        return `class`.getMethod("getLineNumber").invoke(saxonObject) as Int
    }

    fun getColumnNumber(): Int {
        return `class`.getMethod("getColumnNumber").invoke(saxonObject) as Int
    }

    override fun toString(): String {
        return saxonObject.toString()
    }

    override fun hashCode(): Int {
        return saxonObject.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is InstructionInfo) return false
        return saxonObject == other.saxonObject
    }
}

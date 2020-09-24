/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Location
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.QName
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om.StructuredQName

open class InstructionInfo(val saxonObject: Any, private val `class`: Class<*>) : Location {
    private val locationClass: Class<*> by lazy {
        if (`class`.getMethodOrNull("getLocation") == null)
            `class` // Saxon 9
        else
            `class`.classLoader.loadClass("net.sf.saxon.s9api.Location") // Saxon 10
    }

    private val location: Any by lazy {
        `class`.getMethodOrNull("getLocation")?.invoke(saxonObject) ?: saxonObject
    }

    override fun getSystemId(): String? {
        val id = locationClass.getMethod("getSystemId").invoke(location) as String?
        // Saxon <= 9.6 report "*module with no systemId*" for the script being run.
        return if (id == "*module with no systemId*") null else id
    }

    override fun getPublicId(): String? = locationClass.getMethod("getPublicId").invoke(location) as String?

    override fun getLineNumber(): Int = locationClass.getMethod("getLineNumber").invoke(location) as Int

    override fun getColumnNumber(): Int = locationClass.getMethod("getColumnNumber").invoke(location) as Int

    fun getObjectName(): QName? {
        val qname = `class`.getMethod("getObjectName").invoke(saxonObject)
        return qname?.let { StructuredQName(it, `class`.classLoader.loadClass("net.sf.saxon.om.StructuredQName")) }
    }

    override fun toString(): String = saxonObject.toString()

    override fun hashCode(): Int {
        // NOTE: Using saxonObject.hashCode results in ClauseInfo objects not to be grouped.
        var result = systemId.hashCode()
        result = 31 * result + publicId.hashCode()
        result = 31 * result + lineNumber.hashCode()
        result = 31 * result + columnNumber.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is InstructionInfo) return false

        // NOTE: Using saxonObject.equals results in ClauseInfo objects not to be grouped.
        return when {
            systemId != other.systemId -> false
            publicId != other.publicId -> false
            lineNumber != other.lineNumber -> false
            columnNumber != other.columnNumber -> false
            else -> true
        }
    }
}

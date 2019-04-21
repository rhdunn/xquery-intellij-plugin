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

import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.QName
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om.StructuredQName

open class InstructionInfo(val saxonObject: Any, private val `class`: Class<*>) {
    fun isClauseInfo(): Boolean {
        return `class`.classLoader.loadClassOrNull("net.sf.saxon.expr.flwor.ClauseInfo")?.isInstance(saxonObject) == true
    }

    fun getSystemId(): String? {
        val id = `class`.getMethod("getSystemId").invoke(saxonObject) as String?
        // Saxon <= 9.6 report "*module with no systemId*" for the script being run.
        return if (id == "*module with no systemId*") null else id
    }

    fun getLineNumber(): Int {
        return `class`.getMethod("getLineNumber").invoke(saxonObject) as Int
    }

    fun getColumnNumber(): Int {
        return `class`.getMethod("getColumnNumber").invoke(saxonObject) as Int
    }

    fun getObjectName(): QName? {
        val qname = `class`.getMethod("getObjectName").invoke(saxonObject)
        return qname?.let { StructuredQName(it, `class`.classLoader.loadClass("net.sf.saxon.om.StructuredQName")) }
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

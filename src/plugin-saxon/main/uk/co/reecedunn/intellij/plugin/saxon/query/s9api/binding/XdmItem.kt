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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_parse

open class XdmItem(saxonObject: Any, saxonClass: Class<*>) : XdmValue(saxonObject, saxonClass) {
    companion object {
        fun newInstance(value: Any?, type: String, processor: Processor): XdmItem {
            val classLoader = processor.classLoader
            return when (type) {
                "xs:QName" -> XdmAtomicValue(qname_parse(value as String, SAXON_NAMESPACES).toQName(classLoader))
                "xs:numeric" -> XdmNumeric.newInstance(value as String, classLoader)
                "comment()" -> XdmNode.newInstance(value, processor)
                "document-node()" -> XdmNode.newInstance(value, processor)
                "element()" -> XdmNode.newInstance(value, processor)
                "processing-instruction()" -> XdmNode.newInstance(value, processor)
                else -> XdmAtomicValue(value as String, type, classLoader)
            }
        }
    }
}

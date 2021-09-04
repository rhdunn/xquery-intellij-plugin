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
package uk.co.reecedunn.intellij.plugin.xpm.optree.variable

import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_equal
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.inScopeVariables

val XsQNameValue.variableDefinition: XpmVariableDefinition?
    get() = element?.inScopeVariables()?.find { variable ->
        val qname = variable.variableName!!
        if (variable is XpmVariableBinding) { // Locally defined, so can compare the prefix name.
            val matchPrefix = prefix?.data == qname.prefix?.data
            val matchLocalName = localName?.data == qname.localName?.data
            matchPrefix && matchLocalName
        } else {
            // NOTE: If there are a large number of variables, opening the context menu
            // can be is slow (~10 seconds) when just checking the expanded QName, so
            // check local-name first ...
            if (qname.localName?.data == localName?.data) {
                // ... then check the expanded QName namespace.
                val expanded = expand().firstOrNull()
                expanded != null && qname.expand().firstOrNull()?.let { qname_equal(it, expanded) } == true
                //true
            } else {
                false
            }
        }
    }

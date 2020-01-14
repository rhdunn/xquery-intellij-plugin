/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.functions.op

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsQName

class UndeclaredNamespacePrefixException(prefix: String) :
    RuntimeException("XPST0081: Undeclared namespace prefix: $prefix")

// region XPath and XQuery Functions and Operators 3.1 (10.2.1) op:QName-equal

@Suppress("FunctionName")
fun op_qname_equal(arg1: XsQNameValue, arg2: XsQNameValue): Boolean {
    if (arg1.localName?.data?.equals(arg2.localName?.data) == true) {
        return if (arg1.isLexicalQName && arg2.isLexicalQName) {
            (arg1.prefix == null && arg2.prefix == null) || arg1.prefix?.data?.equals(arg2.prefix?.data) == true
        } else {
            (arg1.namespace == null && arg2.namespace == null) || arg1.namespace?.data?.equals(arg2.namespace?.data) == true
        }
    }
    return false
}

// endregion
// region XQuery IntelliJ Plugin Functions and Operators (3.1) op:QName-parse

private fun anyURI(uri: String): XsAnyUriValue = XsAnyUri(uri, XdmUriContext.Namespace, XdmModuleType.NONE)

@Suppress("FunctionName")
fun op_qname_parse(qname: String, namespaces: Map<String, String>): XsQNameValue {
    return when {
        qname.startsWith("Q{") /* URIQualifiedName */ -> {
            val ns = anyURI(qname.substringBefore('}').substring(2))
            val localName = XsNCName(qname.substringAfter('}'), null as PsiElement?)
            XsQName(ns, null, localName, false)
        }
        qname.startsWith('{') /* Clark Notation */ -> {
            val ns = anyURI(qname.substringBefore('}').substring(1))
            val localName = XsNCName(qname.substringAfter('}'), null as PsiElement?)
            XsQName(ns, null, localName, false)
        }
        qname.contains(':') /* QName */ -> {
            val prefix = XsNCName(qname.substringBefore(':'), null as PsiElement?)
            val ns = namespaces[prefix.data]?.let { anyURI(it) }
                ?: throw UndeclaredNamespacePrefixException(prefix.data)
            val localName = XsNCName(qname.substringAfter(':'), null as PsiElement?)
            XsQName(ns, prefix, localName, true)
        }
        else /* NCName */ -> {
            val localName = XsNCName(qname, null as PsiElement?)
            XsQName(anyURI(""), null, localName, true)
        }
    }
}

// endregion
// region XQuery IntelliJ Plugin Functions and Operators (3.2) op:QName-presentation

@Suppress("FunctionName")
fun op_qname_presentation(qname: XsQNameValue, expanded: Boolean = false): String {
    return if (qname.prefix == null || expanded) {
        if (qname.namespace == null)
            qname.localName!!.data
        else
            "Q{${qname.namespace!!.data}}${qname.localName!!.data}"
    } else
        "${qname.prefix!!.data}:${qname.localName!!.data}"
}

// endregion

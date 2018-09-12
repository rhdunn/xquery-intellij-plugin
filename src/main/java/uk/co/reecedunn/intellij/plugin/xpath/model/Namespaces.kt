/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

private val XQUERY_NAMESPACE = XsAnyUri("http://www.w3.org/2012/xquery")

enum class XPathNamespaceType {
    DefaultElementOrType,
    DefaultFunction,
    None,
    Prefixed,
    Undefined,
    XQuery,
}

interface XPathNamespaceDeclaration {
    val namespacePrefix: XsNCNameValue?

    val namespaceUri: XsAnyUriValue?
}

interface XPathDefaultNamespaceDeclaration : XPathNamespaceDeclaration {
    val namespaceType: XPathNamespaceType
}

fun XsQNameValue.getNamespaceType(): XPathNamespaceType {
    val parentType = (this as? PsiElement)?.parent?.node?.elementType
    return when {
        parentType === XQueryElementType.ANNOTATION -> XPathNamespaceType.XQuery
        else -> XPathNamespaceType.Undefined
    }
}

fun XsQNameValue.expand(): Sequence<XsQNameValue> {
    return when {
        isLexicalQName && prefix == null -> { // NCName
            when (this.getNamespaceType()) {
                XPathNamespaceType.XQuery -> sequenceOf(XsQName(XQUERY_NAMESPACE, null, localName, false))
                else -> sequenceOf(this)
            }
        }
        else -> sequenceOf(this)
    }
}

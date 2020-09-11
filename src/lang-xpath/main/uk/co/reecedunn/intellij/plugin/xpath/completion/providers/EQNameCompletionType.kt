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
package uk.co.reecedunn.intellij.plugin.xpath.completion.providers

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element

enum class EQNameCompletionType {
    NCName,
    QNamePrefix,
    QNameLocalName,
    URIQualifiedNameBracedURI,
    URIQualifiedNameLocalName
}

fun XsQNameValue.completionType(element: PsiElement): EQNameCompletionType? = when (isLexicalQName) {
    true -> when {
        prefix == null -> EQNameCompletionType.NCName
        prefix?.element === element -> EQNameCompletionType.QNamePrefix
        localName?.element === element -> EQNameCompletionType.QNameLocalName
        else -> null
    }
    else -> when {
        namespace?.element === element -> EQNameCompletionType.URIQualifiedNameBracedURI
        localName?.element === element -> EQNameCompletionType.URIQualifiedNameLocalName
        else -> null
    }
}

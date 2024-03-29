/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.context.staticContext

fun PsiElement.getPrincipalNodeKind(): XpmUsageType = when (parent.elementType) {
    XPathElementType.ABBREV_FORWARD_STEP -> XpmUsageType.Attribute
    XPathElementType.FORWARD_STEP -> when (parent.firstChild.elementType) {
        XPathTokenType.K_ATTRIBUTE -> XpmUsageType.Attribute
        XPathTokenType.K_NAMESPACE -> XpmUsageType.Namespace
        else -> XpmUsageType.Element
    }
    else -> XpmUsageType.Element
}

fun PsiElement.getUsageType(): XpmUsageType = when (parent.elementType) {
    XPathElementType.NAME_TEST -> parent.getPrincipalNodeKind()
    else -> staticContext?.getUsageType(this) ?: XpmUsageType.Unknown
}

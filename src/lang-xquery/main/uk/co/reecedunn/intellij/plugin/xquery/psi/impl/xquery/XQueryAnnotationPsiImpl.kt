/*
 * Copyright (C) 2016-2017, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNumericLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyAtomicType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryAnnotationPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XQueryAnnotation, XdmAnnotation, VersionConformance {
    // region XdmAnnotation

    override val name: XsQNameValue? get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val values: Sequence<XsAnyAtomicType>
        get() {
            return children().map {
                when (it) {
                    is XPathNumericLiteral -> it as XsAnyAtomicType
                    is XPathStringLiteral -> it.value
                    else -> null
                }
            }.filterNotNull()
        }

    // endregion
    // region VersionConformance

    override val requiresConformance get(): List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

    override val conformanceElement
        get(): PsiElement = findChildByType(XQueryTokenType.ANNOTATION_INDICATOR) ?: firstChild

    // endregion
}

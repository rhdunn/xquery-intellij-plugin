/*
 * Copyright (C) 2016, 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionTest
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmFunction
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmItemType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

val NO_ANNOTATIONS = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)
val ANNOTATIONS = listOf<Version>()

class XQueryFunctionTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathFunctionTest, XdmItemType, VersionConformance {
    // region XdmSequenceType

    override val typeName: String = "function(*)"

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmFunction::class.java

    // endregion
    // region VersionConformance

    override val requiresConformance
        get(): List<Version> {
            return if (conformanceElement.node.elementType === XQueryElementType.ANNOTATION)
                ANNOTATIONS
            else
                NO_ANNOTATIONS
        }

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
}

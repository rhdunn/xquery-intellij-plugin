/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryTumblingWindowClause
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec

class XQueryTumblingWindowClausePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryTumblingWindowClause,
    VersionConformance,
    XPathVariableBinding {
    // region VersionConformance

    override val requiresConformance get(): List<Version> = listOf(XQuerySpec.REC_3_0_20140408)

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
    // region XPathVariableBinding

    private val varName
        get(): XPathVariableName? = children().filterIsInstance<XPathVarName>().firstOrNull() as? XPathVariableName

    override val variableName get(): XsQNameValue? = varName?.variableName

    // endregion
}

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
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryTumblingWindowClause
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class XQueryTumblingWindowClausePsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryTumblingWindowClause,
        XQueryConformance,
        XPathVariableBinding {
    // region XQueryConformance

    override val requiresConformance get(): List<Version> = listOf(XQuery.REC_3_0_20140408)

    override val conformanceElement get(): PsiElement =
        firstChild

    // endregion
    // region XPathVariableBinding

    private val varName get(): XPathVariableName? =
        children().filterIsInstance<XPathVarName>().firstOrNull() as? XPathVariableName

    override val cacheable get(): CachingBehaviour = varName?.cacheable ?: CachingBehaviour.Cache

    override val variableName get(): QName? = varName?.variableName

    // TODO: Locate and use the TypeDeclaration if present.
    override val variableType: XdmSequenceType? = null

    // The bound variable result is dependent on the sequence, so cannot be determined statically.
    override val variableValue: XdmStaticValue? = null

    // endregion
}

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
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.XsInteger
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmConstantExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmVariableName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCountClause
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver

class XQueryCountClausePsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryCountClause,
        XQueryVariableResolver,
        XPathVariableDeclaration {

    private val varName get(): XdmVariableName? =
        children().filterIsInstance<XdmVariableName>().firstOrNull()

    override val cacheable get(): CachingBehaviour = varName?.cacheable ?: CachingBehaviour.Cache

    override val variableName get(): QName? = varName?.variableName

    override val variableType: XdmSequenceType? = XsInteger

    // CountClause is a conditional variable that is evaluated dynamically.
    override val variableValue: XdmConstantExpression? = null

    override fun resolveVariable(name: XPathEQName?): XQueryVariable? {
        val eqname = findChildByClass(XPathEQName::class.java)
        return if (eqname != null && eqname == name) {
            XQueryVariable(eqname, this)
        } else null
    }
}

/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.scripting

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingBlockVarDeclEntry
import uk.co.reecedunn.intellij.plugin.xquery.lang.Scripting
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver

class ScriptingBlockVarDeclEntryPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        ScriptingBlockVarDeclEntry,
        XQueryVariableResolver,
        XPathVariableDeclaration {

    private val varName get(): XPathVariableName? =
        children().filterIsInstance<XPathVarName>().firstOrNull() as? XPathVariableName

    override val cacheable get(): CachingBehaviour = varName?.cacheable ?: CachingBehaviour.Cache

    override val variableName get(): QName? = varName?.variableName

    // TODO: Locate and use the TypeDeclaration if present.
    override val variableType: XdmSequenceType? = null

    // TODO: Locate and use the ExprSingle if present.
    override val variableValue: XdmStaticValue? = null

    override fun resolveVariable(name: XPathEQName?): XQueryVariable? {
        val varName = findChildByType<PsiElement>(XQueryElementType.VAR_NAME)
        if (varName != null && varName == name) {
            return XQueryVariable(varName, this)
        }
        return null
    }
}

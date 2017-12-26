/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNamedFunctionRef
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class XQueryNamedFunctionRefPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryNamedFunctionRef, XQueryConformance {
    override val requiresConformance get(): List<Version> = listOf(XQuery.REC_3_0_20140408, MarkLogic.VERSION_6_0)

    override val conformanceElement get(): PsiElement =
        findChildByType(XQueryTokenType.FUNCTION_REF_OPERATOR) ?: this

    override val functionName: XPathEQName? =
        findChildByClass(XPathEQName::class.java)

    override val arity get(): Int =
        children().filterIsInstance<XdmLexicalValue>().firstOrNull()?.toInt() ?: 0
}

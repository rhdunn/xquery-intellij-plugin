/*
 * Copyright (C) 2016-2017, 2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.flwor.XpmLetBinding
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLetClause

class XQueryLetClausePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XQueryLetClause, XpmSyntaxValidationElement {

    override val bindings: Sequence<XpmLetBinding>
        get() = children().filterIsInstance<XpmLetBinding>()

    override val conformanceElement: PsiElement
        get() = firstChild
}

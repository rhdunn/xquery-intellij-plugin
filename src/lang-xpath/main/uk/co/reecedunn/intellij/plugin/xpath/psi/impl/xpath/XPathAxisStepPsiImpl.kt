/*
 * Copyright (C) 2016, 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAxisStep
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmPathStep
import uk.co.reecedunn.intellij.plugin.xpm.optree.expr.XpmExpression

class XPathAxisStepPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathAxisStep {
    override val axisType: XpmAxisType
        get() = (firstChild as XpmPathStep).axisType

    override val nodeName: XsQNameValue?
        get() = (firstChild as XpmPathStep).nodeName

    override val nodeType: XdmItemType
        get() = (firstChild as XpmPathStep).nodeType

    override val predicateExpression: XpmExpression? = null
}

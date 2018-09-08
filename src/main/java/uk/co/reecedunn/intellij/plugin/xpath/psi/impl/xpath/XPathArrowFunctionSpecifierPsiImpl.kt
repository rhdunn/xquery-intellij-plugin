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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArrowFunctionSpecifier
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

class XPathArrowFunctionSpecifierPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathFunctionReference,
    XPathArrowFunctionSpecifier {

    override val arity
        get(): Int {
            val args: XPathArgumentList? = siblings().filterIsInstance<XPathArgumentList>().firstOrNull()
            return args?.arity?.plus(1) ?: 1
        }

    override val functionName get(): XsQNameValue? = firstChild as? XsQNameValue
}

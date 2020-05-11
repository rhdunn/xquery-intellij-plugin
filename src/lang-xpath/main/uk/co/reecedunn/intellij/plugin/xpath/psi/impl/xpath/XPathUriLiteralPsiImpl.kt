/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.content.XdmLiteralTextPart
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTStopWords
import uk.co.reecedunn.intellij.plugin.xpath.ast.full.text.FTThesaurusID
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriPsiElement

class XPathUriLiteralPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathUriLiteral, XsAnyUriPsiElement, XpmModulePath {
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedData.invalidate()
    }

    // endregion
    // region XsAnyUriValue

    override val context: XdmUriContext
        get() = when (parent) {
            is FTStopWords -> XdmUriContext.StopWords
            is FTThesaurusID -> XdmUriContext.Thesaurus
            else -> XdmUriContext.Namespace
        }

    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.NONE

    override val data: String get() = cachedData.get()!!

    private val cachedData: CacheableProperty<String> = CacheableProperty {
        children().filterIsInstance<XdmLiteralTextPart>().joinToString("") { it.unescapedValue }
    }

    // endregion
}

/*
 * Copyright (C) 2016; 2018-2019 Reece H. Dunn
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
import com.intellij.openapi.util.TextRange
import uk.co.reecedunn.intellij.plugin.intellij.lang.foldable.FoldablePsiElement
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xqdoc.parser.XQueryCommentLineExtractor

class XPathCommentPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathComment,
    FoldablePsiElement {
    // region XPathComment

    override val isXQDoc: Boolean
        get() = firstChild.nextSibling?.node?.elementType === XQDocTokenType.XQDOC_COMMENT_MARKER

    // endregion
    // region FoldablePsiElement

    override val foldingRange: TextRange? get() = textRange

    override val foldingPlaceholderText: String?
        get() {
            val text = this.text
            val parser =
                if (text.endsWith(":)"))
                    XQueryCommentLineExtractor(text.subSequence(2, text.length - 2))
                else
                    XQueryCommentLineExtractor(text.subSequence(2, text.length))
            return if (parser.next()) {
                if (parser.isXQDoc)
                    "(:~ ${parser.text} :)"
                else
                    "(: ${parser.text} :)"
            } else
                "(:...:)"
        }

    // endregion
}

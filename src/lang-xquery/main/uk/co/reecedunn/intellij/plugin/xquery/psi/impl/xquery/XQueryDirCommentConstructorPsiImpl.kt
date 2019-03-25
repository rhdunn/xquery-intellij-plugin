/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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
import com.intellij.openapi.util.TextRange
import uk.co.reecedunn.intellij.plugin.intellij.lang.foldable.FoldablePsiElement
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirCommentConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryDirCommentConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryDirCommentConstructor,
    FoldablePsiElement {
    // region FoldablePsiElement

    override val foldingRange: TextRange? get() = textRange

    override val foldingPlaceholderText: String?
        get() {
            var length = textRange.length
            if (lastChild.node.elementType === XQueryTokenType.XML_COMMENT_END_TAG)
                length -= 3

            val firstLine = text.substring(4, length).split("\n").firstOrNull { line ->
                line.trim().isNotEmpty()
            }
            return firstLine?.let { "<!--$it-->" } ?: "<!--...-->"
        }

    // endregion
}

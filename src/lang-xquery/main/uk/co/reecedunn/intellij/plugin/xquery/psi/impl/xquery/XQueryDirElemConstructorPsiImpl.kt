/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.lang.foldable.FoldablePsiElement
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyAtomicType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class XQueryDirElemConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryDirElemConstructor,
    XdmElementNode,
    FoldablePsiElement {
    // region XdmElementNode

    override val nodeName: XsQNameValue? get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val typedValue: XsAnyAtomicType? = null

    override val closingTag: XsQNameValue? get() = children().filterIsInstance<XsQNameValue>().lastOrNull()

    // endregion
    // region FoldablePsiElement

    override val foldingRange: TextRange?
        get() {
            var hasEnclosedExprOnlyContent = false
            var hasMultiLineAttributes = false

            val contents = node.findChildByType(XQueryElementType.DIR_ELEM_CONTENT)
            if (contents != null) {
                val first = contents.firstChildNode
                val last = contents.lastChildNode
                if (first === last && first.elementType === XQueryElementType.ENCLOSED_EXPR) {
                    hasEnclosedExprOnlyContent = true
                }
            }

            var start: PsiElement? = firstChild
            if (start!!.elementType === XQueryTokenType.OPEN_XML_TAG)
                start = start!!.nextSibling
            if (start!!.elementType === XQueryTokenType.XML_WHITE_SPACE)
                start = start!!.nextSibling
            if (
                start!!.elementType === XQueryElementType.NCNAME ||
                start!!.elementType === XQueryElementType.QNAME
            )
                start = start!!.nextSibling
            if (start?.elementType === XQueryTokenType.XML_WHITE_SPACE)
                start = start.nextSibling
            if (start?.elementType === XQueryElementType.DIR_ATTRIBUTE_LIST) {
                hasMultiLineAttributes = start.textContains('\n')
                if (!hasMultiLineAttributes) {
                    start = start.nextSibling
                }
            }

            val end = lastChild
            val endOffset =
                if (
                    end.elementType === XQueryTokenType.CLOSE_XML_TAG ||
                    end.elementType === XQueryTokenType.SELF_CLOSING_XML_TAG
                ) {
                    end.prevSibling.textRange.endOffset
                } else {
                    end.textRange.startOffset
                }

            if (hasEnclosedExprOnlyContent && !hasMultiLineAttributes || start == null) {
                return null
            }
            return TextRange(start.textRange.startOffset, endOffset)
        }

    override val foldingPlaceholderText: String? = "..."

    // endregion
}

/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsNotElementType
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

val PsiElement.blockOpen: PsiElement?
    get() = children().filterIsElementType(XPathTokenType.BLOCK_OPEN).firstOrNull()

val PsiElement.isEmptyEnclosedExpr: Boolean
    get() = siblings().filterIsNotElementType(IGNORE_TOKENS).firstOrNull() == null

val PsiElement.blockFoldingRange: TextRange?
    get() = when (val blockOpen = blockOpen) {
        null -> null
        else -> TextRange.create(blockOpen.textOffset, textRange.endOffset)
    }

private val IGNORE_TOKENS = TokenSet.create(
    XPathTokenType.WHITE_SPACE,
    XPathElementType.COMMENT,
    XPathTokenType.BLOCK_OPEN,
    XPathTokenType.BLOCK_CLOSE
)

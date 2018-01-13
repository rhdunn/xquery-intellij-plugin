/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package uk.co.reecedunn.intellij.plugin.xquery.psi.impl

import com.intellij.lang.Language
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryDirWhiteSpaceImpl(text: CharSequence) : LeafPsiElement(XQueryTokenType.XML_WHITE_SPACE, text), PsiWhiteSpace {
    override fun accept(visitor: PsiElementVisitor) {
        visitor.visitWhiteSpace(this)
    }

    override fun toString(): String = "XQueryDirWhiteSpaceImpl"

    override fun getLanguage(): Language {
        val master = parent
        return master?.language ?: Language.ANY
    }
}

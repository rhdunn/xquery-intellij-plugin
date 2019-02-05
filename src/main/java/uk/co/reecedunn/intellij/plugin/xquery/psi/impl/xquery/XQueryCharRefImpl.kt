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

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.XmlChar
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef

class XQueryCharRefImpl(type: IElementType, text: CharSequence) : LeafPsiElement(type, text), XQueryCharRef {
    override val codepoint get(): XmlChar {
        val ref = text
        return if (ref.startsWith("&#x")) // `&#x...;` hexadecimal character reference
            XmlChar(
                ref.subSequence(3, ref.length - 1).toString().toInt(
                    radix = 16
                )
            )
        else // `&#...;` decimal character reference
            XmlChar(
                ref.subSequence(2, ref.length - 1).toString().toInt(
                    radix = 10
                )
            )
    }
}

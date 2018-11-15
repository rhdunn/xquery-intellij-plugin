/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.parser

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenTypeWithMarker

open class PsiTreeParser(private val builder: PsiBuilder) {
    // region Parser Helper Functions

    protected fun matchTokenType(type: IElementType): Boolean {
        return builder.matchTokenType(type)
    }

    protected fun matchTokenTypeWithMarker(type: IElementType): PsiBuilder.Marker? {
        return builder.matchTokenTypeWithMarker(type)
    }

    protected fun matchTokenTypeWithMarker(type1: IElementType, type2: IElementType): PsiBuilder.Marker? {
        return builder.matchTokenTypeWithMarker(type1, type2)
    }

    protected fun errorOnTokenType(type: IElementType, message: String): Boolean {
        return builder.errorOnTokenType(type, message)
    }

    // endregion
    // region PsiBuilder API

    protected fun mark(): PsiBuilder.Marker = builder.mark()

    protected fun getTokenType(): IElementType? = builder.tokenType

    protected fun advanceLexer() = builder.advanceLexer()

    protected fun error(message: String) = builder.error(message)

    // endregion
}

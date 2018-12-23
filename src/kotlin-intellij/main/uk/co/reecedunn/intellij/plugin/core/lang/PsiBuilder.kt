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
package uk.co.reecedunn.intellij.plugin.core.lang

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

fun PsiBuilder.matchTokenType(type: IElementType): Boolean {
    if (tokenType === type) {
        advanceLexer()
        return true
    }
    return false
}

fun PsiBuilder.matchTokenTypeWithMarker(type: IElementType): PsiBuilder.Marker? {
    if (tokenType === type) {
        val marker = mark()
        advanceLexer()
        return marker
    }
    return null
}

fun PsiBuilder.matchTokenTypeWithMarker(type: TokenSet): PsiBuilder.Marker? {
    if (type.contains(tokenType)) {
        val marker = mark()
        advanceLexer()
        return marker
    }
    return null
}

fun PsiBuilder.errorOnTokenType(type: IElementType, message: String): Boolean {
    if (tokenType === type) {
        val errorMarker = mark()
        advanceLexer()
        errorMarker.error(message)
        return true
    }
    return false
}

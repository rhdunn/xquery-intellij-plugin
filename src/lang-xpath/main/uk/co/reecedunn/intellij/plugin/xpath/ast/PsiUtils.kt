/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.ast

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExpr
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

fun <T> PsiElement.filterExpressions(klass: Class<T>): Sequence<T> {
    val item = children().filterIsInstance(klass)
    val sequence = children().filterIsInstance<XPathExpr>().firstOrNull()
    return if (sequence != null)
        sequenceOf(item, sequence.children().filterIsInstance(klass)).flatten()
    else
        item
}

inline fun <reified T> PsiElement.filterExpressions(): Sequence<T> = filterExpressions(T::class.java)

fun <T> PsiElement.filterEnclosedExpressions(klass: Class<T>): Sequence<T> {
    val enclosed = children().filterIsInstance<XPathEnclosedExpr>().firstOrNull()
    return enclosed?.filterExpressions(klass) ?: sequenceOf()
}

inline fun <reified T> PsiElement.filterEnclosedExpressions(): Sequence<T> = filterEnclosedExpressions(T::class.java)

fun Sequence<PsiElement>.filterNotWhitespace(): Sequence<PsiElement> = filterNot { e ->
    e.elementType === XPathTokenType.WHITE_SPACE || e is XPathComment
}

val PsiElement.isArrowFunctionCall: Boolean
    get() = when (this) {
        is PluginArrowFunctionCall -> true
        is PluginArrowDynamicFunctionCall -> true
        else -> false
    }

val PsiElement.parenthesizedExprTextOffset: Int?
    get() {
        val pref = reverse(siblings()).filterNotWhitespace().firstOrNull()
        return when {
            pref == null -> null
            pref.elementType !== XPathTokenType.PARENTHESIS_OPEN -> null
            pref.parent !is XPathArgumentList -> pref.textOffset
            pref.parent.firstChild !== pref -> pref.textOffset
            else -> null
        }
    }

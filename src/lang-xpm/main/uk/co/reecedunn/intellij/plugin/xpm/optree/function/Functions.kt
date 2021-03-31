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
package uk.co.reecedunn.intellij.plugin.xpm.optree.function

import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmConcatenatingExpressionImpl
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl.XpmBoundParameter
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.keyName
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmAssignableVariable
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions

val XpmFunctionCall.functionReference: XpmFunctionReference?
    get() = when (val expr = functionCallExpression) {
        is XpmFunctionReference -> expr
        else -> null
    }

val XpmFunctionCall.functionDeclaration: XpmFunctionDeclaration?
    get() {
        val ref = functionReference
        return ref?.functionName?.staticallyKnownFunctions()?.firstOrNull { f ->
            f.arity.isWithin(ref.arity)
        }
    }

val XpmFunctionCall.resolve: Pair<XpmFunctionDeclaration, List<XpmAssignableVariable>>?
    get() = functionDeclaration?.let { it to bindTo(it.parameters) }

fun XpmFunctionCall.bindTo(parameters: List<XpmParameter>): List<XpmAssignableVariable> {
    val positionalArguments = positionalArguments
    var positionalIndex = -1

    return parameters.mapIndexed { index, parameter ->
        when {
            index == 0 && this is XpmArrowFunctionCall /* arrow expression initial argument */ -> {
                XpmBoundParameter(parameter, sourceExpression)
            }
            index == parameters.size - 1 /* last parameter */ -> {
                positionalIndex += 1
                val remaining = positionalArguments.size - positionalIndex
                when {
                    remaining <= 0 -> {
                        val parameterName = parameter.variableName?.localName?.data
                        val arg = keywordArguments.find { it.keyName == parameterName }
                        XpmBoundParameter(parameter, arg?.valueExpression ?: XpmEmptyExpression)
                    }
                    remaining == 1 -> XpmBoundParameter(parameter, positionalArguments.last())
                    else -> {
                        val args = positionalArguments.subList(positionalIndex, positionalArguments.size)
                        XpmBoundParameter(parameter, XpmConcatenatingExpressionImpl(args.asSequence()))
                    }
                }
            }
            else -> {
                positionalIndex += 1
                val remaining = positionalArguments.size - positionalIndex
                when {
                    remaining <= 0 -> {
                        val parameterName = parameter.variableName?.localName?.data
                        val arg = keywordArguments.find { it.keyName == parameterName }
                        XpmBoundParameter(parameter, arg?.valueExpression)
                    }
                    else -> XpmBoundParameter(parameter, positionalArguments[positionalIndex])
                }
            }
        }
    }
}

fun XpmFunctionCall.argumentAt(index: Int): XpmExpression? {
    val arrowOffset = if (this is XpmArrowFunctionCall) 1 else 0
    val p = positionalArguments.size
    val k = keywordArguments.size
    return when {
        index == 0 && arrowOffset == 1 -> (this as XpmArrowFunctionCall).sourceExpression
        index < (p + arrowOffset) -> positionalArguments[index - arrowOffset]
        index < (p + k + arrowOffset) -> keywordArguments[index - arrowOffset - p].valueExpression
        else -> null
    }
}

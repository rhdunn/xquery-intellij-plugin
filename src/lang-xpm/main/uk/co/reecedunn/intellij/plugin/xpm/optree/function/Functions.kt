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

import uk.co.reecedunn.intellij.plugin.xpm.optree.expr.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expr.impl.XpmExpressionsImpl
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl.XpmBoundParameter
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableBinding
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

val XpmFunctionCall.resolve: Pair<XpmFunctionDeclaration, List<XpmVariableBinding>>?
    get() {
        val decl = functionDeclaration ?: return null
        val parameters = decl.parameters

        val positionalArguments = positionalArguments
        var positionalIndex = -1
        return decl to parameters.mapIndexed { index, parameter ->
            when {
                index == 0 && this is XpmArrowFunctionCall /* arrow expression initial argument */ -> {
                    XpmBoundParameter(parameter, sourceExpression)
                }
                index == parameters.size - 1 /* last parameter */ -> {
                    positionalIndex += 1
                    val remaining = positionalArguments.size - positionalIndex
                    when {
                        remaining == 0 -> XpmBoundParameter(parameter, XpmEmptyExpression)
                        remaining == 1 -> XpmBoundParameter(parameter, positionalArguments.last())
                        else -> {
                            val args = positionalArguments.subList(positionalIndex, positionalArguments.size)
                            XpmBoundParameter(parameter, XpmExpressionsImpl(args))
                        }
                    }
                }
                else -> {
                    positionalIndex += 1
                    XpmBoundParameter(parameter, positionalArguments[positionalIndex])
                }
            }
        }
    }

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
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmConcatenatingExpressionImpl
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl.XpmBoundParameter
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl.XpmMissingArgument
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
    get() = functionDeclaration?.let { it to bindTo(it.parameters, it.isVariadic) }

fun XpmFunctionCall.bindTo(parameters: List<XpmParameter>, isVariadic: Boolean): List<XpmAssignableVariable> {
    val bindings = arrayOfNulls<XpmAssignableVariable>(parameters.size)
    var index = 0

    // region Arrow Expressions

    var arrowOffset = 0
    if (this is XpmArrowFunctionCall) {
        bindings[0] = XpmBoundParameter(parameters[0], sourceExpression)
        ++index
        arrowOffset = 1
    }

    // endregion
    // region Positional Arguments

    val positionalArguments = positionalArguments
    for (i in 0 until positionalArguments.size) when {
        // Positional argument position is less than the number of declared parameters.
        index < bindings.size - 1 -> {
            bindings[index] = XpmBoundParameter(parameters[index], positionalArguments[i])
            ++index
        }
        // Only one value corresponding to the last parameter.
        index == bindings.size - 1 && bindings.size == positionalArguments.size + arrowOffset -> {
            bindings[index] = XpmBoundParameter(parameters[index], positionalArguments[i])
            ++index
        }
        // Multiple values corresponding to the last parameter.
        index == bindings.size - 1 -> {
            val args = positionalArguments.subList(i, positionalArguments.size)
            bindings[index] = XpmBoundParameter(parameters[index], XpmConcatenatingExpressionImpl(args.asSequence()))
            ++index
        }
    }

    // endregion
    // region Keyword Arguments

    keywordArguments.forEach { arg ->
        val parameter = parameters.withIndex().find { it.value.variableName?.localName?.data == arg.keyName }
        if (parameter != null) {
            bindings[parameter.index] = XpmBoundParameter(parameter.value, arg.valueExpression)
        }
    }

    // endregion
    // region Default and Missing Arguments

    parameters.withIndex().filter { bindings[it.index] == null }.forEach { (i, parameter) ->
        when {
            i == parameters.size - 1 && isVariadic -> {
                bindings[i] = XpmBoundParameter(parameter, XpmEmptyExpression)
            }
            else -> bindings[i] = XpmBoundParameter(parameter, XpmMissingArgument)
        }
    }

    // endregion

    return bindings.map { it!! }
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

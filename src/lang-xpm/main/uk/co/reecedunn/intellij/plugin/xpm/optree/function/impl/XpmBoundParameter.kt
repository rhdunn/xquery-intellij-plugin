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
package uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl

import com.intellij.navigation.NavigationItem
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmAssignableVariable
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter

data class XpmBoundParameter(val parameter: XpmParameter, val argument: XpmExpression?) : XpmAssignableVariable {
    override val variableName: XsQNameValue?
        get() = parameter.variableName

    override val variableType: XdmSequenceType?
        get() = parameter.variableType

    override val variableExpression: XpmExpression?
        get() = argument ?: parameter.defaultExpression

    override fun toString(): String = (parameter as NavigationItem).presentation?.presentableText!!
}

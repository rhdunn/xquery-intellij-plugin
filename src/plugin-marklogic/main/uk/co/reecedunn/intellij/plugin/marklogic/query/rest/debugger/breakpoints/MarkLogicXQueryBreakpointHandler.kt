/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.breakpoints

import com.intellij.psi.PsiElement
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.MarkLogicDebugSession
import uk.co.reecedunn.intellij.plugin.xquery.intellij.xdebugger.breakpoints.XQueryBreakpointProperties
import java.lang.ref.WeakReference

internal class MarkLogicXQueryBreakpointHandler(
    type: Class<out XBreakpointType<XLineBreakpoint<XQueryBreakpointProperties>, XQueryBreakpointProperties>>,
    private val session: WeakReference<MarkLogicDebugSession>
) : XBreakpointHandler<XLineBreakpoint<XQueryBreakpointProperties>>(type) {
    // region XBreakpointHandler

    val expressionBreakpoints = ArrayList<PsiElement>()

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>) {
        val expr = breakpoint.properties.getExpression(breakpoint) ?: return
        expressionBreakpoints.add(expr)
        session.get()?.updateBreakpoint(expr, register = true)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>, temporary: Boolean) {
        val expr = breakpoint.properties.getExpression(breakpoint) ?: return
        expressionBreakpoints.remove(expr)
        session.get()?.updateBreakpoint(expr, register = false)
    }

    // endregion
}

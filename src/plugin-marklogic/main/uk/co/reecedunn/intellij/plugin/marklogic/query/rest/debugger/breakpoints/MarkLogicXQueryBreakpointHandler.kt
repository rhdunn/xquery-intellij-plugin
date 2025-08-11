// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.breakpoints

import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.MarkLogicDebugSession
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.xdebugger.breakpoints.XQueryBreakpointProperties
import java.lang.ref.WeakReference

internal class MarkLogicXQueryBreakpointHandler(
    type: Class<out XBreakpointType<XLineBreakpoint<XQueryBreakpointProperties>, XQueryBreakpointProperties>>,
    private val project: Project,
    private val session: WeakReference<MarkLogicDebugSession>
) : XBreakpointHandler<XLineBreakpoint<XQueryBreakpointProperties>>(type) {
    // region XBreakpointHandler

    val expressionBreakpoints = ArrayList<XpmExpression>()

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>) {
        val position = breakpoint.sourcePosition ?: return
        val expr = breakpoint.properties.getExpression(project, position) ?: return
        expressionBreakpoints.add(expr)
        session.get()?.updateBreakpoint(expr, register = true)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>, temporary: Boolean) {
        val position = breakpoint.sourcePosition ?: return
        val expr = breakpoint.properties.getExpression(project, position) ?: return
        expressionBreakpoints.remove(expr)
        session.get()?.updateBreakpoint(expr, register = false)
    }

    // endregion
}

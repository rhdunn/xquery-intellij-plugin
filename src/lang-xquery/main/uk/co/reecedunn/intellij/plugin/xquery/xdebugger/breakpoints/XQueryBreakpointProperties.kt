// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.xdebugger.breakpoints

import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.impl.breakpoints.XBreakpointBase
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XQueryBreakpointProperties : XBreakpointProperties<XQueryBreakpointProperties>() {
    @OptionTag("expr-offset")
    var exprOffset: Int = -1

    fun getExpression(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>?): XpmExpression? {
        val position = breakpoint?.sourcePosition ?: return null
        val project = getProject(breakpoint) ?: return null
        return getExpression(project, position)
    }

    fun getExpression(project: Project, position: XSourcePosition): XpmExpression? {
        val module = position.file.toPsiFile(project) as? XQueryModule ?: return null
        val element = module.findElementAt(exprOffset) ?: return null
        return element.ancestorsAndSelf().filterIsInstance<XpmExpression>().firstOrNull { expr ->
            expr.expressionElement != null
        }
    }

    private fun getProject(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>): Project? {
        @Suppress("UNCHECKED_CAST")
        return (breakpoint as? XBreakpointBase<XBreakpoint<*>, *, *>)?.getProject()
    }

    override fun getState(): XQueryBreakpointProperties = this

    override fun loadState(state: XQueryBreakpointProperties): Unit = XmlSerializerUtil.copyBean(state, this)
}

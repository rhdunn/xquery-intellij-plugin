// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.xdebugger.breakpoints

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Transient
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XQueryBreakpointProperties : XBreakpointProperties<XQueryBreakpointProperties>() {
    @Transient
    var textRange: TextRange? = null

    @get:OptionTag("expr-offset")
    @set:OptionTag("expr-offset")
    var exprOffset: Int
        get() = textRange?.startOffset ?: -1
        set(value) {
            textRange = when (value) {
                -1 -> null
                else -> TextRange(value, value + exprLength)
            }
        }

    @OptionTag("expr-length")
    var exprLength: Int = 0
        set(value) {
            field = value
            textRange = when (exprOffset) {
                -1 -> null
                else -> TextRange(exprOffset, exprOffset + value)
            }
        }

    fun getExpression(project: Project, position: XSourcePosition): XpmExpression? {
        val module = position.file.toPsiFile(project) as? XQueryModule ?: return null
        val element = module.findElementAt(exprOffset) ?: return null
        return element.ancestorsAndSelf().filterIsInstance<XpmExpression>().firstOrNull { expr ->
            expr.expressionElement != null
        }
    }

    override fun getState(): XQueryBreakpointProperties = this

    override fun loadState(state: XQueryBreakpointProperties): Unit = XmlSerializerUtil.copyBean(state, this)
}

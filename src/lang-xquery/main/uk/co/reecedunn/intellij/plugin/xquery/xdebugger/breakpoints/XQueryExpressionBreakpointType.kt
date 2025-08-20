// Copyright (C) 2020-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.xdebugger.breakpoints

import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import com.intellij.xdebugger.impl.XSourcePositionImpl
import uk.co.reecedunn.intellij.plugin.core.psi.lineElements
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import javax.swing.Icon

class XQueryExpressionBreakpointType :
    XLineBreakpointType<XQueryBreakpointProperties>(
        "xijp.xquery-expr",
        XQueryBundle.message("breakpoint.expression")
    ) {
    // region XLineBreakpointType

    override fun createBreakpointProperties(file: VirtualFile, line: Int): XQueryBreakpointProperties {
        return createProperties()
    }

    override fun createProperties(): XQueryBreakpointProperties = XQueryBreakpointProperties()

    override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean {
        val module = file.toPsiFile(project) as? XQueryModule ?: return false
        return getExpressionsAt(module, line).any()
    }

    override fun computeVariants(project: Project, position: XSourcePosition): MutableList<out XLineBreakpointVariant> {
        val module = position.file.toPsiFile(project) as? XQueryModule ?: return mutableListOf()
        return getExpressionsAt(module, position.line)
            .distinctBy { it.hashCode() }
            .mapNotNull { expression ->
                val element = expression as PsiElement
                XSourcePositionImpl.createByElement(element)?.let { ExpressionBreakpointVariant(it, element) }
            }
            .sortedBy { it.highlightRange?.startOffset }
            .toMutableList().asReversed()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getHighlightRange(breakpoint: XLineBreakpoint<XQueryBreakpointProperties>?): TextRange? {
        return breakpoint?.properties?.textRange
    }

    private fun getExpressionsAt(module: XQueryModule, line: Int): Sequence<XpmExpression> {
        return module.lineElements(line).flatMap {
            it.ancestorsAndSelf().filterIsInstance<XpmExpression>().filter { expr -> expr.expressionElement != null }
        }
    }

    // endregion
    // region ExpressionBreakpointVariant

    inner class ExpressionBreakpointVariant(
        position: XSourcePosition,
        element: PsiElement
    ) : XLinePsiElementBreakpointVariant(position, element) {
        private val icon: Icon = (element as? NavigationItem)?.presentation?.getIcon(false) ?: XPathIcons.Nodes.Expr
        private val text: String = element.text
        private val highlightRange: TextRange? = element.textRange

        override fun createProperties(): XQueryBreakpointProperties? {
            val properties = super.createProperties() ?: return null
            properties.textRange = highlightRange
            return properties
        }

        override fun getIcon(): Icon = icon
        override fun getText(): String = StringUtil.shortenTextWithEllipsis(text, 100, 0)
        override fun getHighlightRange(): TextRange? = highlightRange
    }

    // endregion
}

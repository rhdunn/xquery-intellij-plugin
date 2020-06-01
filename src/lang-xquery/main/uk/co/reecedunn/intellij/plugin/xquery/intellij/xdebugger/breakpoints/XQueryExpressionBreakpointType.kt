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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.xdebugger.breakpoints

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import com.intellij.xdebugger.impl.XSourcePositionImpl
import uk.co.reecedunn.intellij.plugin.core.psi.lineElements
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.intellij.resources.XQueryBundle

class XQueryExpressionBreakpointType :
    XLineBreakpointType<XQueryBreakpointProperties>(
        "xijp.xquery-expr",
        XQueryBundle.message("breakpoint.expression")
    ) {
    // region XLineBreakpointType

    override fun createBreakpointProperties(file: VirtualFile, line: Int): XQueryBreakpointProperties? {
        return XQueryBreakpointProperties()
    }

    override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean {
        val module = file.toPsiFile(project) as? XQueryModule ?: return false
        return getExpressionsAt(module, line).any()
    }

    override fun computeVariants(project: Project, position: XSourcePosition): MutableList<out XLineBreakpointVariant> {
        val module = position.file.toPsiFile(project) as? XQueryModule ?: return mutableListOf()
        return getExpressionsAt(module, position.line)
            .distinctBy { it.hashCode() }
            .mapNotNull { element ->
                XSourcePositionImpl.createByElement(element)?.let { XLinePsiElementBreakpointVariant(it, element) }
            }
            .sortedBy { it.highlightRange?.startOffset }
            .toMutableList().asReversed()
    }

    private fun getExpressionsAt(module: XQueryModule, line: Int): Sequence<XPathExpr> {
        return module.lineElements(line).flatMap { element ->
            element.ancestorsAndSelf().filterIsInstance<XPathExpr>()
        }
    }

    // endregion
}

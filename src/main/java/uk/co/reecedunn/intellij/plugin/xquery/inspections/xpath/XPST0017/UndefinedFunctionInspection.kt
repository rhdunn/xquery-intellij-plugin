/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0017

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

/** XPST0017 error condition
 *
 * It is a *static error* if a QName used in a query contains a
 * namespace prefix that is not in the *statically known namespaces*.
 */
class UndefinedFunctionInspection : LocalInspectionTool() {
    override fun getDisplayName(): String =
        XQueryBundle.message("inspection.XPST0017.undefined-function.display-name")

    override fun getDescriptionFileName(): String? =
        id + ".html"

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryFile) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XPathEQName>()
                       .filter { qname -> qname.type == XPathEQName.Type.Function }
                       .forEach { qname ->
            val declarations = qname.resolveFunctionDecls().toList()
            val context = qname.localName
            if (context == null) {
                // Missing local name -- do nothing.
            } else if (declarations.isEmpty()) {
                // 1. The expanded QName does not match the name of a function signature in the static context.
                val description = XQueryBundle.message("inspection.XPST0017.undefined-function.unresolved-qname")
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
            } else {
                // 2. The number of arguments does not match the arity of a function signature in the static context.
                val parent = qname.parent
                val arity = when (parent) {
                    is XQueryFunctionCall -> parent.arity
                    is XQueryNamedFunctionRef -> parent.arity
                    is XQueryArrowFunctionSpecifier -> parent.arity
                    else -> -1
                }
                if (declarations.firstOrNull { f -> f.arity == arity } == null) {
                    val description = XQueryBundle.message("inspection.XPST0017.undefined-function.unresolved-arity")
                    descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
                }
            }
        }
        return descriptors.toTypedArray()
    }
}

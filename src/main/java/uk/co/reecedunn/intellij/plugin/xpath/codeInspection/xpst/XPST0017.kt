/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.xpst

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryPluginBundle
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XPST0017 : Inspection("xpst/XPST0017.md", XPST0017::class.java.classLoader) {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XpmFunctionReference>()
            .forEach { ref ->
                val qname = ref.functionName ?: return@forEach
                if (qname.localName == null) return@forEach

                val declarations = (qname as? XPathEQName)?.staticallyKnownFunctions()?.toList() ?: return@forEach
                if (declarations.isEmpty()) {
                    // 1. The expanded QName does not match the name of a function signature in the static context.
                    descriptors.add(
                        manager.createProblemDescriptor(
                            qname.localName?.element!!,
                            XQueryPluginBundle.message("inspection.XPST0017.undefined-function.unresolved-qname"),
                            null as LocalQuickFix?,
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                            isOnTheFly
                        )
                    )
                } else {
                    // 2. The number of arguments does not match the arity of a function signature in the static context.
                    val arity = (qname.parent as? XpmFunctionReference)?.arity ?: -1
                    if (declarations.firstOrNull { f -> f.arity.isWithin(arity) } == null) {
                        descriptors.add(
                            manager.createProblemDescriptor(
                                qname.localName?.element!!,
                                XQueryPluginBundle.message("inspection.XPST0017.undefined-function.unresolved-arity"),
                                null as LocalQuickFix?,
                                ProblemHighlightType.GENERIC_ERROR,
                                isOnTheFly
                            )
                        )
                    }
                }
            }
        return descriptors.toTypedArray()
    }
}

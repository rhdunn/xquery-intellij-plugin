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
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XPST0081 : Inspection("xpst/XPST0081.md", XPST0081::class.java.classLoader) {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XPathEQName>().forEach { eqname ->
            val qname = eqname as XsQNameValue
            if (qname.prefix != null && qname.prefix!!.data != "xmlns" && !eqname.expand().any()) {
                descriptors.add(
                    manager.createProblemDescriptor(
                        qname.prefix?.element!!,
                        XQueryPluginBundle.message("inspection.XPST0081.unbound-qname-prefix.message"),
                        null as LocalQuickFix?,
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        isOnTheFly
                    )
                )
            }
        }
        return descriptors.toTypedArray()
    }
}

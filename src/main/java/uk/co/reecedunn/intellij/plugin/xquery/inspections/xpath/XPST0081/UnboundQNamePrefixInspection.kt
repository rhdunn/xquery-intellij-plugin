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
@file:Suppress("PackageName")

package uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0081

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.model.expand
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.inspections.Inspection
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

/** XPST0081 error condition
 *
 * It is a *static error* if a QName used in a query contains a
 * namespace prefix that is not in the *statically known namespaces*.
 */
class UnboundQNamePrefixInspection : Inspection("xpst/XPST0081.md") {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XPathEQName>().forEach { eqname ->
            val qname = eqname as XsQNameValue
            if (qname.prefix != null && qname.prefix!!.data != "xmlns" && !eqname.expand().any()) {
                val description = XQueryBundle.message("inspection.XPST0081.unbound-qname-prefix.message")
                val context = qname.prefix?.element!!
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
            }
        }
        return descriptors.toTypedArray()
    }
}

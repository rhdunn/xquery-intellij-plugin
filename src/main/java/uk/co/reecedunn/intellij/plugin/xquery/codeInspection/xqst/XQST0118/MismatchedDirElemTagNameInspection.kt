/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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

package uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst.XQST0118

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle

/** XQST0118 error condition
 *
 * It is a *static error* if a direct element constructor has a close tag that
 * does not match the open tag (prefix and local name).
 */
class MismatchedDirElemTagNameInspection : Inspection("xqst/XQST0118.md") {
    private fun displayName(eqname: XsQNameValue): String {
        if (eqname.prefix == null)
            return eqname.localName!!.data
        return "${eqname.prefix!!.data}:${eqname.localName!!.data}"
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XQueryDirElemConstructor>().forEach { elem ->
            val openTag = elem.openTag!!
            val closeTag = elem.closeTag
            if (openTag.localName == null || closeTag?.localName == null) return@forEach

            if (openTag.prefix?.data != closeTag.prefix?.data || openTag.localName?.data != closeTag.localName?.data) {
                val description = XQueryBundle.message("inspection.XQST0118.mismatched-dir-elem-tag-name.message", displayName(closeTag), displayName(openTag))
                val context = closeTag.element!!
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
            }
        }
        return descriptors.toTypedArray()
    }
}
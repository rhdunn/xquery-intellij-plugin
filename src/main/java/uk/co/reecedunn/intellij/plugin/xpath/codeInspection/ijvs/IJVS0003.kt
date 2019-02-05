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
package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.core.lexer.EntityReferenceType
import uk.co.reecedunn.intellij.plugin.intellij.lang.Specification
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.intellij.resources.Resources
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryPluginBundle

class IJVS0003 : Inspection("ijvs/IJVS0003.md", Resources) {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val version: Specification = file.XQueryVersion.getVersionOrDefault(file.getProject())

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XQueryPredefinedEntityRef>().forEach { element ->
            val ref = element.entityRef
            when (ref.type) {
                EntityReferenceType.XmlEntityReference -> {}
                EntityReferenceType.Html4EntityReference -> {
                    if (version !== XQuerySpec.MARKLOGIC_0_9 && version !== XQuerySpec.MARKLOGIC_1_0) {
                        val description = XQueryPluginBundle.message("annotator.string-literal.html4-entity", ref.name)
                        descriptors.add(manager.createProblemDescriptor(element, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                EntityReferenceType.Html5EntityReference -> {
                    if (version !== XQuerySpec.MARKLOGIC_0_9 && version !== XQuerySpec.MARKLOGIC_1_0) {
                        val description = XQueryPluginBundle.message("annotator.string-literal.html5-entity", ref.name)
                        descriptors.add(manager.createProblemDescriptor(element, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                    }
                }
                else -> {
                    val description = XQueryPluginBundle.message("annotator.string-literal.unknown-xml-entity", ref.name)
                    descriptors.add(manager.createProblemDescriptor(element, description, null as LocalQuickFix?, ProblemHighlightType.ERROR, isOnTheFly))
                }
            }
        }
        return descriptors.toTypedArray()
    }
}

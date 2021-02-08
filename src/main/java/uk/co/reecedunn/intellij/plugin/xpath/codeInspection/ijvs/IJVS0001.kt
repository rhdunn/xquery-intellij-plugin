/*
 * Copyright (C) 2016-2018, 2020 Reece H. Dunn
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
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseX as BaseXProduct
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.existdb.lang.EXistDB as EXistDBProduct
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryPluginBundle
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformanceName
import uk.co.reecedunn.intellij.plugin.saxon.lang.SaxonEE
import uk.co.reecedunn.intellij.plugin.saxon.lang.SaxonHE
import uk.co.reecedunn.intellij.plugin.saxon.lang.SaxonPE
import uk.co.reecedunn.intellij.plugin.w3.lang.W3CSpecifications
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogic as MarkLogicProduct
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmInspectionDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidation

private fun supports(a: Specification, b: Version): Boolean {
    return when (a) {
        XQuerySpec.MARKLOGIC_0_9 ->
            b === XQuerySpec.MARKLOGIC_0_9 ||
                    b === XQuerySpec.WD_1_0_20030502
        XQuerySpec.MARKLOGIC_1_0 ->
            b.kind === MarkLogic ||
                    b === XQuerySpec.MARKLOGIC_1_0 ||
                    b === XQuerySpec.REC_1_0_20070123 ||
                    b === XQuerySpec.REC_3_0_20140408 ||
                    b === XQuerySpec.REC_3_1_20170321
        XQuerySpec.REC_1_0_20070123 ->
            b !== XQuerySpec.WD_1_0_20030502 &&
                    b !== XQuerySpec.MARKLOGIC_0_9 &&
                    a.value >= b.value
        else ->
            a.value >= b.value
    }
}

class IJVS0001 : Inspection("ijvs/IJVS0001.md", IJVS0001::class.java.classLoader) {
    private fun getProductVersion(product: Product, version: Version): XpmProductVersion = when (version) {
        BaseX.VERSION_6_1 -> BaseXProduct.VERSION_6_1
        BaseX.VERSION_7_7 -> BaseXProduct.VERSION_7_7
        BaseX.VERSION_7_8 -> BaseXProduct.VERSION_7_8
        BaseX.VERSION_8_4 -> BaseXProduct.VERSION_8_4
        BaseX.VERSION_8_5 -> BaseXProduct.VERSION_8_5
        BaseX.VERSION_8_6 -> BaseXProduct.VERSION_8_6
        BaseX.VERSION_9_0 -> BaseXProduct.VERSION_8_6
        BaseX.VERSION_9_1 -> BaseXProduct.VERSION_9_1
        EXistDB.VERSION_3_0 -> EXistDBProduct.VERSION_3_0
        EXistDB.VERSION_3_1 -> EXistDBProduct.VERSION_3_1
        EXistDB.VERSION_3_6 -> EXistDBProduct.VERSION_3_6
        EXistDB.VERSION_4_0 -> EXistDBProduct.VERSION_4_0
        EXistDB.VERSION_4_3 -> EXistDBProduct.VERSION_4_3
        MarkLogic.VERSION_4_0 -> MarkLogicProduct.VERSION_6
        MarkLogic.VERSION_5_0 -> MarkLogicProduct.VERSION_6
        MarkLogic.VERSION_6_0 -> MarkLogicProduct.VERSION_6
        MarkLogic.VERSION_7_0 -> MarkLogicProduct.VERSION_7
        MarkLogic.VERSION_8_0 -> MarkLogicProduct.VERSION_8
        MarkLogic.VERSION_9_0 -> MarkLogicProduct.VERSION_9
        MarkLogic.VERSION_10_0 -> MarkLogicProduct.VERSION_9
        Saxon.VERSION_9_4 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_9_6
            Saxon.PE -> SaxonPE.VERSION_9_4
            else -> SaxonEE.VERSION_9_4
        }
        Saxon.VERSION_9_5 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_9_6
            Saxon.PE -> SaxonPE.VERSION_9_5
            else -> SaxonEE.VERSION_9_5
        }
        Saxon.VERSION_9_6 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_9_6
            Saxon.PE -> SaxonPE.VERSION_9_5
            else -> SaxonEE.VERSION_9_4
        }
        Saxon.VERSION_9_7 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_9_7
            Saxon.PE -> SaxonPE.VERSION_9_7
            else -> SaxonEE.VERSION_9_7
        }
        Saxon.VERSION_9_8 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_9_7
            Saxon.PE -> SaxonPE.VERSION_9_8
            else -> SaxonEE.VERSION_9_8
        }
        Saxon.VERSION_9_9 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_9_7
            Saxon.PE -> SaxonPE.VERSION_9_9
            else -> SaxonEE.VERSION_9_9
        }
        Saxon.VERSION_10_0 -> when (product) {
            Saxon.HE -> SaxonHE.VERSION_10_0
            Saxon.PE -> SaxonPE.VERSION_10_0
            else -> SaxonEE.VERSION_10_0
        }
        else -> W3CSpecifications.REC
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.project)
        val product = settings.product
        val productVersion = settings.productVersion
        val xquery = file.XQueryVersion.getVersionOrDefault(file.project)

        val diagnostics = XpmInspectionDiagnostics(manager, isOnTheFly)
        file.walkTree().filterIsInstance<VersionConformance>().forEach { versioned ->
            val required = versioned.requiresConformance
            if (required.isEmpty()) return@forEach

            if (required.find { version -> product.conformsTo(productVersion, version) } == null) {
                val context = versioned.conformanceElement
                if (!context.textRange.isEmpty) {
                    val name = (versioned as? VersionConformanceName)?.conformanceName
                    val description =
                        if (name != null)
                            XQueryPluginBundle.message(
                                "inspection.XPST0003.unsupported-construct-with-name.message",
                                productVersion,
                                required.joinToString(", or "),
                                name
                            )
                        else
                            XQueryPluginBundle.message(
                                "inspection.XPST0003.unsupported-construct.message",
                                productVersion,
                                required.joinToString(", or ")
                            )
                    diagnostics.error(context, XpmDiagnostics.XPST0003, description)
                }
            } else {
                val requiredXQuery = required.filter { req -> req is Specification || req.kind === MarkLogic }
                if (requiredXQuery.isEmpty()) return@forEach

                if (requiredXQuery.find { version -> supports(xquery, version) } == null) {
                    val context = versioned.conformanceElement
                    if (!context.textRange.isEmpty) {
                        val description = XQueryPluginBundle.message(
                            "inspection.XPST0003.unsupported-construct-version.message",
                            xquery.versionId,
                            required.joinToString(", or ")
                        )
                        diagnostics.error(context, XpmDiagnostics.XPST0003, description)
                    }
                }
            }
        }

        val validator = XpmSyntaxValidation()
        validator.configuration = XpmLanguageConfiguration(getProductVersion(product, productVersion))
        validator.validate(file, diagnostics)

        return diagnostics.toTypedArray()
    }
}

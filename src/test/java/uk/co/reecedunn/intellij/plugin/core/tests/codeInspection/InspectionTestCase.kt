// Copyright (C) 2016-2018, 2020-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ex.InspectionManagerEx
import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.psi.SmartPointerManager
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseXSyntaxValidator
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockSmartPointerManager
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogicSyntaxValidator
import uk.co.reecedunn.intellij.plugin.saxon.lang.SaxonSyntaxValidator
import uk.co.reecedunn.intellij.plugin.w3.lang.XQuerySyntaxValidator
import uk.co.reecedunn.intellij.plugin.xijp.lang.XQueryIntelliJPluginSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

abstract class InspectionTestCase : ParsingTestCase<XQueryModule>(XQuery) {
    private val inspectionManager: InspectionManager
        get() = InspectionManager.getInstance(project)

    private val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(project)

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        project.registerServiceInstance(SmartPointerManager::class.java, MockSmartPointerManager())
        project.registerServiceInstance(InspectionManager::class.java, InspectionManagerEx(project))

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        XQueryProjectSettings().registerService(project)

        XpmSyntaxValidator.register(this, BaseXSyntaxValidator)
        XpmSyntaxValidator.register(this, MarkLogicSyntaxValidator)
        XpmSyntaxValidator.register(this, SaxonSyntaxValidator)
        XpmSyntaxValidator.register(this, XQueryIntelliJPluginSyntaxValidator)
        XpmSyntaxValidator.register(this, XQuerySyntaxValidator)

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
    }

    fun inspect(file: XQueryModule, inspection: LocalInspectionTool): List<ProblemDescriptor>? {
        return inspection.checkFile(file, inspectionManager as InspectionManagerEx, false)?.filterNotNull()
    }

    fun inspect(
        file: XQueryModule,
        inspection: LocalInspectionTool,
        configuration: XQueryProjectSettings.() -> Unit
    ): List<ProblemDescriptor>? = synchronized(settings) {
        settings.configuration()
        return inspect(file, inspection)
    }
}

/*
 * Copyright (C) 2016-2018, 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ex.InspectionManagerEx
import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.psi.SmartPointerManager
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseXSyntaxValidator
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
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

abstract class InspectionTestCase :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()) {

    private val inspectionManager: InspectionManager
        get() = InspectionManager.getInstance(project)

    protected val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(project)

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        project.registerServiceInstance(XQueryProjectSettings::class.java, XQueryProjectSettings())
        project.registerServiceInstance(SmartPointerManager::class.java, MockSmartPointerManager())
        project.registerServiceInstance(InspectionManager::class.java, InspectionManagerEx(project))

        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

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
}

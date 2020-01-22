/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import com.intellij.compat.openapi.vfs.impl.VirtualFileManagerImpl
import com.intellij.compat.semantic.SemServiceImpl
import com.intellij.compat.testFramework.registerFileBasedIndex
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileManagerListener
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.semantic.SemContributor
import com.intellij.semantic.SemContributorEP
import com.intellij.semantic.SemService
import com.intellij.util.xml.*
import com.intellij.util.xml.impl.DomApplicationComponent
import com.intellij.util.xml.impl.DomImplementationClassEP
import com.intellij.util.xml.impl.DomManagerImpl
import com.intellij.util.xml.impl.DomServiceImpl
import com.intellij.xml.XmlExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.reflection.newInstance
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.dom.XsltPackageDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.dom.XsltStylesheetDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.dom.XsltTransformDomFileDescription

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    @Suppress("DEPRECATION") // DomFileDescription.EP_NAME
    private fun registerDomApplicationComponent() {
        registerExtensionPoint("com.intellij.util.xml.impl.DomImplementationClassEP", "EP_NAME")
        registerExtensionPoint("com.intellij.util.xml.DomFileDescription", "EP_NAME")
        registerExtensionPoint("com.intellij.util.xml.impl.DomFileMetaData", "EP_NAME")

        registerExtension(DomFileDescription.EP_NAME, XsltStylesheetDomFileDescription)
        registerExtension(DomFileDescription.EP_NAME, XsltTransformDomFileDescription)
        registerExtension(DomFileDescription.EP_NAME, XsltPackageDomFileDescription)

        registerApplicationService(DomApplicationComponent::class.java, DomApplicationComponent())
    }

    private fun registerSemContributor(service: String) {
        val ep = SemContributorEP()
        ep.implementation = service
        // IntelliJ <= 2018.3 places SemContributor on the project.
        registerExtension(myProject, SemContributor.EP_NAME, ep)
        // IntelliJ >= 2019.1 places SemContributor on the application.
        registerExtension(SemContributor.EP_NAME, ep)
    }

    private fun registerVirtualFileManager() {
        registerExtensionPoint(
            ExtensionPointName("com.intellij.virtualFileManagerListener"),
            VirtualFileManagerListener::class.java
        )

        val bus = ApplicationManager.getApplication().messageBus
        registerApplicationService(VirtualFileManager::class.java, VirtualFileManagerImpl(arrayOf(), bus))
    }

    private fun registerDomManager() {
        // IntelliJ <= 2018.3 places SemContributor on the project.
        registerExtensionPoint(myProject, SemContributor.EP_NAME, SemContributorEP::class.java)
        // IntelliJ >= 2019.1 places SemContributor on the application.
        registerExtensionPoint(SemContributor.EP_NAME, SemContributorEP::class.java)

        registerApplicationService(SemService::class.java, SemServiceImpl(myProject))

        registerSemContributor("com.intellij.util.xml.impl.DomSemContributor")
        registerVirtualFileManager()
        myProject.registerService(DomManager::class.java, DomManagerImpl(myProject))
    }

    private fun registerConverterManager() {
        registerExtensionPoint(DomImplementationClassEP.CONVERTER_EP_NAME, DomImplementationClassEP::class.java)

        val converter = newInstance<ConverterManager>("com.intellij.util.xml.impl.ConverterManagerImpl")
        registerApplicationService(ConverterManager::class.java, converter)
    }

    @BeforeAll
    override fun setUp() {
        super.setUp()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        registerExtensionPoint(StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java)
        registerExtensionPoint(XmlExtension.EP_NAME, XmlExtension::class.java)

        myProject.registerService(ProjectRootManager::class.java, MockProjectRootsManager())
        myProject.registerService(ModuleManager::class.java, MockModuleManager(myProject))
        myProject.registerService(InjectedLanguageManager::class.java, MockInjectedLanguageManager())

        registerPomModel()
        registerApplicationService(DomService::class.java, DomServiceImpl())

        registerDomApplicationComponent()
        registerDomManager()
        registerConverterManager()
        registerFileBasedIndex()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    fun parseXml(resource: String): XmlFile {
        if (resource.endsWith(".xsl")) {
            val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
            return file.toPsiFile(myProject)!!
        }
        return super.parseText(resource)
    }

    fun element(resource: String, localName: String): List<XmlTag> {
        return parseXml(resource).walkTree().filterIsInstance<XmlTag>().filter { e ->
            e.namespace == XSLT.NAMESPACE && e.localName == localName
        }.filterNotNull().toList()
    }

    fun attribute(resource: String, elementName: String, attributeName: String): List<XmlAttributeValue> {
        return element(resource, elementName).mapNotNull { e ->
            e.getAttribute(attributeName, "")?.valueElement
        }
    }
}

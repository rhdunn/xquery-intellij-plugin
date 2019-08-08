/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.parser

import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.VirtualFileManagerImpl
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.semantic.SemContributor
import com.intellij.semantic.SemContributorEP
import com.intellij.semantic.SemService
import com.intellij.semantic.SemServiceImpl
import com.intellij.util.xml.DomFileDescription
import com.intellij.util.xml.DomManager
import com.intellij.util.xml.impl.DomApplicationComponent
import com.intellij.util.xml.impl.DomImplementationClassEP
import com.intellij.util.xml.impl.DomManagerImpl
import com.intellij.xml.XmlExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.dom.XslPackageDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.dom.XslStylesheetDomFileDescription
import uk.co.reecedunn.intellij.plugin.xslt.dom.XslTransformDomFileDescription

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    @Suppress("DEPRECATION") // DomFileDescription.EP_NAME
    private fun registerDomApplicationComponent() {
        registerExtensionPoint(DomImplementationClassEP.EP_NAME, DomImplementationClassEP::class.java)
        registerExtensionPoint("com.intellij.util.xml.DomFileDescription", "EP_NAME")
        registerExtensionPoint("com.intellij.util.xml.impl.DomFileMetaData", "EP_NAME")

        registerExtension(DomFileDescription.EP_NAME, XslStylesheetDomFileDescription)
        registerExtension(DomFileDescription.EP_NAME, XslTransformDomFileDescription)
        registerExtension(DomFileDescription.EP_NAME, XslPackageDomFileDescription)

        registerApplicationService(DomApplicationComponent::class.java, DomApplicationComponent())
    }

    private fun registerSemContributor(service: String) {
        val ep = SemContributorEP()
        ep.implementation = service
        registerExtension(SemContributor.EP_NAME, ep)
    }

    private fun registerDomManager() {
        registerExtensionPoint(SemContributor.EP_NAME, SemContributorEP::class.java)
        registerApplicationService(SemService::class.java, SemServiceImpl(myProject, PsiManager.getInstance(myProject)))

        registerSemContributor("com.intellij.util.xml.impl.DomSemContributor")

        registerApplicationService(VirtualFileManager::class.java, VirtualFileManagerImpl(mutableListOf()))
        myProject.registerService(DomManager::class.java, DomManagerImpl(myProject))
    }

    @BeforeAll
    override fun setUp() {
        super.setUp()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        registerExtensionPoint(StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java)
        registerExtensionPoint(XmlExtension.EP_NAME, XmlExtension::class.java)

        myProject.registerService(ProjectRootManager::class.java, MockProjectRootsManager())
        myProject.registerService(ModuleManager::class.java, MockModuleManager(myProject))

        registerDomApplicationComponent()
        registerDomManager()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    fun parseXml(resource: String): XmlFile {
        if (resource.endsWith(".xsl")) {
            val file = ResourceVirtualFile(ParserTestCase::class.java.classLoader, resource)
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

// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import com.intellij.compat.lang.xml.registerBasicXmlElementFactory
import com.intellij.lang.Language
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlExtension
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.XsltShadowPsiElementFactory

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase(language: Language) : ParsingTestCase<XmlFile>(language) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XmlASTFactory().registerExtension(project, XMLLanguage.INSTANCE)

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)

        app.registerBasicXmlElementFactory()

        project.registerServiceInstance(ProjectRootManager::class.java, MockProjectRootsManager())
        project.registerServiceInstance(ModuleManager::class.java, MockModuleManager(mockProject))
        project.registerServiceInstance(InjectedLanguageManager::class.java, MockInjectedLanguageManager())

        XpmShadowPsiElementFactory.register(this, XsltShadowPsiElementFactory)
    }

    fun parseXml(resource: String): XmlFile {
        if (resource.endsWith(".xsl")) {
            val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
            return file.toPsiFile(project) as XmlFile
        }
        return super.parseText(resource)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> parse(resource: String, namespaceUri: String, localName: String): List<T> {
        return parseXml(resource).walkTree()
            .filterIsInstance<XmlTag>()
            .filter { it.namespace == namespaceUri && it.localName == localName }
            .map { XpmShadowPsiElementFactory.create(it) as T }
            .toList()
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

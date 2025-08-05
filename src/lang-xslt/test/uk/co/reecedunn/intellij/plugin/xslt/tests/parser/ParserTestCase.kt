// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import com.intellij.lang.Language
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase(language: Language) : ParsingTestCase<XmlFile>(language) {
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

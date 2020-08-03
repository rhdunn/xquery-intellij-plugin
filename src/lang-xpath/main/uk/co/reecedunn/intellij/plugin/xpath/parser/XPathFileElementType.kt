/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.parser

import com.intellij.lang.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.isXsltFile
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath

class XPathFileElementType : IFileElementType(XPath) {
    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode? {
        val project = psi.project
        val languageForParser = getLanguageForParser(psi)
        val builder = PsiBuilderFactory.getInstance().createBuilder(
            project, chameleon, null, languageForParser, chameleon.chars
        )

        val definition = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser) as XPathParserDefinition
        val parser = definition.createParser(project, getParserContext(psi))
        val node = parser.parse(this, builder)
        return node.firstChildNode
    }

    private fun getParserContext(psi: PsiElement): XPathParserContext {
        // NOTE: Using InjectedLanguageManager#getInjectionHost returns null.
        val host = InjectedLanguageUtil.findInjectionHost(psi)
        val attr = host?.toXmlAttributeValue()?.attribute ?: return XPathParserContext.DEFAULT
        val schemaType = XsltSchemaType.create(attr.schemaType)
        if (schemaType != null) return XPathParserContext(schemaType)

        if (attr.isXsltFile && attr.attributeValueTemplateExpressions().isNotEmpty()) {
            return XPathParserContext.VALUE_TEMPLATE_EXPRESSION
        }
        return XPathParserContext.DEFAULT
    }
}

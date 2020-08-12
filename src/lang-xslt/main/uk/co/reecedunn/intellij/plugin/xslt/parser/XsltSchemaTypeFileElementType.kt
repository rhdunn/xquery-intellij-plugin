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
package uk.co.reecedunn.intellij.plugin.xslt.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.psi.tree.IFileElementType
import uk.co.reecedunn.intellij.plugin.core.lang.parserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XsltSchemaTypes

class XsltSchemaTypeFileElementType : IFileElementType(XsltSchemaTypes) {
    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode {
        // NOTE: Using InjectedLanguageManager#getInjectionHost returns null.
        val host = InjectedLanguageUtil.findInjectionHost(psi) ?: psi

        val languageForParser = getLanguageForParser(psi)
        val builder = PsiBuilderFactory.getInstance().createBuilder(
            psi.project, chameleon, null, languageForParser, chameleon.chars
        )

        val definition = languageForParser.parserDefinition as XsltSchemaTypesParserDefinition
        val parser = definition.createParser(XsltSchemaTypes.create(host) ?: XsltSchemaTypes.Expression)
        val node = parser.parse(this, builder)
        return node.firstChildNode
    }
}

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

import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XsltSchemaTypes
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.schema.XsltSchemaTypePsiImpl

class XsltSchemaTypesParserDefinition : XPathParserDefinition() {
    override fun createLexer(project: Project): Lexer = XPathLexer(XmlCodePointRangeImpl())

    override fun createParser(project: Project): PsiParser = createParser(XsltSchemaTypes.Expression)

    fun createParser(schemaType: ISchemaType): PsiParser = XsltSchemaTypesParser(schemaType)

    override fun getFileNodeType(): IFileElementType = XsltSchemaTypesElementType.SCHEMA_TYPE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = XsltSchemaTypePsiImpl(viewProvider)
}

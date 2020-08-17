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
package uk.co.reecedunn.intellij.plugin.xslt.parser.schema

import com.intellij.lang.Language
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.intellij.fileTypes.XsltSchemaTypeFileType
import uk.co.reecedunn.intellij.plugin.xslt.lexer.XsltAttributeValueTemplateLexer
import uk.co.reecedunn.intellij.plugin.xslt.parser.XsltSchemaTypesParser
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.schema.XsltSchemaTypePsiImpl

object XslAVT : Language(XPath, "xsl:avt"), ISchemaType {
    // region Language

    val FileType: LanguageFileType = XsltSchemaTypeFileType(this)

    override fun getAssociatedFileType(): LanguageFileType? = FileType

    // endregion
    // region ISchemaType

    override val type: String get() = id

    override val language: Language get() = this

    // endregion
    // region ParserDefinition

    val FileElementType: IFileElementType = IFileElementType(this)

    class ParserDefinition : XPathParserDefinition() {
        override fun createLexer(project: Project): Lexer = XsltAttributeValueTemplateLexer(XmlCodePointRangeImpl())

        override fun createParser(project: Project): PsiParser = XsltSchemaTypesParser(XslAVT)

        override fun getFileNodeType(): IFileElementType = FileElementType

        override fun createFile(viewProvider: FileViewProvider): PsiFile = XsltSchemaTypePsiImpl(viewProvider, FileType)
    }

    // endregion
}

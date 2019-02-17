/*
 * Copyright (C) 2016, 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.fileTypes

import com.intellij.lexer.Lexer
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.ByteSequence
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import javax.swing.Icon

object XQueryFileType : LanguageFileType(XQuery) {
    object Factory : FileTypeFactory() {
        override fun createFileTypes(consumer: FileTypeConsumer) {
            consumer.consume(XQueryFileType, XQueryFileType.EXTENSIONS)
        }
    }

    private val UTF_8 = Charset.forName("UTF-8")

    // xqu  -- BaseX
    // xql  -- eXist-db; BaseX
    // xqm  -- eXist-db; BaseX
    // xqws -- eXist-db
    const val EXTENSIONS = "xq;xqy;xquery;xqu;xql;xqm;xqws"

    override fun getName(): String = "XQuery"

    override fun getDescription(): String = XQueryBundle.message("xquery.files.filetype.description")

    override fun getDefaultExtension(): String = "xqy"

    override fun getIcon(): Icon? = XQueryIcons.FileType

    private fun matchToken(lexer: Lexer, type: IElementType): Boolean {
        val match = lexer.tokenType === type
        lexer.advance()
        return match
    }

    private fun matchWhiteSpaceOrComment(lexer: Lexer, required: Boolean): Boolean {
        var matched = false
        while (true) {
            matched = when {
                lexer.tokenType === XPathTokenType.WHITE_SPACE -> {
                    lexer.advance()
                    true
                }
                lexer.tokenType === XPathTokenType.COMMENT_START_TAG -> {
                    lexer.advance()
                    if (lexer.tokenType === XPathTokenType.COMMENT) {
                        lexer.advance()
                    }
                    if (lexer.tokenType === XPathTokenType.COMMENT_END_TAG) {
                        lexer.advance()
                    }
                    true
                }
                else -> return matched && required
            }
        }
    }

    private fun matchString(lexer: Lexer, defaultValue: String?): String? {
        if (lexer.tokenType !== XPathTokenType.STRING_LITERAL_START)
            return defaultValue
        lexer.advance()
        if (lexer.tokenType !== XPathTokenType.STRING_LITERAL_CONTENTS)
            return defaultValue
        val match = lexer.tokenText
        lexer.advance()
        if (lexer.tokenType !== XPathTokenType.STRING_LITERAL_END)
            return defaultValue
        lexer.advance()
        return match
    }

    private fun getXQueryEncoding(content: CharSequence): Charset {
        val lexer = XQueryLexer()
        lexer.start(content)
        matchWhiteSpaceOrComment(lexer, false)
        if (!matchToken(lexer, XQueryTokenType.K_XQUERY)) return UTF_8
        if (!matchWhiteSpaceOrComment(lexer, true)) return UTF_8
        if (!matchToken(lexer, XQueryTokenType.K_VERSION)) return UTF_8
        if (!matchWhiteSpaceOrComment(lexer, true)) return UTF_8
        if (matchString(lexer, null) == null) return UTF_8
        if (!matchWhiteSpaceOrComment(lexer, true)) return UTF_8
        if (!matchToken(lexer, XQueryTokenType.K_ENCODING)) return UTF_8
        if (!matchWhiteSpaceOrComment(lexer, true)) return UTF_8
        return try {
            Charset.forName(matchString(lexer, "utf-8")!!)
        } catch (e: UnsupportedCharsetException) {
            UTF_8
        }

    }

    override fun getCharset(file: VirtualFile, content: ByteArray): String? {
        return getXQueryEncoding(ByteSequence(content)).name()
    }

    override fun extractCharsetFromFileContent(project: Project?, file: VirtualFile?, content: CharSequence): Charset {
        return getXQueryEncoding(content)
    }
}

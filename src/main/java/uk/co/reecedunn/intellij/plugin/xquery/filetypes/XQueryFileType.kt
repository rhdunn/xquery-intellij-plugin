/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.filetypes

import com.intellij.lexer.Lexer
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.ByteSequence
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import javax.swing.Icon

private val UTF_8 = Charset.forName("UTF-8")

private val FILETYPE_ICON = IconLoader.getIcon("/icons/xquery.png")
private val FILETYPE_ICON_163 = IconLoader.getIcon("/icons/xquery-163.png")

class XQueryFileType private constructor() : LanguageFileType(XQuery) {
    override fun getName(): String = "XQuery"

    override fun getDescription(): String = XQueryBundle.message("xquery.files.filetype.description")

    override fun getDefaultExtension(): String = "xqy"

    override fun getIcon(): Icon? {
        return if (ApplicationInfo.getInstance().build.baselineVersion >= 163)
            FILETYPE_ICON_163
        else
            FILETYPE_ICON
    }

    private fun matchToken(lexer: Lexer, type: IElementType): Boolean {
        val match = lexer.tokenType === type
        lexer.advance()
        return match
    }

    private fun matchWhiteSpaceOrComment(lexer: Lexer, required: Boolean): Boolean {
        var matched = false
        while (true) {
            if (lexer.tokenType === XQueryTokenType.WHITE_SPACE) {
                lexer.advance()
                matched = true
            } else if (lexer.tokenType === XQueryTokenType.COMMENT_START_TAG) {
                lexer.advance()
                if (lexer.tokenType === XQueryTokenType.COMMENT) {
                    lexer.advance()
                }
                if (lexer.tokenType === XQueryTokenType.COMMENT_END_TAG) {
                    lexer.advance()
                }
                matched = true
            } else {
                return matched && required
            }
        }
    }

    private fun matchString(lexer: Lexer, defaultValue: String?): String? {
        if (lexer.tokenType !== XQueryTokenType.STRING_LITERAL_START)
            return defaultValue
        lexer.advance()
        if (lexer.tokenType !== XQueryTokenType.STRING_LITERAL_CONTENTS)
            return defaultValue
        val match = lexer.tokenText
        lexer.advance()
        if (lexer.tokenType !== XQueryTokenType.STRING_LITERAL_END)
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
        try {
            return Charset.forName(matchString(lexer, "utf-8")!!)
        } catch (e: UnsupportedCharsetException) {
            return UTF_8
        }

    }

    override fun getCharset(file: VirtualFile, content: ByteArray): String? {
        return getXQueryEncoding(ByteSequence(content)).name()
    }

    override fun extractCharsetFromFileContent(project: Project?, file: VirtualFile?, content: CharSequence): Charset {
        return getXQueryEncoding(content)
    }

    companion object {
        // xq;xqy;xquery -- standard defined extensions
        // xql           -- XQuery Language (main) file [eXist-db; BaseX]
        // xqm           -- XQuery Module file [eXist-db; BaseX]
        // xqu           -- XQuery file [BaseX]
        // xqws          -- XQuery Web Service [eXist-db]
        const val EXTENSIONS = "xq;xqy;xquery;xql;xqm;xqu;xqws"

        val INSTANCE = XQueryFileType()
    }
}

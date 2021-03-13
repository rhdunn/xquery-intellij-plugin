/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xproc.lang

import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.tree.IFileElementType

object XProc : XMLLanguage(INSTANCE, "XProc", "application/xproc+xml") {
    // region Language

    const val NAMESPACE: String = "http://www.w3.org/ns/xproc"

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = "XProc"

    override fun getAssociatedFileType(): LanguageFileType? = null

    // endregion
    // region Parser Definition

    val FileElementType: IFileElementType = IFileElementType(this)

    class ParserDefinition : XMLParserDefinition() {
        override fun getFileNodeType(): IFileElementType = FileElementType

        override fun createFile(viewProvider: FileViewProvider): PsiFile {
            return XmlFileImpl(viewProvider, FileElementType)
        }
    }

    // endregion
}

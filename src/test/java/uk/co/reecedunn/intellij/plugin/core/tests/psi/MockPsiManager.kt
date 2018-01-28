/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.lang.LanguageUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import org.apache.xmlbeans.impl.common.IOUtil

import java.io.*
import java.nio.charset.Charset

class MockPsiManager(project: Project) : com.intellij.mock.MockPsiManager(project) {
    @Throws(IOException::class)
    private fun streamToString(stream: InputStream, charset: Charset): String {
        val writer = ByteArrayOutputStream()
        IOUtil.copyCompletely(stream, writer)
        return String(writer.toByteArray(), charset)
    }

    override fun findFile(file: VirtualFile): PsiFile? {
        try {
            val language = LanguageUtil.getLanguageForPsi(project, file) ?: return null

            val content = streamToString(file.inputStream, file.charset)
            return PsiFileFactory.getInstance(project).createFileFromText(file.name, language, content, true, false, false, file)
        } catch (e: IOException) {
            return null
        }
    }
}

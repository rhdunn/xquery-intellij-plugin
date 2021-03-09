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
package uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml

import com.intellij.codeHighlighting.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class XQueryElemTagTreeHighlightingPassFactory :
    TextEditorHighlightingPassFactory,
    TextEditorHighlightingPassFactoryRegistrar {

    override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
        registrar.registerTextEditorHighlightingPass(this, intArrayOf(Pass.UPDATE_ALL), null, false, -1)
    }

    override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? = when {
        editor.isOneLineMode -> null
        editor !is EditorEx -> null
        !isTagTreeHighlightingActive(file) -> null
        else -> XQueryElemTagTreeHighlightingPass(file, editor)
    }
}

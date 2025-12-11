// Copyright (C) 2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

class XPathTypedHandler : TypedHandlerDelegate() {
    override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is XPath) return Result.CONTINUE

        if (charTyped == '$' || charTyped == ':') {
            AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
            return Result.CONTINUE
        }
        return Result.CONTINUE
    }
}

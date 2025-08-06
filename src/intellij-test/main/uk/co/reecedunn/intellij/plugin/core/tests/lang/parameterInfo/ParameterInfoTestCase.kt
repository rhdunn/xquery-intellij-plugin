// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo

import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.psi.PsiFile
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.core.editor.editor
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parseText

interface ParameterInfoTestCase<File : PsiFile> : LanguageParserTestCase<File> {
    fun createParameterInfoContext(text: String, offset: Int): CreateParameterInfoContext {
        val file = parseText<File>(text)
        val editor = file.editor
        editor.caretModel.moveToOffset(offset)
        return MockCreateParameterInfoContext(editor, file)
    }

    fun updateParameterInfoContext(text: String, offset: Int): UpdateParameterInfoContext {
        val file = parseText<File>(text)
        val editor = file.editor
        editor.caretModel.moveToOffset(offset)
        return MockUpdateParameterInfoContext(editor, file)
    }
}

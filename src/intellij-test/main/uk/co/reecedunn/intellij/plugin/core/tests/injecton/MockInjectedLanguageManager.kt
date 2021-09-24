/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.injecton

import com.intellij.compat.lang.injection.InjectedLanguageManager
import com.intellij.injected.editor.DocumentWindow
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLanguageInjectionHost

class MockInjectedLanguageManager : InjectedLanguageManager() {
    override fun mightHaveInjectedFragmentAtOffset(hostDocument: Document, hostOffset: Int): Boolean = TODO()

    override fun enumerate(host: PsiElement, visitor: PsiLanguageInjectionHost.InjectedPsiVisitor): Unit = TODO()

    override fun getTopLevelFile(element: PsiElement): PsiFile = element.containingFile

    override fun getUnescapedText(injectedNode: PsiElement): String = TODO()

    override fun getInjectionHost(injectedProvider: FileViewProvider): PsiLanguageInjectionHost = TODO()

    override fun getInjectionHost(injectedElement: PsiElement): PsiLanguageInjectionHost = TODO()

    override fun getInjectedPsiFiles(host: PsiElement): MutableList<Pair<PsiElement, TextRange>> = TODO()

    override fun enumerateEx(
        host: PsiElement,
        containingFile: PsiFile,
        probeUp: Boolean,
        visitor: PsiLanguageInjectionHost.InjectedPsiVisitor
    ) {
        TODO()
    }

    override fun getCachedInjectedDocumentsInRange(
        hostPsiFile: PsiFile,
        range: TextRange
    ): MutableList<DocumentWindow> {
        TODO()
    }

    override fun dropFileCaches(file: PsiFile): Unit = TODO()

    override fun getNonEditableFragments(window: DocumentWindow): MutableList<TextRange> = TODO()

    override fun registerMultiHostInjector(injector: MultiHostInjector, parentDisposable: Disposable): Unit = TODO()

    override fun isInjectedFragment(injectedFile: PsiFile): Boolean = TODO()

    override fun intersectWithAllEditableFragments(
        injectedPsi: PsiFile,
        rangeToEdit: TextRange
    ): MutableList<TextRange> {
        TODO()
    }

    override fun freezeWindow(document: DocumentWindow): DocumentWindow = TODO()

    override fun findInjectedElementAt(hostFile: PsiFile, hostDocumentOffset: Int): PsiElement = TODO()

    override fun injectedToHost(injectedContext: PsiElement, injectedTextRange: TextRange): TextRange = TODO()

    override fun injectedToHost(injectedContext: PsiElement, injectedOffset: Int): Int = TODO()

    override fun injectedToHost(injectedContext: PsiElement, injectedOffset: Int, minHostOffset: Boolean): Int = TODO()
}

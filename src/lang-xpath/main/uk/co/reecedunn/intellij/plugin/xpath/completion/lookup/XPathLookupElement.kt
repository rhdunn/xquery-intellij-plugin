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
package uk.co.reecedunn.intellij.plugin.xpath.completion.lookup

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.psi.PsiElement

open class XPathLookupElement(lookupString: String) : LookupElement() {
    private val lookupStrings: MutableSet<String> = mutableSetOf(lookupString)
    override fun getLookupString(): String = lookupStrings.first()
    override fun getAllLookupStrings(): MutableSet<String> = lookupStrings

    override fun getPsiElement(): PsiElement? = null
    override fun isValid(): Boolean = psiElement?.isValid ?: true

    protected val presentation: LookupElementPresentation = LookupElementPresentation()

    init {
        presentation.itemText = lookupString
    }

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.copyFrom(this.presentation)
    }

    open val insertText: XPathInsertText? = null

    override fun handleInsert(context: InsertionContext) {
        val insert = insertText ?: return
        insert.completeText(context.document, context.tailOffset)
        insert.moveCaret(context.editor.caretModel)

        if (insert === XPathInsertText.QNAME_PREFIX) {
            // Keep the auto-complete popup visible, so namespaced variables
            // and functions can be auto-completed.
            AutoPopupController.getInstance(context.project).autoPopupMemberLookup(context.editor, null)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is XPathLookupElement) return false
        return `object` === other.`object` && lookupString == other.lookupString
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + lookupString.hashCode()
        result = 31 * result + if (`object` === this) 0 else `object`.hashCode()
        return result
    }
}

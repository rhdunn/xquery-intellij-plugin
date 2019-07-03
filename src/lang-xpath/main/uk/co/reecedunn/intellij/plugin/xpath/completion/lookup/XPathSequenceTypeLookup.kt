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

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xpath.completion.XPathEmptyFunctionInsertHandler

class XPathSequenceTypeLookup(kindTest: String, tailText: String = "()") : LookupElement() {
    private val lookupStrings: MutableSet<String> = mutableSetOf(kindTest)
    override fun getLookupString(): String = lookupStrings.first()
    override fun getAllLookupStrings(): MutableSet<String> = lookupStrings

    override fun getObject(): Any = this
    override fun getPsiElement(): PsiElement? = null
    override fun isValid(): Boolean = true

    private val presentation = LookupElementPresentation()
    init {
        presentation.itemText = lookupString
        presentation.isItemTextBold = true
        presentation.tailText = tailText
    }

    override fun renderElement(presentation: LookupElementPresentation?) {
        presentation?.copyFrom(this.presentation)
    }

    override fun handleInsert(context: InsertionContext) {
        XPathEmptyFunctionInsertHandler.handleInsert(context, this)
    }

    val caretOffset: Int = if (tailText == "()") 2 else 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is XPathSequenceTypeLookup) return false
        return lookupString == other.lookupString
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + lookupString.hashCode()
        result = 31 * result + presentation.tailText!!.hashCode()
        return result
    }
}

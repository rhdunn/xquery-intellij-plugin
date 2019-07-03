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
package uk.co.reecedunn.intellij.plugin.xquery.completion.lookup

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.completion.XPathEmptyFunctionInsertHandler
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration

class XQueryFunctionCallLookup(localName: String, prefix: String?, private val function: XPathFunctionDeclaration) :
    LookupElement() {

    private val lookupStrings: MutableSet<String> = mutableSetOf(prefix?.let { "$it:$localName" } ?: localName)
    override fun getLookupString(): String = lookupStrings.first()
    override fun getAllLookupStrings(): MutableSet<String> = lookupStrings

    override fun getObject(): Any = function
    override fun getPsiElement(): PsiElement? = function.functionName?.element
    override fun isValid(): Boolean = psiElement?.isValid ?: true

    override fun handleInsert(context: InsertionContext) {
        XPathEmptyFunctionInsertHandler.handleInsert(context, this)
    }

    private val presentation = LookupElementPresentation()
    init {
        val params = function.children().filterIsInstance<XPathParamList>().firstOrNull()
        presentation.itemText = lookupString
        presentation.icon = XQueryIcons.Nodes.FunctionDecl
        presentation.tailText = params?.presentation?.presentableText ?: "()"
        presentation.typeText = function.returnType?.typeName
    }

    override fun renderElement(presentation: LookupElementPresentation?) {
        presentation?.copyFrom(this.presentation)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is XQueryFunctionCallLookup) return false
        return lookupString == other.lookupString && function === other.function
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + lookupString.hashCode()
        result = 31 * result + `object`.hashCode()
        return result
    }
}

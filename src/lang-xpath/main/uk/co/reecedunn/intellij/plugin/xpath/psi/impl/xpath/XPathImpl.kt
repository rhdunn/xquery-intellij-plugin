/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext

class XPathImpl(provider: FileViewProvider) :
    PsiFileBase(provider, XPath),
    uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath,
    XpmStaticContext {
    // region Object

    override fun toString(): String = "XPath(" + containingFile.name + ")"

    // endregion
    // region PsiFile

    override fun getFileType(): FileType = XPathFileType

    // endregion
    // region XstContext

    override fun getUsageType(element: PsiElement): XpmUsageType? {
        val parentType = element.parent.elementType
        return when {
            parentType === XPathElementType.NAMESPACE_DECLARATION -> {
                if ((element as? XsQNameValue)?.prefix?.data == "xmlns")
                    XpmUsageType.Namespace
                else
                    XpmUsageType.Attribute
            }
            else -> USAGE_TYPES[parentType]
        }
    }

    override fun expandQName(qname: XsQNameValue): Sequence<XsQNameValue> = emptySequence()

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement? = null

    // endregion

    companion object {
        val USAGE_TYPES: Map<IElementType, XpmUsageType> = mapOf(
            XPathElementType.ARROW_FUNCTION_CALL to XpmUsageType.FunctionRef,
            XPathElementType.ATOMIC_OR_UNION_TYPE to XpmUsageType.Type,
            XPathElementType.ATTRIBUTE_TEST to XpmUsageType.Attribute,
            XPathElementType.ELEMENT_TEST to XpmUsageType.Element,
            XPathElementType.FT_SCORE_VAR to XpmUsageType.Variable,
            XPathElementType.FUNCTION_CALL to XpmUsageType.FunctionRef,
            XPathElementType.KEYWORD_ARGUMENT to XpmUsageType.Parameter,
            XPathElementType.NAMED_FUNCTION_REF to XpmUsageType.FunctionRef,
            XPathElementType.PARAM to XpmUsageType.Parameter,
            XPathElementType.PRAGMA to XpmUsageType.Pragma,
            XPathElementType.QUANTIFIER_BINDING to XpmUsageType.Variable,
            XPathElementType.SCHEMA_ATTRIBUTE_TEST to XpmUsageType.Attribute,
            XPathElementType.SCHEMA_ELEMENT_TEST to XpmUsageType.Element,
            XPathElementType.SIMPLE_FOR_BINDING to XpmUsageType.Variable,
            XPathElementType.SIMPLE_LET_BINDING to XpmUsageType.Variable,
            XPathElementType.SIMPLE_TYPE_NAME to XpmUsageType.Type,
            XPathElementType.TYPE_ALIAS to XpmUsageType.Type,
            XPathElementType.TYPE_NAME to XpmUsageType.Type,
            XPathElementType.LOCAL_UNION_TYPE to XpmUsageType.Type,
            XPathElementType.VAR_REF to XpmUsageType.Variable
        )
    }
}

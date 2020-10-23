/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xpm.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableDefinition
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

class XPathImpl(provider: FileViewProvider) :
    PsiFileBase(provider, XPath),
    uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath,
    XPathStaticContext {
    // region Object

    override fun toString(): String = "XPath(" + containingFile.name + ")"

    // endregion
    // region PsiFile

    override fun getFileType(): FileType = XPathFileType

    // endregion
    // region XstContext

    override fun getUsageType(element: PsiElement): XstUsageType? = USAGE_TYPES[element.parent.elementType]

    override fun expandQName(qname: XsQNameValue): Sequence<XsQNameValue> = emptySequence()

    // endregion
    // region XPathStaticContext

    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XdmNamespaceDeclaration> {
        return context.staticallyKnownXPathNamespaces()
    }

    override fun defaultNamespace(
        context: PsiElement,
        type: XdmNamespaceType
    ): Sequence<XdmDefaultNamespaceDeclaration> = when (type) {
        XdmNamespaceType.DefaultElementOrType -> context.defaultElementOrTypeXPathNamespace()
        XdmNamespaceType.DefaultFunctionDecl -> context.defaultFunctionXPathNamespace()
        XdmNamespaceType.DefaultFunctionRef -> context.defaultFunctionXPathNamespace()
        else -> emptySequence()
    }

    override fun staticallyKnownFunctions(): Sequence<XpmFunctionDeclaration?> {
        return emptySequence() // TODO
    }

    override fun staticallyKnownFunctions(eqname: XPathEQName): Sequence<XpmFunctionDeclaration> {
        return emptySequence() // TODO
    }

    override fun inScopeVariables(context: PsiElement): Sequence<XdmVariableDefinition> {
        return emptySequence() // TODO
    }

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement? = null

    // endregion

    companion object {
        val USAGE_TYPES: Map<IElementType, XstUsageType> = mapOf(
            XPathElementType.ARROW_FUNCTION_CALL to XstUsageType.FunctionRef,
            XPathElementType.ATOMIC_OR_UNION_TYPE to XstUsageType.Type,
            XPathElementType.ATTRIBUTE_TEST to XstUsageType.Attribute,
            XPathElementType.ELEMENT_TEST to XstUsageType.Element,
            XPathElementType.FUNCTION_CALL to XstUsageType.FunctionRef,
            XPathElementType.NAMED_FUNCTION_REF to XstUsageType.FunctionRef,
            XPathElementType.PARAM to XstUsageType.Parameter,
            XPathElementType.PRAGMA to XstUsageType.Pragma,
            XPathElementType.SCHEMA_ATTRIBUTE_TEST to XstUsageType.Attribute,
            XPathElementType.SCHEMA_ELEMENT_TEST to XstUsageType.Element,
            XPathElementType.SIMPLE_TYPE_NAME to XstUsageType.Type,
            XPathElementType.TYPE_ALIAS to XstUsageType.Type,
            XPathElementType.TYPE_NAME to XstUsageType.Type,
            XPathElementType.UNION_TYPE to XstUsageType.Type,
            XPathElementType.VAR_NAME to XstUsageType.Variable
        )
    }
}

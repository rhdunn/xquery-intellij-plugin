/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathStaticContext

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
    // region XPathStaticContext

    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XPathNamespaceDeclaration> {
        return emptySequence() // TODO
    }

    override fun staticallyKnownFunctions(): Sequence<XPathFunctionDeclaration?> {
        return emptySequence() // TODO
    }

    override fun staticallyKnownFunctions(eqname: XPathEQName): Sequence<XPathFunctionDeclaration> {
        return emptySequence() // TODO
    }

    // endregion
}

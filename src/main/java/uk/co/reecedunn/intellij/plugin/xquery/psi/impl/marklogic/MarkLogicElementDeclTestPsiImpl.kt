/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.marklogic

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.marklogic.MarkLogicElementDeclTest
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class MarkLogicElementDeclTestPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), MarkLogicElementDeclTest, XQueryConformanceCheck {
    override fun conformsTo(implementation: ImplementationItem): Boolean =
        implementation.getVersion(XQueryConformance.MARKLOGIC).supportsVersion(XQueryVersion.VERSION_7_0)

    override fun getConformanceElement(): PsiElement =
        firstChild

    override fun getConformanceErrorMessage(): String =
        XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_7_0)
}
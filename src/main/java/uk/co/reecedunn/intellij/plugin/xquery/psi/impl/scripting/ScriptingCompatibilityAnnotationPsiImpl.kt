/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.scripting

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.lang.Scripting
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class ScriptingCompatibilityAnnotationPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), ScriptingCompatibilityAnnotation, XQueryConformance {
    override val requiresConformance get(): List<Version> = listOf(Scripting.NOTE_1_0_20140918)

    override val conformanceElement get(): PsiElement =
            firstChild
}

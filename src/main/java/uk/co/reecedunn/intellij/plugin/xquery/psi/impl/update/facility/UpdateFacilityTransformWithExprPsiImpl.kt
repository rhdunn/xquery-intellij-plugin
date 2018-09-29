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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityTransformWithExpr
import uk.co.reecedunn.intellij.plugin.intellij.lang.BaseX
import uk.co.reecedunn.intellij.plugin.intellij.lang.UpdateFacility
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class UpdateFacilityTransformWithExprPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), UpdateFacilityTransformWithExpr, XQueryConformance {
    override val requiresConformance get(): List<Version> = listOf(
        UpdateFacility.NOTE_3_0_20170124,
        BaseX.VERSION_8_5)

    override val conformanceElement get(): PsiElement =
        findChildByType(XQueryTokenType.K_TRANSFORM) ?: firstChild
}

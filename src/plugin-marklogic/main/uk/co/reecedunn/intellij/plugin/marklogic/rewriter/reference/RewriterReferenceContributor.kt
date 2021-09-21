/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.reference

import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.Rewriter
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference

class RewriterReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val rewriter = XmlPatterns.xmlTag().withNamespace(Rewriter.NAMESPACE)

        val dispatch = rewriter.withLocalName("dispatch").withoutAttributeValue("xdbc", "true")
        registrar.registerReferenceProvider(dispatch, ModuleUriReference)

        val setPath = rewriter.withLocalName("set-path")
        registrar.registerReferenceProvider(setPath, ModuleUriReference)

        val setErrorHandler = rewriter.withLocalName("set-error-handler")
        registrar.registerReferenceProvider(setErrorHandler, ModuleUriReference)
    }
}

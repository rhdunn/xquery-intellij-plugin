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
package uk.co.reecedunn.intellij.plugin.xquery.completion

import com.intellij.patterns.PlatformPatterns
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionContributorEx
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpath.completion.filters.*
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.completion.property.*
import uk.co.reecedunn.intellij.plugin.xquery.completion.providers.XQueryForwardOrReverseAxisProvider
import uk.co.reecedunn.intellij.plugin.xquery.completion.providers.XQueryKindTestProvider

class XQueryCompletionContributor : CompletionContributorEx() {
    init {
        val XQuery = PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(XQueryModule::class.java))

        // XQuery 3.1 EBNF (113) ForwardAxis ; XQuery 3.1 EBNF (116) ReverseAxis
        builder(XQuery).withFilter(XPathForwardOrReverseAxisFilter)
            .withProperty(XQueryVersion).withProperty(XQueryProductVersion)
            .addCompletions(XQueryForwardOrReverseAxisProvider)

        // XQuery 3.1 EBNF (184) SequenceType
        builder(XQuery).withFilter(XPathSequenceTypeFilter).withProperty(XPathVersion)
            .addCompletions(XPathSequenceTypeProvider)

        // XQuery 3.1 EBNF (186) ItemType
        builder(XQuery).withFilter(XPathItemTypeFilter).withProperty(XPathVersion).addCompletions(XPathItemTypeProvider)

        // XQuery 3.1 EBNF (187) AtomicOrUnionType ; XQuery 3.1 EBNF (205) SimpleTypeName
        builder(XQuery).withFilter(XPathAtomicOrUnionTypeFilter)
            .withProperty(XQueryProduct).withProperty(XQueryProductVersion)
            .withProperty(XQueryStaticallyKnownNamespaces)
            .addCompletions(XPathAtomicOrUnionTypeProvider)

        // XQuery 3.1 EBNF (188) KindTest
        builder(XQuery).withFilter(XPathKindTestFilter)
            .withProperty(XPathVersion).withProperty(XQueryProductVersion)
            .addCompletions(XQueryKindTestProvider).addCompletions(XPathKindTestProvider)

        // XQuery 3.1 EBNF (234) QName
        builder(XQuery).withFilter(XPathQNamePrefixFilter)
            .withProperty(XQueryStaticallyKnownNamespaces)
            .addCompletions(XPathQNamePrefixProvider)
    }
}

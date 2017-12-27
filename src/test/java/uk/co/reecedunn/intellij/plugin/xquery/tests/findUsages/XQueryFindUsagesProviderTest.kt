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
package uk.co.reecedunn.intellij.plugin.xquery.tests.findUsages

import com.intellij.lang.HelpID
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarRef
import uk.co.reecedunn.intellij.plugin.xquery.findUsages.XQueryFindUsagesProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryFindUsagesProviderTest : ParserTestCase() {
    fun testNCName() {
        val provider = XQueryFindUsagesProvider()

        val file = parseResource("tests/parser/xquery-1.0/VarRef.xq")!!
        val varRef = file.descendants().filterIsInstance<XQueryVarRef>().first()
        val varName = varRef.children().filterIsInstance<XPathVarName>().first()
        val qname = varName.children().filterIsInstance<XPathQName>().first()
        val ncName = qname.children().filterIsInstance<XPathNCName>().first()

        assertThat(provider.canFindUsagesFor(ncName), `is`(true))
        assertThat(provider.getHelpId(ncName), `is`(HelpID.FIND_OTHER_USAGES))
        assertThat(provider.getType(ncName), `is`("Identifier"))
        assertThat(provider.getDescriptiveName(ncName), `is`("one"))
        assertThat(provider.getNodeText(ncName, true), `is`("one"))
        assertThat(provider.getNodeText(ncName, false), `is`("one"))
    }
}

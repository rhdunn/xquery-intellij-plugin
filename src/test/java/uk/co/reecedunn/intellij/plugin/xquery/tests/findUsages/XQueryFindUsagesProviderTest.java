/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.findUsages;

import com.intellij.lang.HelpID;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNCName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarRef;
import uk.co.reecedunn.intellij.plugin.xquery.findUsages.XQueryFindUsagesProvider;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.reecedunn.intellij.plugin.xquery.functional.PsiTreeWalker.children;
import static uk.co.reecedunn.intellij.plugin.xquery.functional.PsiTreeWalker.descendants;

public class XQueryFindUsagesProviderTest extends ParserTestCase {
    public void testNCName() {
        final XQueryFindUsagesProvider provider = new XQueryFindUsagesProvider();

        XQueryFile file = parseResource("tests/parser/xquery-1.0/VarRef.xq");
        XQueryVarRef varRef = descendants(file).findFirst(XQueryVarRef.class).get();
        XQueryVarName varName = children(varRef).findFirst(XQueryVarName.class).get();
        XQueryNCName ncName = children(varName).findFirst(XQueryNCName.class).get();

        assertThat(provider.canFindUsagesFor(ncName), is(true));
        assertThat(provider.getHelpId(ncName), is(HelpID.FIND_OTHER_USAGES));
        assertThat(provider.getType(ncName), is(""));
        assertThat(provider.getDescriptiveName(ncName), is("one"));
        assertThat(provider.getNodeText(ncName, true), is("one"));
        assertThat(provider.getNodeText(ncName, false), is("one"));
    }
}

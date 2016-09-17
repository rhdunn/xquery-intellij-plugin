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
package uk.co.reecedunn.intellij.plugin.xquery.tests.reference;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class XQueryReferenceTest extends ParserTestCase {
    // region XQuery 1.0 :: URILiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-URILiteral")
    public void testURILiteral_HttpUri() {
        final ASTNode node = parseResource("tests/resolve/xquery-1.0/ModuleImport_URILiteral_SameDirectory.xq");

        XQueryModuleImport moduleImportPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryModuleImport.class);
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = PsiNavigation.findChildrenByClass(moduleImportPsi, XQueryUriLiteral.class);
        assertThat(uriLiterals.size(), is(2));

        PsiReference httpUriRef = uriLiterals.get(0).getReference();
        assertThat(httpUriRef.getCanonicalText(), is("http://example.com/test"));
        assertThat(httpUriRef.getVariants().length, is(0));

        PsiElement resolved = httpUriRef.resolve();
        assertThat(resolved, is(nullValue()));
    }

    // endregion
}

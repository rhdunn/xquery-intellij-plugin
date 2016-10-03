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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryASTFactoryTest extends TestCase {
    @SuppressWarnings("ConstantConditions")
    public void testCreateElement() {
        ASTFactory factory = new XQueryASTFactory();
        assertThat(factory.createComposite(XQueryTokenType.INTEGER_LITERAL).getClass().getName(), is(CompositeElement.class.getName()));
    }

    @SuppressWarnings("ConstantConditions")
    public void testCreateLeaf() {
        ASTFactory factory = new XQueryASTFactory();
        assertThat(factory.createLeaf(XQueryTokenType.COMMA, ",").getClass().getName(), is(LeafPsiElement.class.getName()));
    }
}

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
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

public class XQueryASTFactoryTest extends TestCase {
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testCreateElement() {
        ASTFactory factory = new XQueryASTFactory();

        // foreign ASTNode
        AssertionError e = expectThrows(AssertionError.class, () -> factory.createComposite(XQueryTokenType.INTEGER_LITERAL));
        assertThat(e.getMessage(), is("Alien element type [XQUERY_INTEGER_LITERAL_TOKEN]. Can't create XQuery AST Node for that."));
    }

    public void testCreateLeaf() {
        ASTFactory factory = new XQueryASTFactory();
        assertThat(factory.createLeaf(XQueryTokenType.COMMA, ",").getClass().getName(), is(LeafPsiElement.class.getName()));
    }
}

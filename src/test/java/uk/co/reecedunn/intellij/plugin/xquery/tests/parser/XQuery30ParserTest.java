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

import com.intellij.lang.ASTNode;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuery30ParserTest extends XQuery10ParserTest {
    // region Initialization

    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setXQueryVersion(XQueryVersion.XQUERY_3_0);
    }

    // endregion
    // region VersionDecl (XQuery 3.0)

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingEncodingString() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VersionDecl")
    public void testVersionDecl_EncodingOnly_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}

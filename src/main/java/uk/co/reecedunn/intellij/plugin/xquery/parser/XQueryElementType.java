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
package uk.co.reecedunn.intellij.plugin.xquery.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;

public interface XQueryElementType {
    IFileElementType FILE = new IFileElementType(XQuery.INSTANCE);

    IElementType CDATA_SECTION = new IElementType("XQUERY_CDATA_SECTION", XQuery.INSTANCE);
    IElementType COMMENT = new IElementType("XQUERY_COMMENT", XQuery.INSTANCE);
    IElementType DIR_COMMENT_CONSTRUCTOR = new IElementType("XQUERY_DIR_COMMENT_CONSTRUCTOR", XQuery.INSTANCE);
    IElementType QNAME = new IElementType("XQUERY_QNAME", XQuery.INSTANCE);
    IElementType STRING_LITERAL = new IElementType("XQUERY_STRING_LITERAL", XQuery.INSTANCE);
    IElementType VERSION_DECL = new IElementType("XQUERY_VERSION_DECL", XQuery.INSTANCE);
}

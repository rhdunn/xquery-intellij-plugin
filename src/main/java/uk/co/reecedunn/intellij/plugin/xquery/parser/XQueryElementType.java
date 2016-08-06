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
    IElementType NCNAME = new IElementType("XQUERY_NCNAME", XQuery.INSTANCE);
    IElementType QNAME = new IElementType("XQUERY_QNAME", XQuery.INSTANCE);
    IElementType LITERAL = new IElementType("XQUERY_LITERAL", XQuery.INSTANCE);
    IElementType STRING_LITERAL = new IElementType("XQUERY_STRING_LITERAL", XQuery.INSTANCE);
    IElementType URI_LITERAL = new IElementType("XQUERY_URI_LITERAL", XQuery.INSTANCE);

    IElementType FILTER_EXPR = new IElementType("XQUERY_FILTER_EXPR", XQuery.INSTANCE);
    IElementType RELATIVE_PATH_EXPR = new IElementType("XQUERY_RELATIVE_PATH_EXPR", XQuery.INSTANCE);
    IElementType PATH_EXPR = new IElementType("XQUERY_PATH_EXPR", XQuery.INSTANCE);
    IElementType UNARY_EXPR = new IElementType("XQUERY_UNARY_EXPR", XQuery.INSTANCE);
    IElementType CAST_EXPR = new IElementType("XQUERY_CAST_EXPR", XQuery.INSTANCE);
    IElementType CASTABLE_EXPR = new IElementType("XQUERY_CASTABLE_EXPR", XQuery.INSTANCE);
    IElementType TREAT_EXPR = new IElementType("XQUERY_TREAT_EXPR", XQuery.INSTANCE);
    IElementType INSTANCEOF_EXPR = new IElementType("XQUERY_INSTANCEOF_EXPR", XQuery.INSTANCE);
    IElementType INTERSECT_EXCEPT_EXPR = new IElementType("XQUERY_INTERSECT_EXCEPT_EXPR", XQuery.INSTANCE);
    IElementType UNION_EXPR = new IElementType("XQUERY_UNION_EXPR", XQuery.INSTANCE);
    IElementType MULTIPLICATIVE_EXPR = new IElementType("XQUERY_MULTIPLICATIVE_EXPR", XQuery.INSTANCE);
    IElementType ADDITIVE_EXPR = new IElementType("XQUERY_ADDITIVE_EXPR", XQuery.INSTANCE);
    IElementType RANGE_EXPR = new IElementType("XQUERY_RANGE_EXPR", XQuery.INSTANCE);
    IElementType COMPARISON_EXPR = new IElementType("XQUERY_COMPARISON_EXPR", XQuery.INSTANCE);
    IElementType AND_EXPR = new IElementType("XQUERY_AND_EXPR", XQuery.INSTANCE);
    IElementType OR_EXPR = new IElementType("XQUERY_OR_EXPR", XQuery.INSTANCE);

    IElementType QUERY_BODY = new IElementType("XQUERY_QUERY_BODY", XQuery.INSTANCE);

    IElementType IMPORT = new IElementType("XQUERY_IMPORT", XQuery.INSTANCE);
    IElementType SCHEMA_PREFIX = new IElementType("XQUERY_SCHEMA_PREFIX", XQuery.INSTANCE);
    IElementType NAMESPACE_DECL = new IElementType("XQUERY_NAMESPACE_DECL", XQuery.INSTANCE);
    IElementType SCHEMA_IMPORT = new IElementType("XQUERY_SCHEMA_IMPORT", XQuery.INSTANCE);
    IElementType MODULE_IMPORT = new IElementType("XQUERY_MODULE_IMPORT", XQuery.INSTANCE);
    IElementType PROLOG = new IElementType("XQUERY_PROLOG", XQuery.INSTANCE);

    IElementType TYPE_DECLARATION = new IElementType("XQUERY_TYPE_DECLARATION", XQuery.INSTANCE);

    IElementType VAR_DECL = new IElementType("XQUERY_VAR_DECL", XQuery.INSTANCE);
    IElementType BASE_URI_DECL = new IElementType("XQUERY_BASE_URI_DECL", XQuery.INSTANCE);
    IElementType DEFAULT_COLLATION_DECL = new IElementType("XQUERY_DEFAULT_COLLATION_DECL", XQuery.INSTANCE);
    IElementType COPY_NAMESPACES_DECL = new IElementType("XQUERY_COPY_NAMESPACES_DECL", XQuery.INSTANCE);
    IElementType EMPTY_ORDER_DECL = new IElementType("XQUERY_EMPTY_ORDER_DECL", XQuery.INSTANCE);
    IElementType ORDERING_MODE_DECL = new IElementType("XQUERY_ORDERING_MODE_DECL", XQuery.INSTANCE);
    IElementType OPTION_DECL = new IElementType("XQUERY_OPTION_DECL", XQuery.INSTANCE);
    IElementType DEFAULT_NAMESPACE_DECL = new IElementType("XQUERY_DEFAULT_NAMESPACE_DECL", XQuery.INSTANCE);
    IElementType BOUNDARY_SPACE_DECL = new IElementType("XQUERY_BOUNDARY_SPACE_DECL", XQuery.INSTANCE);
    IElementType UNKNOWN_DECL = new IElementType("XQUERY_UNKNOWN_DECL", XQuery.INSTANCE);
    IElementType MODULE_DECL = new IElementType("XQUERY_MODULE_DECL", XQuery.INSTANCE);
    IElementType VERSION_DECL = new IElementType("XQUERY_VERSION_DECL", XQuery.INSTANCE);
    IElementType MAIN_MODULE = new IElementType("XQUERY_MAIN_MODULE", XQuery.INSTANCE);
    IElementType LIBRARY_MODULE = new IElementType("XQUERY_LIBRARY_MODULE", XQuery.INSTANCE);
    IElementType MODULE = new IElementType("XQUERY_MODULE", XQuery.INSTANCE);

    IElementType DIR_COMMENT_CONSTRUCTOR = new IElementType("XQUERY_DIR_COMMENT_CONSTRUCTOR", XQuery.INSTANCE);
}

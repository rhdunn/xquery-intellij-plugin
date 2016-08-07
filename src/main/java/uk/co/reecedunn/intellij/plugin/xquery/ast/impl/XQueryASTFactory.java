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
package uk.co.reecedunn.intellij.plugin.xquery.ast.impl;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;

public class XQueryASTFactory extends ASTFactory {
    @Override
    @Nullable
    public CompositeElement createComposite(final IElementType type) {
        if (type == XQueryElementType.STRING_LITERAL) {
            return new XQueryStringLiteralImpl(type);
        } else if (type == XQueryElementType.URI_LITERAL) {
            return new XQueryUriLiteralImpl(type);
        } else if (type == XQueryElementType.LITERAL) {
            return new XQueryLiteralImpl(type);
        } else if (type == XQueryElementType.FILTER_EXPR) {
            return new XQueryFilterExprImpl(type);
        } else if (type == XQueryElementType.RELATIVE_PATH_EXPR) {
            return new XQueryRelativePathExprImpl(type);
        } else if (type == XQueryElementType.PATH_EXPR) {
            return new XQueryPathExprImpl(type);
        } else if (type == XQueryElementType.UNARY_EXPR) {
            return new XQueryUnaryExprImpl(type);
        } else if (type == XQueryElementType.CAST_EXPR) {
            return new XQueryCastExprImpl(type);
        } else if (type == XQueryElementType.CASTABLE_EXPR) {
            return new XQueryCastableExprImpl(type);
        } else if (type == XQueryElementType.TREAT_EXPR) {
            return new XQueryTreatExprImpl(type);
        } else if (type == XQueryElementType.INSTANCEOF_EXPR) {
            return new XQueryInstanceofExprImpl(type);
        } else if (type == XQueryElementType.INTERSECT_EXCEPT_EXPR) {
            return new XQueryIntersectExceptExprImpl(type);
        } else if (type == XQueryElementType.UNION_EXPR) {
            return new XQueryUnionExprImpl(type);
        } else if (type == XQueryElementType.MULTIPLICATIVE_EXPR) {
            return new XQueryMultiplicativeExprImpl(type);
        } else if (type == XQueryElementType.ADDITIVE_EXPR) {
            return new XQueryAdditiveExprImpl(type);
        } else if (type == XQueryElementType.RANGE_EXPR) {
            return new XQueryRangeExprImpl(type);
        } else if (type == XQueryElementType.COMPARISON_EXPR) {
            return new XQueryComparisonExprImpl(type);
        } else if (type == XQueryElementType.AND_EXPR) {
            return new XQueryAndExprImpl(type);
        } else if (type == XQueryElementType.OR_EXPR) {
            return new XQueryOrExprImpl(type);
        } else if (type == XQueryElementType.QUERY_BODY) {
            return new XQueryQueryBodyImpl(type);
        } else if (type == XQueryElementType.NCNAME) {
            return new XQueryNCNameImpl(type);
        } else if (type == XQueryElementType.QNAME) {
            return new XQueryQNameImpl(type);
        } else if (type == XQueryElementType.COMMENT) {
            return new XQueryCommentImpl(type);
        } else if (type == XQueryElementType.DIR_COMMENT_CONSTRUCTOR) {
            return new XQueryDirCommentConstructorImpl(type);
        } else if (type == XQueryElementType.CDATA_SECTION) {
            return new XQueryCDataSectionImpl(type);
        } else if (type == XQueryElementType.SCHEMA_PREFIX) {
            return new XQuerySchemaPrefixImpl(type);
        } else if (type == XQueryElementType.ANY_KIND_TEST) {
            return new XQueryAnyKindTestImpl(type);
        } else if (type == XQueryElementType.ATOMIC_TYPE) {
            return new XQueryAtomicTypeImpl(type);
        } else if (type == XQueryElementType.ITEM_TYPE) {
            return new XQueryItemTypeImpl(type);
        } else if (type == XQueryElementType.OCCURRENCE_INDICATOR) {
            return new XQueryOccurrenceIndicatorImpl(type);
        } else if (type == XQueryElementType.SEQUENCE_TYPE) {
            return new XQuerySequenceTypeImpl(type);
        } else if (type == XQueryElementType.TYPE_DECLARATION) {
            return new XQueryTypeDeclarationImpl(type);
        } else if (type == XQueryElementType.CONSTRUCTION_DECL) {
            return new XQueryConstructionDeclImpl(type);
        } else if (type == XQueryElementType.VAR_DECL) {
            return new XQueryVarDeclImpl(type);
        } else if (type == XQueryElementType.BASE_URI_DECL) {
            return new XQueryBaseURIDeclImpl(type);
        } else if (type == XQueryElementType.DEFAULT_COLLATION_DECL) {
            return new XQueryDefaultCollationDeclImpl(type);
        } else if (type == XQueryElementType.COPY_NAMESPACES_DECL) {
            return new XQueryCopyNamespacesDeclImpl(type);
        } else if (type == XQueryElementType.EMPTY_ORDER_DECL) {
            return new XQueryEmptyOrderDeclImpl(type);
        } else if (type == XQueryElementType.ORDERING_MODE_DECL) {
            return new XQueryOrderingModeDeclImpl(type);
        } else if (type == XQueryElementType.OPTION_DECL) {
            return new XQueryOptionDeclImpl(type);
        } else if (type == XQueryElementType.DEFAULT_NAMESPACE_DECL) {
            return new XQueryDefaultNamespaceDeclImpl(type);
        } else if (type == XQueryElementType.BOUNDARY_SPACE_DECL) {
            return new XQueryBoundarySpaceDeclImpl(type);
        } else if (type == XQueryElementType.NAMESPACE_DECL) {
            return new XQueryNamespaceDeclImpl(type);
        } else if (type == XQueryElementType.SCHEMA_IMPORT) {
            return new XQuerySchemaImportImpl(type);
        } else if (type == XQueryElementType.MODULE_IMPORT) {
            return new XQueryModuleImportImpl(type);
        } else if (type == XQueryElementType.PROLOG) {
            return new XQueryPrologImpl(type);
        } else if (type == XQueryElementType.VERSION_DECL) {
            return new XQueryVersionDeclImpl(type);
        } else if (type == XQueryElementType.MODULE_DECL) {
            return new XQueryModuleDeclImpl(type);
        } else if (type == XQueryElementType.MAIN_MODULE) {
            return new XQueryMainModuleImpl(type);
        } else if (type == XQueryElementType.LIBRARY_MODULE) {
            return new XQueryLibraryModuleImpl(type);
        } else if (type == XQueryElementType.MODULE) {
            return new XQueryModuleImpl(type);
        } else if (type == XQueryElementType.IMPORT) {
            return new XQueryImportImpl(type);
        } else if (type == XQueryElementType.UNKNOWN_DECL) {
            return new XQueryUnknownDeclImpl(type);
        }

        throw new AssertionError("Alien element type [" + type + "]. Can't create XQuery AST Node for that.");
    }

    @Override
    @Nullable
    public LeafElement createLeaf(@NotNull final IElementType type, @NotNull CharSequence text) {
        if (type == XQueryTokenType.COMMENT ||
            type == XQueryTokenType.XML_COMMENT) {
            return new PsiCommentImpl(type, text);
        } else if (type == XQueryTokenType.INTEGER_LITERAL) {
            return new XQueryIntegerLiteralImpl(type, text);
        } else if (type == XQueryTokenType.DECIMAL_LITERAL) {
            return new XQueryDecimalLiteralImpl(type, text);
        } else if (type == XQueryTokenType.DOUBLE_LITERAL) {
            return new XQueryDoubleLiteralImpl(type, text);
        } else if (type == XQueryTokenType.PREDEFINED_ENTITY_REFERENCE) {
            return new XQueryPredefinedEntityRefImpl(type, text);
        } else if (type == XQueryTokenType.CHARACTER_REFERENCE) {
            return new XQueryCharRefImpl(type, text);
        } else if (type == XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER) {
            return new XQueryEscapeCharacterImpl(type, text);
        }

        return new LeafPsiElement(type, text);
    }
}

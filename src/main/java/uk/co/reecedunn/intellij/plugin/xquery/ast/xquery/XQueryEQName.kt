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
package uk.co.reecedunn.intellij.plugin.xquery.ast.xquery

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.functional.Option
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace

/**
 * An XQuery 3.0 <code>EQName</code> node in the XQuery AST.
 *
 * When the <code>EQName</code> node is specialised (such as with
 * <code>VarName</code>), the <code>EQName</code> node is not stored directly
 * in the AST. Instead, it is exposed as an instance of that specialised node.
 *
 * This may be an instance of an <code>NCName</code>, <code>QName</code> or
 * <code>URIQualifiedName</code>.
 */
interface XQueryEQName: PsiElement {
    enum class Type {
        Function,
        Variable,
        Unknown,
    }

    val type get(): Type {
        val parent = parent.node.elementType
        if (parent == XQueryElementType.FUNCTION_CALL ||
            parent == XQueryElementType.NAMED_FUNCTION_REF ||
            parent == XQueryElementType.ARROW_FUNCTION_SPECIFIER) {
            return Type.Function;
        } else {
            var previous = prevSibling
            while (previous?.node?.elementType == XQueryElementType.COMMENT ||
                   previous?.node?.elementType == XQueryTokenType.WHITE_SPACE) {
                previous = previous.prevSibling
            }

            if (previous?.node?.elementType == XQueryTokenType.VARIABLE_INDICATOR) {
                return Type.Variable;
            }
        }
        return Type.Unknown;
    }

    val prefix: Option<PsiElement>

    val localName: Option<PsiElement>

    fun resolvePrefixNamespace(): Option<XQueryNamespace>
}

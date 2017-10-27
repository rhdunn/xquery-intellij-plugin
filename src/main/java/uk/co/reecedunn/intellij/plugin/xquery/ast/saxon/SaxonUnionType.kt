package uk.co.reecedunn.intellij.plugin.xquery.ast.saxon

import com.intellij.psi.PsiElement

/**
 * A Saxon 9.8 `UnionType` node in the XQuery AST.
 *
 * <pre>
 *    UnionType ::= "union" "(" QName ("," QName)* ")"
 * </pre>
 *
 * Reference: http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/union-types
 */
interface SaxonUnionType : PsiElement
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
package uk.co.reecedunn.intellij.plugin.xquery.ast.xquery;

/**
 * An XQuery 1.0 <code>MainModule</code> node in the XQuery AST.
 *
 * This implements a <code>MainModule</code> node as an instance of a
 * <code>Module</code> node, which differs from the XQuery grammar, which
 * has the <code>MainModule</code> as a child of <code>Module</code>.
 * This has the effect of defining the <code>MainModule</code> EBNF as:
 *
 * <pre>{@code
 *     Module     ::= LibraryModule | MainModule
 *     MainModule ::= VersionDecl? Prolog QueryBody
 * }</pre>
 *
 * This simplifies the AST tree and makes it easier to reason what the
 * module type is from the {@link XQueryFile} node.
 */
public interface XQueryMainModule extends XQueryModuleBase {
}

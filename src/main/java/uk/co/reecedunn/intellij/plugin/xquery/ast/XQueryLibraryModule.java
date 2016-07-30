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
package uk.co.reecedunn.intellij.plugin.xquery.ast;

/**
 * An XQuery 1.0 <code>LibraryModule</code> node in the XQuery AST.
 *
 * This implements a <code>LibraryModule</code> node as an instance of a
 * <code>Module</code> node, which differs from the XQuery grammar, which
 * has the <code>LibraryModule</code> as a child of <code>Module</code>.
 * This has the effect of defining the <code>LibraryModule</code> EBNF as:
 *
 * <pre>{@code
 *     Module        ::= LibraryModule | MainModule
 *     LibraryModule ::= VersionDecl? ModuleDecl Prolog
 * }</pre>
 *
 * This simplifies the AST tree and makes it easier to reason what the
 * module type is from the {@link XQueryFile} node.
 */
public interface XQueryLibraryModule extends XQueryModule {
}

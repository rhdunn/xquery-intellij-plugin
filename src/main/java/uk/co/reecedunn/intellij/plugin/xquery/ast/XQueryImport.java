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
 * An XQuery 1.0 <code>Import</code> node in the XQuery AST.
 *
 * Because the child nodes of an <code>Import</code> are only referenced
 * from the <code>Import</code> node in the grammar, the <code>Import</code>
 * nodes are stored as instances of the child nodes instead of as distinct
 * nodes themselves.
 *
 * In the case of an invalid <code>Import</code> declaration that has an
 * <code>import</code> keyword that does not form a <code>SchemaImport</code>
 * nor <code>ModuleImport</code>, an <code>Import</code> node is used directly.
 * This is because there is not enough information to know what type of import
 * declaration this is.
 */
public interface XQueryImport {
}

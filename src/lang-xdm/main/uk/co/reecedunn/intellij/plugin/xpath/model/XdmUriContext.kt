/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

/**
 * The context type of an XsAnyURIValue.
 *
 * This specifies how the URI is intended to be used for when processing the
 * URI, such as when determining the paths the URI can resolve to.
 */
enum class XdmUriContext {
    /**
     * This represents the Static Base URIs.
     */
    BaseUri,
    /**
     * This represents a string collation.
     */
    Collation,
    /**
     * This represents a location URI for import statements.
     *
     * BaseX does not use location URIs when resolving modules. It converts
     * the namespace into a file system path or Java class path, and locates
     * the module using those paths.
     *
     * eXist-db uses location URIs to specify where to load the module from.
     * This can be from a resolvable URI, from a database collection, or from
     * a jar file in the classpath. If the location URI specifies a Java class,
     * that class must implement the eXist-db module API.
     *
     * MarkLogic uses location URIs to specify where in the modules database
     * for the application server the module is loaded from. If the database
     * is specified to be the file system (database 0), the module is loaded
     * from the file system instead. The application server can specify a
     * base path to resolve the modules relative to, such as the path on the
     * file system where the modules are located in the project. Modules are
     * also loaded from the Modules folder on a MarkLogic installation, which
     * contains the non-builtin MarkLogic APIs.
     *
     * Saxon passes the location URIs to implementations of the Saxon
     * ModuleURIResolver interface when resolving ModuleImports. The location
     * URIs are not used for resolving SchemaImports.
     */
    Location,
    /**
     * This represents a namespace not part of an import or declaration.
     */
    Namespace,
    /**
     * This represents a namespace declaration.
     *
     * BaseX handles declared and imported Java classes differently. A declared
     * class supports constructing new instances of that class and accessing
     * static methods.
     *
     * eXist-db supports binding any Java class to a namespace declaration.
     *
     * Saxon PE and EE support binding Java or .NET classes to a namespace
     * declaration as reflexive extension functions.
     * Saxon also supports registering functions to the static
     * context using the Saxon API in Java or .NET.
     *
     * MarkLogic uses the namespace directly to identify functions and
     * variables in that scope.
     *
     * Saxon PE and EE support binding Java or .NET classes to a namespace
     * declaration as reflexive extension functions. Integrated extension
     * functions are also supported by the s9api. These are functions that
     * implement the Saxon extension function API and are registered
     * beforehand.
     *
     * Saxon HE also supports integrated extension functions.
     */
    NamespaceDeclaration,
    /**
     * This represents a word list location.
     *
     * This is an XQuery and XPath Full Text feature.
     */
    StopWords,
    /**
     * This represents the target namespace URI for import statements.
     *
     * BaseX handles declared and imported Java classes differently. An
     * imported class has an instance of that class bound to the namespace.
     * If the class implements the BaseX module API, the class can access the
     * static and dynamic context of the query.
     *
     * eXist-db and MarkLogic only support importing module files from import
     * declarations via location URIs.
     *
     * Saxon passes the target namespace URI to implementations of the Saxon
     * ModuleURIResolver interface when resolving ModuleImports, and to
     * implementations of the URIResolver interface when resolving
     * SchemaImports.
     */
    TargetNamespace,
    /**
     * This represents a thesaurus location.
     *
     * This is an XQuery and XPath Full Text feature.
     */
    Thesaurus
}

/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.saxon.s9api

import java.io.File
import java.net.URLClassLoader

internal class SaxonClasses(path: File) {
    val itemClass: Class<*>
    val processorClass: Class<*>
    val typeClass: Class<*>
    val typeHierarchyClass: Class<*>
    val xdmItemClass: Class<*>
    val xdmSequenceIteratorClass: Class<*>
    val xqueryCompilerClass: Class<*>
    val xqueryEvaluatorClass: Class<*>
    val xqueryExecutableClass: Class<*>

    init {
        val loader = URLClassLoader(arrayOf(path.toURI().toURL()))
        itemClass = loader.loadClass("net.sf.saxon.om.Item")
        processorClass = loader.loadClass("net.sf.saxon.s9api.Processor")
        typeClass = loader.loadClass("net.sf.saxon.type.Type")
        typeHierarchyClass = loader.loadClass("net.sf.saxon.type.TypeHierarchy")
        xdmItemClass = loader.loadClass("net.sf.saxon.s9api.XdmItem")
        xdmSequenceIteratorClass = loader.loadClass("net.sf.saxon.s9api.XdmSequenceIterator")
        xqueryCompilerClass = loader.loadClass("net.sf.saxon.s9api.XQueryCompiler")
        xqueryEvaluatorClass = loader.loadClass("net.sf.saxon.s9api.XQueryEvaluator")
        xqueryExecutableClass = loader.loadClass("net.sf.saxon.s9api.XQueryExecutable")
    }
}

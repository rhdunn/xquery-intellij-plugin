/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.module.path

enum class XdmModuleType(val extensions: Array<String>) {
    DotNet(arrayOf()), // Saxon
    DTD(arrayOf()), // EXPath Package
    Java(arrayOf()), // BaseX, eXist-db, Saxon
    NVDL(arrayOf()), // EXPath Package
    RelaxNG(arrayOf()), // EXPath Package
    RelaxNGCompact(arrayOf()), // EXPath Package
    Resource(arrayOf()), // EXPath Package
    Schematron(arrayOf()), // EXPath Package
    XMLSchema(arrayOf(".xsd")), // Schema Aware Feature, EXPath Package
    XPath(arrayOf()), // XSLT
    XProc(arrayOf()), // EXPath Package
    XQuery(arrayOf(".xq", ".xqm", ".xqy", ".xql", ".xqu", ".xquery")), // Module Feature, EXPath Package
    XSLT(arrayOf()); // MarkLogic, EXPath Package

    companion object {
        val JAVA: Array<XdmModuleType> = arrayOf(Java)
        val MODULE: Array<XdmModuleType> = arrayOf(XQuery, Java, DotNet)
        val MODULE_OR_SCHEMA: Array<XdmModuleType> = arrayOf(XQuery, XMLSchema, Java, DotNet)
        val NONE: Array<XdmModuleType> = arrayOf()
        val XPATH_OR_XQUERY: Array<XdmModuleType> = arrayOf(XPath, XQuery)
        val XQUERY: Array<XdmModuleType> = arrayOf(XQuery)
        val RESOURCE: Array<XdmModuleType> = arrayOf(Resource)
        val SCHEMA: Array<XdmModuleType> = arrayOf(XMLSchema)
        val STYLESHEET: Array<XdmModuleType> = arrayOf(XSLT)
    }
}

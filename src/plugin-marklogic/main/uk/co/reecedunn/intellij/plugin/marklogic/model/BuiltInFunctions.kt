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
package uk.co.reecedunn.intellij.plugin.marklogic.model

import uk.co.reecedunn.intellij.plugin.xdm.module.JarModuleResolver

object BuiltInFunctions : JarModuleResolver() {
    override val classLoader: ClassLoader = this::class.java.classLoader

    override val modules = mapOf(
        "http://marklogic.com/cts" to "com/marklogic/cts.xqy",
        "http://marklogic.com/geospatial" to "com/marklogic/geospatial.xqy",
        "http://marklogic.com/xdmp" to "com/marklogic/xdmp.xqy",
        "http://marklogic.com/xdmp/dbg" to "com/marklogic/xdmp/dbg.xqy",
        "http://marklogic.com/xdmp/json" to "com/marklogic/xdmp/json.xqy",
        "http://marklogic.com/xdmp/map" to "com/marklogic/xdmp/map.xqy",
        "http://marklogic.com/xdmp/math" to "com/marklogic/xdmp/math.xqy",
        "http://marklogic.com/xdmp/profile" to "com/marklogic/xdmp/profile.xqy",
        "http://marklogic.com/xdmp/schema-components" to "com/marklogic/xdmp/schema-components.xqy",
        "http://marklogic.com/xdmp/semantics" to "com/marklogic/xdmp/semantics.xqy",
        "http://marklogic.com/xdmp/spell" to "com/marklogic/xdmp/spell.xqy",
        "http://marklogic.com/xdmp/sql" to "com/marklogic/xdmp/sql.xqy",
        "http://marklogic.com/xdmp/tde" to "com/marklogic/xdmp/tde.xqy",
        "http://marklogic.com/xdmp/temporal" to "com/marklogic/xdmp/temporal.xqy",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#" to "org/w3/www/1999/02/22-rdf-syntax-ns.xqy"
    )
}

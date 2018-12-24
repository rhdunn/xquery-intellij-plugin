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
package uk.co.reecedunn.intellij.plugin.marklogic.resources

import uk.co.reecedunn.intellij.plugin.core.io.decode

object MarkLogicQueries {
    private fun loadText(path: String): String {
        val loader = MarkLogicQueries::class.java.classLoader
        return loader.getResourceAsStream(path)!!.decode()
    }

    val Run = loadText("queries/marklogic/run.xq")

    val Version = loadText("queries/marklogic/version.xq")
}

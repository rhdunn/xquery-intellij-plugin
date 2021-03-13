/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xproc.resources

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.ResourceRegistrarImpl
import com.intellij.javaee.StandardResourceProvider

class XProcResourceProvider : StandardResourceProvider {
    companion object {
        private const val XPROC_NAMESPACE = "http://www.w3.org/ns/xproc"
    }

    override fun registerResources(registrar: ResourceRegistrar?) {
        val resources = registrar as ResourceRegistrarImpl

        resources.addStdResource(XPROC_NAMESPACE, "1.0", "/schemas/xproc10.rng", XProcResourceProvider::class.java)
    }
}

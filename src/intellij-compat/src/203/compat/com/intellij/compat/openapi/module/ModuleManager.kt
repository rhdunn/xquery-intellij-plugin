/*
 * Copyright (C) 2020 Reece H. Dunn
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
package com.intellij.compat.openapi.module

import com.intellij.openapi.module.*
import java.io.IOException
import java.nio.file.Path

@Suppress("NonExtendableApiUsage")
abstract class ModuleManager : com.intellij.openapi.module.ModuleManager() {
    @Throws(IOException::class, ModuleWithNameAlreadyExists::class)
    abstract fun loadModule(file: Path): Module
}

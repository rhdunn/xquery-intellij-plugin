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
package com.google.compat.gson

import com.google.gson.JsonElement
import com.google.gson.stream.JsonReader
import java.io.Reader

object JsonParser {
    @Suppress("unused")
    fun parseString(json: String): JsonElement = com.google.gson.JsonParser().parse(json)

    fun parseReader(json: Reader): JsonElement = com.google.gson.JsonParser().parse(json)

    @Suppress("unused")
    fun parseReader(json: JsonReader): JsonElement = com.google.gson.JsonParser().parse(json)
}

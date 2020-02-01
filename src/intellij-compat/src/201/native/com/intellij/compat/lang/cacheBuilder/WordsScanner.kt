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
package com.intellij.compat.lang.cacheBuilder

import com.intellij.lang.cacheBuilder.WordOccurrence
import com.intellij.util.Processor

abstract class WordsScanner : com.intellij.lang.cacheBuilder.WordsScanner {
    // IntelliJ >= 2020.1 changes the 'processor' variable type, making it contravariant.
    override fun processWords(fileText: CharSequence, processor: Processor<in WordOccurrence>) {
        processWordsEx(fileText, processor)
    }

    // It is not possible to override a Kotlin/Java method that only differs in variance.
    abstract fun processWordsEx(fileText: CharSequence, processor: Processor<in WordOccurrence>)
}

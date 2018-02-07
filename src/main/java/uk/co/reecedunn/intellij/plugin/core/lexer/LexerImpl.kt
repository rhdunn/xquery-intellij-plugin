/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

abstract class LexerImpl : LexerBase() {
    protected val mTokenRange: CodePointRange = CodePointRange()
    protected var mType: IElementType? = null

    // region Lexer

    override fun getTokenType(): IElementType? = mType

    override fun getTokenStart(): Int = mTokenRange.start

    override fun getTokenEnd(): Int = mTokenRange.end

    override fun getBufferSequence(): CharSequence = mTokenRange.bufferSequence

    override fun getBufferEnd(): Int = mTokenRange.bufferEnd

    // endregion
}

/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.lexer;

import org.jetbrains.annotations.NotNull;

public class XQueryCodePointRange {
    public static final int END_OF_BUFFER = -1;

    private CharSequence mBuffer;
    private int mStart;
    private int mEnd;
    private int mSaved;
    private int mEndOfBuffer;

    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset) {
        mBuffer = buffer;
        mStart = mEnd = startOffset;
        mEndOfBuffer = endOffset;
    }

    public void flush() {
        mStart = mEnd;
    }

    public void match() {
        if (mEnd != mEndOfBuffer) {
            if (Character.isHighSurrogate(mBuffer.charAt(mEnd))) {
                mEnd += 1;
                if ((mEnd != mEndOfBuffer) && Character.isLowSurrogate(mBuffer.charAt(mEnd)))
                    mEnd += 1;
            } else {
                mEnd += 1;
            }
        }
    }

    public void save() {
        mSaved = mEnd;
    }

    public void restore() {
        mEnd = mSaved;
    }

    public final int getStart() {
        return mStart;
    }

    public final int getEnd() {
        return mEnd;
    }

    public CharSequence getBufferSequence() {
        return mBuffer;
    }

    public final int getBufferEnd() {
        return mEndOfBuffer;
    }

    public final int getCodePoint() {
        if (mEnd == mEndOfBuffer)
            return END_OF_BUFFER;
        char high = mBuffer.charAt(mEnd);
        if (Character.isHighSurrogate(high) && (mEnd + 1) != mEndOfBuffer) {
            char low = mBuffer.charAt(mEnd + 1);
            if (Character.isLowSurrogate(low)) {
                return Character.toCodePoint(high, low);
            }
        }
        return high;
    }
}

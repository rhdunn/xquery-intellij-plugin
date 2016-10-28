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
package uk.co.reecedunn.intellij.plugin.xquery.filetypes;

public class ByteSequence implements CharSequence {
    private final byte[] mData;
    private final int mOffset;
    private final int mLength;

    public ByteSequence(byte[] data) {
        this(data, 0, data.length);
    }

    private ByteSequence(byte[] data, int offset, int length) {
        mData = data;
        mOffset = offset;
        mLength = length;
    }

    @Override
    public int length() {
        return mLength;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= mLength) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        return (char)(mData[mOffset + index] & 0xFF);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || start > mLength) {
            throw new IndexOutOfBoundsException(String.valueOf(start));
        }

        int length = end - start;
        if (length < 0 || length > mLength) {
            throw new IndexOutOfBoundsException(String.valueOf(length));
        }

        return new ByteSequence(mData, mOffset + start, length);
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public String toString() {
        return new String(mData, mOffset, mLength);
    }
}

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
package uk.co.reecedunn.intellij.plugin.xquery.tests.findUsages;

import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.util.Processor;

import java.util.ArrayList;
import java.util.List;

public class WordOccurrences implements Processor<WordOccurrence> {
    private List<WordOccurrence> mWordOccurrences = new ArrayList<>();

    public List<WordOccurrence> getWordOccurrrences() {
        return mWordOccurrences;
    }

    @Override
    public boolean process(WordOccurrence wordOccurrence) {
        mWordOccurrences.add(wordOccurrence);
        return true;
    }
}

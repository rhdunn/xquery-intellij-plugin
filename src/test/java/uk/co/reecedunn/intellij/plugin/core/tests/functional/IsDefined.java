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
package uk.co.reecedunn.intellij.plugin.core.tests.functional;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNot;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;

public class IsDefined<T> extends BaseMatcher<T> {
    public boolean matches(Object o) {
        return ((Option)o).isDefined();
    }

    public void describeTo(Description description) {
        description.appendText("defined");
    }

    @Factory
    public static Matcher<Object> defined() {
        return new IsDefined<>();
    }

    @Factory
    public static Matcher<Object> notDefined() {
        return IsNot.not(defined());
    }

    @Factory
    public static <T> Matcher<T> defined(Class<T> type) {
        return new IsDefined<>();
    }

    @Factory
    public static <T> Matcher<T> notDefined(Class<T> type) {
        return IsNot.not(defined(type));
    }
}

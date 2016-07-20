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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImplementationsTest extends TestCase {
    public void testImplementations() {
        final List<ImplementationItem> implementations = Implementations.getImplementations();
        assertThat(implementations.size(), is(3));

        assertThat(implementations.get(0).getID(), is("marklogic"));
        assertThat(implementations.get(0).toString(), is("MarkLogic"));

        assertThat(implementations.get(1).getID(), is("saxon"));
        assertThat(implementations.get(1).toString(), is("Saxonica"));

        assertThat(implementations.get(2).getID(), is("w3c"));
        assertThat(implementations.get(2).toString(), is("W3C"));

        assertThat(implementations.get(0).equals(implementations.get(0)), is(true));
        assertThat(implementations.get(1).equals(implementations.get(0)), is(false));
        assertThat(implementations.get(0).equals(implementations.get(0).getID()), is(false));

        assertThat(implementations.get(0).equals(ImplementationItem.NULL_ITEM), is(false));
        assertThat(ImplementationItem.NULL_ITEM.equals(ImplementationItem.NULL_ITEM), is(true));
    }

    public void testDefaultImplementation() {
        ImplementationItem implementation = Implementations.getDefaultImplementation();
        assertThat(implementation.getID(), is("w3c"));
        assertThat(implementation.toString(), is("W3C"));
    }

    public void testDefaultItemForAnUnsupportedTagName() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        ImplementationItem version = implementation.getDefaultItem("does-not-exist");
        assertThat(version.getID(), is(nullValue()));
        assertThat(version.toString(), is(nullValue()));
    }

    public void testImplementationVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        final List<ImplementationItem> versions = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION);
        assertThat(versions.size(), is(3));

        assertThat(versions.get(0).getID(), is("saxon/HE"));
        assertThat(versions.get(0).toString(), is("Home Edition"));

        assertThat(versions.get(1).getID(), is("saxon/PE"));
        assertThat(versions.get(1).toString(), is("Professional Edition"));

        assertThat(versions.get(2).getID(), is("saxon/EE"));
        assertThat(versions.get(2).toString(), is("Enterprise Edition"));

        assertThat(versions.get(0).equals(versions.get(0)), is(true));
        assertThat(versions.get(1).equals(versions.get(0)), is(false));
        assertThat(versions.get(0).equals(versions.get(0).getID()), is(false));

        assertThat(versions.get(0).equals(ImplementationItem.NULL_ITEM), is(false));
        assertThat(ImplementationItem.NULL_ITEM.equals(ImplementationItem.NULL_ITEM), is(true));
    }

    public void testDefaultImplementationVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(1);

        ImplementationItem version = implementation.getDefaultItem(ImplementationItem.IMPLEMENTATION_VERSION);
        assertThat(version.getID(), is("saxon/HE"));
        assertThat(version.toString(), is("Home Edition"));
    }
}

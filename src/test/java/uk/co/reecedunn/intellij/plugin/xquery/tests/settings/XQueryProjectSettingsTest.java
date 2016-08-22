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
package uk.co.reecedunn.intellij.plugin.xquery.tests.settings;

import com.intellij.openapi.application.PathManager;
import com.intellij.util.xmlb.XmlSerializer;
import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

public class XQueryProjectSettingsTest extends TestCase {
    public void testDefaultValues() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getImplementation(), is("w3c"));
        assertThat(settings.getImplementationVersion(), is("w3c/spec"));
        assertThat(settings.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(settings.getXQuery10Dialect(), is("w3c/1.0"));
        assertThat(settings.getXQuery30Dialect(), is("w3c/3.0"));
        assertThat(settings.getXQuery31Dialect(), is("w3c/3.1"));

        assertThat(settings.getImplementationItem().getID(), is("w3c"));
        assertThat(settings.getImplementationVersionItem().getID(), is("w3c/spec"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0).getID(), is("w3c/1.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0).getID(), is("w3c/3.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1).getID(), is("w3c/3.1"));
    }

    public void testGetState() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getState(), is(settings));
    }

    public void testLoadState() {
        ImplementationItem implementation = Implementations.getImplementations().get(0);
        ImplementationItem implementationVersion = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(0);
        List<ImplementationItem> implementationDialects = implementationVersion.getItems(ImplementationItem.IMPLEMENTATION_DIALECT);

        XQueryProjectSettings other = new XQueryProjectSettings();
        other.setImplementation(implementation.getID());
        other.setImplementationVersion(implementationVersion.getID());
        other.setXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC);
        other.setXQuery10Dialect(implementationDialects.get(0).getID());
        other.setXQuery30Dialect(implementationDialects.get(1).getID());
        other.setXQuery31Dialect(implementationDialects.get(2).getID());

        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.loadState(other);
        assertThat(settings.getImplementation(), is("marklogic"));
        assertThat(settings.getImplementationVersion(), is("marklogic/v6"));
        assertThat(settings.getXQueryVersion(), is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(settings.getXQuery10Dialect(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getXQuery30Dialect(), is("marklogic/v6/1.0"));
        assertThat(settings.getXQuery31Dialect(), is("marklogic/v6/1.0-ml"));

        // Setting via the bean properties updates the transient properties.
        assertThat(settings.getImplementationItem().getID(), is("marklogic"));
        assertThat(settings.getImplementationVersionItem().getID(), is("marklogic/v6"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0).getID(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0).getID(), is("marklogic/v6/1.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1).getID(), is("marklogic/v6/1.0-ml"));
    }

    public void testExportedFiles() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getExportFiles().length, is(1));
        assertThat(settings.getExportFiles()[0].getParent(), is(new File(PathManager.getOptionsPath()).getAbsolutePath()));
        assertThat(settings.getExportFiles()[0].getName(), is("xquery_project_settings.xml"));
    }

    public void testPresentableName() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getPresentableName(), is("XQuery Project Settings"));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testTransientProperties() {
        ImplementationItem implementation = Implementations.getImplementations().get(0);
        ImplementationItem implementationVersion = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(0);
        List<ImplementationItem> implementationDialects = implementationVersion.getItems(ImplementationItem.IMPLEMENTATION_DIALECT);

        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.setImplementationItem(implementation);
        settings.setImplementationVersionItem(implementationVersion);
        settings.setDialectForXQueryVersion(XQueryVersion.XQUERY_1_0, implementationDialects.get(0));
        settings.setDialectForXQueryVersion(XQueryVersion.XQUERY_3_0, implementationDialects.get(1));
        settings.setDialectForXQueryVersion(XQueryVersion.XQUERY_3_1, implementationDialects.get(2));

        AssertionError e = expectThrows(AssertionError.class, () -> settings.setDialectForXQueryVersion(null, implementationDialects.get(2)));
        assertThat(e.getMessage(), is("Unknown XQuery version: null"));

        assertThat(settings.getImplementationItem().getID(), is("marklogic"));
        assertThat(settings.getImplementationVersionItem().getID(), is("marklogic/v6"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0).getID(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0).getID(), is("marklogic/v6/1.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1).getID(), is("marklogic/v6/1.0-ml"));

        // Setting via the transient properties updates the bean properties.
        assertThat(settings.getImplementation(), is("marklogic"));
        assertThat(settings.getImplementationVersion(), is("marklogic/v6"));
        assertThat(settings.getXQuery10Dialect(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getXQuery30Dialect(), is("marklogic/v6/1.0"));
        assertThat(settings.getXQuery31Dialect(), is("marklogic/v6/1.0-ml"));
    }

    public void testSerialization() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        XMLOutputter outputter = new XMLOutputter();

        final String expected
                = "<XQueryProjectSettings>"
                + "<option name=\"XQuery10Dialect\" value=\"w3c/1.0\" />"
                + "<option name=\"XQuery30Dialect\" value=\"w3c/3.0\" />"
                + "<option name=\"XQuery31Dialect\" value=\"w3c/3.1\" />"
                + "<option name=\"XQueryVersion\" value=\"3.0\" />"
                + "<option name=\"implementation\" value=\"w3c\" />"
                + "<option name=\"implementationVersion\" value=\"w3c/spec\" />"
                + "</XQueryProjectSettings>";

        Element element = XmlSerializer.serialize(settings);
        assertThat(outputter.outputString(element), is(expected));
    }
}

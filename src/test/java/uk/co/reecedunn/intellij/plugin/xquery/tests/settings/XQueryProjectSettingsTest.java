/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.intellij.util.xmlb.XmlSerializer;
import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic;
import uk.co.reecedunn.intellij.plugin.xquery.lang.W3C;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryProjectSettingsTest extends TestCase {
    public void testDefaultValues() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getImplementationVersion(), is("w3c/spec/v1ed"));
        assertThat(settings.getXQueryVersion(), is(XQuery.INSTANCE.getREC_1_0_20070123().getLabel()));
        assertThat(settings.getXQuery10Dialect(), is(XQuery.INSTANCE.getId()));
        assertThat(settings.getXQuery30Dialect(), is(XQuery.INSTANCE.getId()));
        assertThat(settings.getXQuery31Dialect(), is(XQuery.INSTANCE.getId()));

        assertThat(settings.getProduct(), is(W3C.INSTANCE.getSPECIFICATIONS()));
        assertThat(settings.getProductVersion(), is(W3C.INSTANCE.getFIRST_EDITION()));
    }

    public void testGetState() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getState(), is(settings));
    }

    public void testLoadState() {
        XQueryProjectSettings other = new XQueryProjectSettings();
        other.setImplementationVersion("marklogic/v6");
        other.setXQueryVersion(XQuery.INSTANCE.getMARKLOGIC_0_9().getLabel());
        other.setXQuery10Dialect(MarkLogic.INSTANCE.getId());
        other.setXQuery30Dialect(MarkLogic.INSTANCE.getId());
        other.setXQuery31Dialect(MarkLogic.INSTANCE.getId());

        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.loadState(other);
        assertThat(settings.getImplementationVersion(), is("marklogic/v6"));
        assertThat(settings.getXQueryVersion(), is(XQuery.INSTANCE.getMARKLOGIC_0_9().getLabel()));
        assertThat(settings.getXQuery10Dialect(), is(MarkLogic.INSTANCE.getId()));
        assertThat(settings.getXQuery30Dialect(), is(MarkLogic.INSTANCE.getId()));
        assertThat(settings.getXQuery31Dialect(), is(MarkLogic.INSTANCE.getId()));

        assertThat(settings.getProduct(), is(MarkLogic.INSTANCE.getMARKLOGIC()));
        assertThat(settings.getProductVersion(), is(MarkLogic.INSTANCE.getVERSION_6_0()));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testTransientProperties() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.setImplementationVersion("marklogic/v6");

        assertThat(settings.getProduct(), is(MarkLogic.INSTANCE.getMARKLOGIC()));
        assertThat(settings.getProductVersion(), is(MarkLogic.INSTANCE.getVERSION_6_0()));

        // Setting via the transient properties updates the bean properties.
        assertThat(settings.getImplementationVersion(), is("marklogic/v6"));
        assertThat(settings.getXQuery10Dialect(), is("xquery"));
        assertThat(settings.getXQuery30Dialect(), is("xquery"));
        assertThat(settings.getXQuery31Dialect(), is("xquery"));
    }

    public void testDefaultXQueryDialectForUnsupportedXQueryVersions() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.setImplementationVersion("marklogic/v7");
        settings.setXQuery10Dialect(null);
        settings.setXQuery30Dialect(null);
        settings.setXQuery31Dialect(null);

        settings.setImplementationVersion("w3c/spec");
        settings.setXQuery10Dialect("w3c/1.0");
        settings.setXQuery30Dialect("w3c/3.0");
        settings.setXQuery31Dialect("w3c/3.1");
    }

    public void testSerialization() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        XMLOutputter outputter = new XMLOutputter();

        final String expected
                = "<XQueryProjectSettings>"
                + "<option name=\"XQuery10Dialect\" value=\"xquery\" />"
                + "<option name=\"XQuery30Dialect\" value=\"xquery\" />"
                + "<option name=\"XQuery31Dialect\" value=\"xquery\" />"
                + "<option name=\"XQueryVersion\" value=\"1.0\" />"
                + "<option name=\"implementationVersion\" value=\"w3c/spec/v1ed\" />"
                + "</XQueryProjectSettings>";

        Element element = XmlSerializer.serialize(settings);
        assertThat(outputter.outputString(element), is(expected));
    }
}

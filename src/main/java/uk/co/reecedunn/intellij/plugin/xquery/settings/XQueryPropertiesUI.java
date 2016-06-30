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
package uk.co.reecedunn.intellij.plugin.xquery.settings;

import uk.co.reecedunn.intellij.plugin.xquery.LanguageLevel;

import javax.swing.*;

public class XQueryPropertiesUI {
    private JComboBox<LanguageLevel> mLanguageLevel;
    private JPanel mPanel;

    public JPanel getPanel() {
        return mPanel;
    }

    private void createUIComponents() {
        mLanguageLevel = new JComboBox<>();
        mLanguageLevel.addItem(LanguageLevel.XQUERY_0_9_MARKLOGIC);
        mLanguageLevel.addItem(LanguageLevel.XQUERY_1_0);
        mLanguageLevel.addItem(LanguageLevel.XQUERY_1_0_MARKLOGIC);
        mLanguageLevel.addItem(LanguageLevel.XQUERY_3_0);
        mLanguageLevel.addItem(LanguageLevel.XQUERY_3_1);
    }
}

/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.execution.marklogic.configuration;

import com.intellij.openapi.fileChooser.FileTypeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI;
import uk.co.reecedunn.intellij.plugin.core.ui.TextFieldFileAccessor;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import javax.swing.*;

public class MarkLogicSettingsUI implements SettingsUI<MarkLogicRunConfiguration> {
    private Project mProject;
    private JPanel mPanel;
    private ComponentWithBrowseButton<JTextField> mMainModule;
    private JTextField mHostName;
    private JTextField mPort;
    private JTextField mUserName;
    private JPasswordField mPassword;

    public MarkLogicSettingsUI(Project project) {
        mProject = project;
    }

    private void createUIComponents() {
        mMainModule = new ComponentWithBrowseButton<>(new JTextField(), null);
        mHostName = new JTextField();
        mPort = new JTextField();
        mUserName = new JTextField();
        mPassword = new JPasswordField();

        mMainModule.addBrowseFolderListener(
            XQueryBundle.message("browser.choose-main-module"),
            null,
            mProject,
            new FileTypeDescriptor("XQuery", XQueryFileType.EXTENSIONS.split(";")),
            new TextFieldFileAccessor());
    }

    @Override
    public JPanel getPanel() {
        return mPanel;
    }

    @Override
    public boolean isModified(MarkLogicRunConfiguration configuration) {
        return true;
    }

    @Override
    public void reset(MarkLogicRunConfiguration configuration) {
        mMainModule.getChildComponent().setText(configuration.getMainModulePath());
        mHostName.setText(configuration.getServerHost());
        mPort.setText(Integer.toString(configuration.getServerPort()));
        mUserName.setText(configuration.getUserName());
        mPassword.setText(configuration.getPassword());
    }

    @Override
    public void apply(MarkLogicRunConfiguration configuration) {
        configuration.setMainModulePath(mMainModule.getChildComponent().getText());
        configuration.setServerHost(mHostName.getText());
        try {
            configuration.setServerPort(Integer.parseInt(mPort.getText()));
        } catch (NumberFormatException e) {
            //
        }
        configuration.setUserName(mUserName.getText());
        configuration.setPassword(String.valueOf(mPassword.getPassword()));
    }
}

/*
 * Copyright 2013 gergo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author gergo
 */
@StyleSheet("uploadstatewindow.css")
public class UploadStateWindow extends Window {

    private static final String CANCEL_ICON = "remove_small.png";
    private static final String WINDOW_STYLE_CLASS = "multiple-upload-state-window";
    private static final String WINDOW_LAYOUT_STYLE_CLASS = "multiple-upload-state-window-layout";
    private List<UploadStatePanel> uploadStatePanels = new ArrayList<UploadStatePanel>();
    private VerticalLayout windowLayout = new VerticalLayout();
    private String uploadStatusCaption = "Upload status";
    private String cancelButtonCaption = "Cancel";
    private Resource cancelIconResource = new ClassResource(UploadStateWindow.class, CANCEL_ICON);
    private ConfirmDialog confirmDialog = new ConfirmDialog();

    public UploadStateWindow() {
        super();
        initWindow();
    }

    private void initWindow() {
        setCaption(uploadStatusCaption);
        addStyleName(WINDOW_STYLE_CLASS);

        setResizable(false);
        setDraggable(false);

        setVisible(false);
        setWidth(350, Unit.PIXELS);
        setClosable(true);
        addCloseListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                show();
                confirmDialog.show();
            }
        });

        confirmDialog.setAction(new ConfirmAction() {
            @Override
            public void execute() {
                interruptAll();
            }
        });

        windowLayout.setStyleName(WINDOW_LAYOUT_STYLE_CLASS);
        setContent(windowLayout);
        windowLayout.setMargin(false);
    }

    public void refreshVisibility() {
        if (hasVisibleContent()) {
            show();
        } else {
            confirmDialog.hide();
            hide();
        }
    }

    private boolean hasVisibleContent() {
        for (UploadStatePanel uploadStatePanel : uploadStatePanels) {
            if (uploadStatePanel.isVisible()) {
                return true;
            }
        }
        return false;
    }

    private void show() {
        if (this.getParent() == null) {
            UI.getCurrent().addWindow(this);
        }
        setVisible(true);
    }

    private void hide() {
        setVisible(false);
    }

    public void interruptAll() {
        for (int i = uploadStatePanels.size() - 1; i >= 0; i--) {
            UploadStatePanel panel = uploadStatePanels.get(i);
            panel.interruptAll();
        }
    }

    public void addPanel(UploadStatePanel panel) {
        uploadStatePanels.add(panel);
        windowLayout.addComponent(panel);
    }

    public void removePanel(UploadStatePanel panel) {
        uploadStatePanels.remove(panel);
        windowLayout.removeComponent(panel);
    }

    public void setUploadStatusCaption(String uploadStatusCaption) {
        this.uploadStatusCaption = uploadStatusCaption;
        setCaption(uploadStatusCaption);
    }

    public void setCancelButtonCaption(String cancelButtonCaption) {
        this.cancelButtonCaption = cancelButtonCaption;
    }

    public String getCancelButtonCaption() {
        return cancelButtonCaption;
    }

    public Resource getCancelIconResource() {
        return cancelIconResource;
    }

    public void setCancelIconResource(Resource cancelIconResource) {
        this.cancelIconResource = cancelIconResource;
    }

    public ConfirmDialog getConfirmDialog() {
        return confirmDialog;
    }

    public void setConfirmDialog(ConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }
}
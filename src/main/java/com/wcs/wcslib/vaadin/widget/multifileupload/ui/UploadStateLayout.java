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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.UploadUtil;

/**
 *
 * @author gergo
 */
public class UploadStateLayout extends CssLayout {

    private static final String CANCEL_BUTTON_STYLE_CLASS = "multiple-upload-state-cancelbutton";
    private static final String CANCEL_BUTTON_LAYOUT_STYLE_CLASS = "multiple-upload-state-cancelbuttonlayout";
    private final Label fileName = new Label();
    private final Label textualProgress = new Label();
    private final ProgressBar pi = new ProgressBar();
    private Button cancelButton;
    private VerticalLayout layout;
    private HorizontalLayout cancelLayout;
    private UploadStatePanel uploadStatePanel;
    private FileDetailBean fileDetailBean;

    public UploadStateLayout(final UploadStatePanel uploadStatePanel) {
        this.uploadStatePanel = uploadStatePanel;
        initForm();
    }

    private void initForm() {
        layout = new VerticalLayout();
        addComponent(layout);

        layout.addComponent(fileName);

        pi.setVisible(false);
        pi.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(pi);

        textualProgress.setVisible(false);

        cancelLayout = new HorizontalLayout();
        cancelLayout.addStyleName(CANCEL_BUTTON_LAYOUT_STYLE_CLASS);
        cancelLayout.setWidth(100, Unit.PERCENTAGE);
        cancelLayout.addComponent(textualProgress);
        cancelButton = new Button();
        cancelLayout.addComponent(cancelButton);
        cancelLayout.setComponentAlignment(cancelButton, Alignment.TOP_RIGHT);
        layout.addComponent(cancelLayout);
    }

    private Button createNewCancelButton() {
        return createCancelBtn();
    }

    private Button createCancelBtn() {
        Button button = new Button();
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                uploadStatePanel.interruptUpload(fileDetailBean);
            }
        });
        button.setIcon(uploadStatePanel.getWindow().getCancelIconResource());
        button.setStyleName("small");
        button.setCaption(uploadStatePanel.getWindow().getCancelButtonCaption());
        return button;
    }

    public void setFileName(String fileName) {
        this.fileName.setValue(fileName);
    }

    public void setProgress(long bytesReceived, long contentLength) {
        pi.setValue(new Float(bytesReceived / (float) contentLength));
        textualProgress.setValue(
                UploadUtil.getHumanReadableByteCount(bytesReceived, false)
                + " / "
                + UploadUtil.getHumanReadableByteCount(contentLength, false));
    }

    public void startStreaming(FileDetailBean fileDetailBean) {
        this.fileDetailBean = fileDetailBean;
        pi.setValue(0f);
        pi.setVisible(true);
        textualProgress.setVisible(true);
        setFileName(fileDetailBean.getFileName());

        Button newCancelBtn = createNewCancelButton();
        cancelLayout.replaceComponent(cancelButton, newCancelBtn);
        cancelButton = newCancelBtn;
        cancelButton.addStyleName(CANCEL_BUTTON_STYLE_CLASS);
    }

    public FileDetailBean getFileDetailBean() {
        return fileDetailBean;
    }
}
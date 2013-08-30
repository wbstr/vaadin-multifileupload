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

import com.vaadin.ui.CustomComponent;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.SmartMultiUpload;

/**
 *
 * @author gergo
 */
public class MultiFileUpload extends CustomComponent {

    private SmartMultiUpload smartUpload;
    private UploadStatePanel uploadStatePanel;

    public MultiFileUpload(UploadFinishedHandler handler, UploadStateWindow uploadStateWindow, boolean multiple) {
        uploadStatePanel = createStatePanel(uploadStateWindow);
        initSmartUpload(handler, multiple);
    }

    public MultiFileUpload(UploadFinishedHandler handler, UploadStateWindow uploadStateWindow) {
        this(handler, uploadStateWindow, true);
    }

    public SmartMultiUpload getSmartUpload() {
        return smartUpload;
    }

    public UploadStatePanel getUploadStatePanel() {
        return uploadStatePanel;
    }

    public void setPanelCaption(String caption) {
        uploadStatePanel.setCaption(caption);
    }

    protected UploadStatePanel createStatePanel(UploadStateWindow uploadStateWindow) {
        return new UploadStatePanel(uploadStateWindow);
    }

    private void initSmartUpload(UploadFinishedHandler handler, boolean multiple) {
        smartUpload = new SmartMultiUpload(uploadStatePanel, multiple);

        uploadStatePanel.setMultiUpload(smartUpload);
        uploadStatePanel.setFinishedHandler(handler);

        setCompositionRoot(smartUpload);
    }

    public void interruptAll() {
        uploadStatePanel.interruptAll();
    }

    public void setMaxFileSize(int maxFileSizeInBytes) {
        smartUpload.setMaxFileSize(maxFileSizeInBytes);
    }

    public int getMaxFileSize() {
        return smartUpload.getMaxFileSize();
    }

    public String getSizeErrorMsg() {
        return smartUpload.getSizeErrorMsg();
    }

    /**
     *
     * @param pattern Pattern of the error message, which occurs when a user uploaded too big file. ({0} maxFileSize,
     * {1} fileSize, {2} fileName)
     */
    public void setSizeErrorMsgPattern(String pattern) {
        smartUpload.setSizeErrorMsgPattern(pattern);
    }

    /**
     *
     * @param maxVisibleRows The number of rows which after the upload queue table renders a scrollbar.
     */
    public void setMaxVisibleRows(int maxVisibleRows) {
        uploadStatePanel.getTable().setMaxVisibleRows(maxVisibleRows);
    }

    /**
     * Sets mime types that browser should let users upload. This check is done by the client side and not supported by
     * all browsers. Some browser us the accept filter just as a initial filter for their file chooser dialog. Note that
     * using this method does not invalidate need for server side checks.
     *
     * See https://developer.mozilla.org/en-US/docs/HTML/Element/Input
     *
     * @param accept
     */
    public void setAcceptFilter(String accept) {
        smartUpload.setAcceptFilter(accept);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        smartUpload.setEnabled(enabled);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        smartUpload.setEnabled(!readOnly);
    }
}

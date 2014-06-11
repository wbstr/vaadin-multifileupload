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

import com.vaadin.server.Resource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.SmartMultiUpload;

import java.util.List;

/**
 * 
 * @author gergo
 */
public class MultiFileUpload extends CustomComponent {

    private SmartMultiUpload smartUpload;
    private UploadStatePanel uploadStatePanel;
    private String interruptedMsg = "All uploads have been interrupted.";

    public MultiFileUpload(UploadFinishedHandler handler, UploadStateWindow uploadStateWindow, boolean multiple) {
        uploadStatePanel = createStatePanel(uploadStateWindow);
        initSmartUpload(handler, multiple);
    }

    public MultiFileUpload() {
        this(null, new UploadStateWindow(), true);
    }

    public MultiFileUpload(final boolean multiple) {
        this(null, new UploadStateWindow(), multiple);
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

    public void setAllFilesUploadedHandler(final AllFilesUploadedHandler allFilesUploadedHandler) {
        this.getUploadStatePanel().setAllFilesUploadedHandler(allFilesUploadedHandler);
    }

    private void initSmartUpload(UploadFinishedHandler handler, boolean multiple) {
        smartUpload = new SmartMultiUpload(uploadStatePanel, multiple);

        uploadStatePanel.setMultiUpload(smartUpload);
        uploadStatePanel.setFinishedHandler(handler);

        setCompositionRoot(smartUpload);
    }

    public void setUploadFinishHandler(final UploadFinishedHandler uploadFinishHandler) {
        this.uploadStatePanel.setFinishedHandler(uploadFinishHandler);
    }

    public void setUploadButtonCaptions(String singleUploadCaption, String multiUploadCaption) {
        smartUpload.setUploadButtonCaptions(singleUploadCaption, multiUploadCaption);
    }

    public void setUploadButtonIcon(Resource icon) {
        smartUpload.setUploadButtonIcon(icon);
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

    public String getInterruptedMsg() {
        return interruptedMsg;
    }

    public void setInterruptedMsg(String interruptedMsg) {
        this.interruptedMsg = interruptedMsg;
    }

    public String getSizeErrorMsg() {
        return smartUpload.getSizeErrorMsg();
    }

    /**
     * 
     * @param pattern
     *            Pattern of the error message, which occurs when a user uploaded too big file. ({0} maxFileSize, {1}
     *            fileSize, {2} fileName)
     */
    public void setSizeErrorMsgPattern(String pattern) {
        smartUpload.setSizeErrorMsgPattern(pattern);
    }

    /**
     * 
     * @param maxVisibleRows
     *            The number of rows which after the upload queue table renders a scrollbar.
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

    /**
     * Sets valid mime types.
     * 
     * See http://reference.sitepoint.com/html/mime-types-full
     * 
     * @param mimeTypes
     *            Mime types should be accepted.
     */
    public void setAcceptedMimeTypes(List<String> mimeTypes) {
        smartUpload.setAcceptedMimeTypes(mimeTypes);
    }

    /**
     * 
     * @param pattern
     *            Pattern of the error message, which occurs when a user uploaded a file that is not match to the given
     *            mime types. ({0} fileName)
     */
    public void setMimeTypeErrorMsgPattern(String pattern) {
        smartUpload.setMimeTypeErrorMsgPattern(pattern);
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

    @Override
    public void attach() {
        super.attach();
        uploadStatePanel.getWindow().addPanel(uploadStatePanel);
    }

    @Override
    public void detach() {
        if (uploadStatePanel.hasUploadInProgress()) {
            interruptAll();
            Notification.show(uploadStatePanel.getCaption(), interruptedMsg, Notification.Type.WARNING_MESSAGE);
        }

        super.detach();
        uploadStatePanel.getWindow().removePanel(uploadStatePanel);
    }

    public void setIndeterminate(final boolean indeterminate) {
        this.getUploadStatePanel().getCurrentUploadingLayout().setIndeterminate(indeterminate);
        this.setOverallProgressVisible(!indeterminate);
    }

    public void setOverallProgressVisible(final boolean overallProgressVisible) {
        this.getUploadStatePanel().getWindow().setOverallProgressVisible(overallProgressVisible);
    }
}

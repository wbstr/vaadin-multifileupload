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
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Notification;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.SmartMultiUpload;
import java.util.List;

/**
 * @author gergo
 */
public class MultiFileUpload extends CustomComponent {

    private SmartMultiUpload smartUpload;
    private UploadStatePanel uploadStatePanel;
    private String interruptedMsg = "All uploads have been interrupted.";

    public MultiFileUpload(UploadStartedHandler uploadStartedHandler, UploadFinishedHandler uploadFinishedHandler, UploadStateWindow uploadStateWindow, boolean multiple) {
        uploadStatePanel = createStatePanel(uploadStateWindow);
        initSmartUpload(uploadStartedHandler, uploadFinishedHandler, multiple);
    }

    public MultiFileUpload(UploadFinishedHandler uploadFinishedHandler, UploadStateWindow uploadStateWindow, boolean multiple) {
        this(null, uploadFinishedHandler, uploadStateWindow, multiple);
    }

    public MultiFileUpload(UploadFinishedHandler uploadFinishedHandler, UploadStateWindow uploadStateWindow) {
        this(uploadFinishedHandler, uploadStateWindow, true);
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

    /**
     *
     * @param component Wraps the given component to be a drop area for file upload. Only supported for HTML5 multiple
     * upload.
     * @return wrapped component
     */
    public DragAndDropWrapper createDropComponent(Component component) {
        return smartUpload.createDropComponent(component);
    }

    protected UploadStatePanel createStatePanel(UploadStateWindow uploadStateWindow) {
        return new UploadStatePanel(uploadStateWindow);
    }

    private void initSmartUpload(UploadStartedHandler uploadStartedHandler, UploadFinishedHandler uploadFinishedHandler, boolean multiple) {
        smartUpload = new SmartMultiUpload(uploadStatePanel, multiple);

        uploadStatePanel.setMultiUpload(smartUpload);
        uploadStatePanel.setStartedHandler(uploadStartedHandler);
        uploadStatePanel.setFinishedHandler(uploadFinishedHandler);

        setCompositionRoot(smartUpload);
    }

    public void setUploadButtonCaptions(String singleUploadCaption, String multiUploadCaption) {
        smartUpload.setUploadButtonCaptions(singleUploadCaption, multiUploadCaption);
    }

    public void setUploadButtonIcon(Resource icon) {
        smartUpload.setUploadButtonIcon(icon);
    }

    public void addUploadButtonStyleName(String styleName) {
        smartUpload.addUploadButtonStyleName(styleName);
    }

    public void removeUploadButtonStyleName(String styleName) {
        smartUpload.removeUploadButtonStyleName(styleName);
    }

    public void interruptAll() {
        uploadStatePanel.interruptAll();
    }

    public void setMaxFileSize(long maxFileSizeInBytes) {
        smartUpload.setMaxFileSize(maxFileSizeInBytes);
    }

    public long getMaxFileSize() {
        return smartUpload.getMaxFileSize();
    }

    public void setMaxFileCount(int maxFileCount) {
        smartUpload.setMaxFileCount(maxFileCount);
    }

    public int getMaxFileCount() {
        return smartUpload.getMaxFileCount();
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
     * @param pattern Pattern of the error message, which occurs when a user uploaded too big file. ({0} maxFileSize,
     * {1} fileSize, {2} fileName)
     */
    public void setSizeErrorMsgPattern(String pattern) {
        smartUpload.setSizeErrorMsgPattern(pattern);
    }

    /**
     * @param maxVisibleRows The number of rows which after the upload queue grid renders a scrollbar.
     */
    public void setMaxVisibleRows(int maxVisibleRows) {
        uploadStatePanel.getUploadQueueGrid().setMaxVisibleRows(maxVisibleRows);
    }

    /**
     * Sets mime types that browser should let users upload. This check is done by the client side and not supported by
     * all browsers. Some browser us the accept filter just as a initial filter for their file chooser dialog. Note that
     * using this method does not invalidate need for server side checks.
     * <p>
     * See https://developer.mozilla.org/en-US/docs/HTML/Element/Input
     *
     * @param accept
     */
    public void setAcceptFilter(String accept) {
        smartUpload.setAcceptFilter(accept);
    }

    /**
     * Sets valid mime types.
     * <p>
     * See http://reference.sitepoint.com/html/mime-types-full
     *
     * @param mimeTypes Mime types should be accepted.
     */
    public void setAcceptedMimeTypes(List<String> mimeTypes) {
        smartUpload.setAcceptedMimeTypes(mimeTypes);
    }

    /**
     * @param pattern Pattern of the error message, which occurs when a user uploaded a file that is not match to the
     * given mime types. ({0} fileName)
     */
    public void setMimeTypeErrorMsgPattern(String pattern) {
        smartUpload.setMimeTypeErrorMsgPattern(pattern);
    }

    public AllUploadFinishedHandler getAllUploadFinishedHandler() {
        return uploadStatePanel.getAllUploadFinishedHandler();
    }

    /**
     * Invokes when all files have been uploaded.
     *
     * @param allUploadFinishedHandler
     */
    public void setAllUploadFinishedHandler(AllUploadFinishedHandler allUploadFinishedHandler) {
        uploadStatePanel.setAllUploadFinishedHandler(allUploadFinishedHandler);
    }

    /**
     * Make ProgressBar indeterminate.
     *
     * @param indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        getUploadStatePanel().getCurrentUploadingLayout().setIndeterminate(indeterminate);
        setOverallProgressVisible(!indeterminate);
    }

    /**
     * Display an overall percentage progress of currently uploading files.
     *
     * @param overallProgressVisible
     */
    public void setOverallProgressVisible(boolean overallProgressVisible) {
        getUploadStatePanel().getWindow().setOverallProgressVisible(overallProgressVisible);
    }

    @Override
    public void focus() {
        smartUpload.focus();
    }

    public int getTabIndex() {
        return smartUpload.getTabIndex();
    }

    public void setTabIndex(int tabIndex) {
        smartUpload.setTabIndex(tabIndex);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        smartUpload.setEnabled(enabled);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
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
}

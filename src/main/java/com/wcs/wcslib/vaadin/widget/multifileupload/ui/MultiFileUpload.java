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
     * @param pattern Pattern of the error message, which occurs when a user uploaded too big file.
     *  ({0} maxFileSize, {1} fileSize, {2} fileName)
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
}

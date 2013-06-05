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
}

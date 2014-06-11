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

import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.FileDetail;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.MultiUploadHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.SmartMultiUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.UploadUtil;
import com.wcs.wcslib.vaadin.widget.multifileupload.receiver.DefaultUploadReceiver;
import com.wcs.wcslib.vaadin.widget.multifileupload.receiver.UploadReceiver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author gergo
 */
public class UploadStatePanel extends Panel implements MultiUploadHandler {

    private static final Logger logger = Logger.getLogger(UploadStatePanel.class.getName());
    private static final String PANEL_STLYE_CLASS = "multiple-upload-state-panel";
    private List<FileDetailBean> uploadQueue = new ArrayList<>();
    private UploadStateLayout currentUploadingLayout;
    private final UploadStateWindow window;
    private SmartMultiUpload multiUpload;
    private UploadReceiver receiver;
    private UploadFinishedHandler finishedHandler;
    private AllFilesUploadedHandler allFilesUploadedHandler;
    private UploadQueueTable table = new UploadQueueTable();
    private boolean firstStreamed = false;

    public UploadStatePanel(UploadStateWindow window) {
        this(window, new DefaultUploadReceiver());
    }

    public UploadStatePanel(final UploadStateWindow window, final UploadReceiver uploadReceiver) {
        this.window = window;
        this.receiver = uploadReceiver;
        setVisible(false);
        setStyleName(PANEL_STLYE_CLASS);

        this.window.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(final Window.CloseEvent closeEvent) {
                if (!uploadQueue.isEmpty()) {
                    UploadStatePanel.this.window.getConfirmDialog().show();
                }
            }
        });

        createLayout();
    }

    private void createLayout() {
        VerticalLayout panelLayout = new VerticalLayout();
        setContent(panelLayout);
        panelLayout.setMargin(false);
        currentUploadingLayout = new UploadStateLayout(this);
        panelLayout.addComponent(currentUploadingLayout);
        panelLayout.addComponent(table);
    }

    private boolean isValidFileSize(StreamVariable.StreamingStartEvent event) {
        if (event.getContentLength() > multiUpload.getMaxFileSize() || event.getContentLength() <= 0) {
            //the client side file size check may not work in old browsers
            interruptUpload(uploadQueue.get(0));
            String formattedErrorMsg = UploadUtil.getSizeErrorMessage(multiUpload.getSizeErrorMsg(),
                    multiUpload.getMaxFileSize(), (int) event.getContentLength(), event.getFileName());
            Notification.show(formattedErrorMsg, Notification.Type.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isValidMimeType(StreamVariable.StreamingStartEvent event) {
        if (multiUpload.getAcceptedMimeTypes() != null && !multiUpload.getAcceptedMimeTypes().isEmpty()
                && !multiUpload.getAcceptedMimeTypes().contains(event.getMimeType())) {
            logger.log(Level.FINE, "Mime type is not valid! File name: {0}, Mime type: {1}",
                    new Object[] { event.getFileName(), event.getMimeType() });

            interruptUpload(uploadQueue.get(0));
            String formattedErrorMsg = UploadUtil.getMimeTypeErrorMessage(multiUpload.getMimeTypeErrorMsgPattern(),
                    event.getFileName());
            Notification.show(formattedErrorMsg, Notification.Type.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void streamingStarted(StreamVariable.StreamingStartEvent event) {
        if (!uploadQueue.isEmpty()) {
            if (!isValidFileSize(event) || !isValidMimeType(event)) {
                return;
            }

            if (firstStreamed) {
                currentUploadingLayout.startStreaming(uploadQueue.get(0));
            } else {
                firstStreamed = true;
            }
        }
    }

    @Override
    public void streamingFinished(StreamVariable.StreamingEndEvent event) {
        try {
            removeFromQueue(currentUploadingLayout.getFileDetailBean());
            InputStream stream = receiver.getStream();
            //"simple" Upload fires Upload.FinishedEvent on interruptUpload()
            if (stream != null) {
                if (finishedHandler != null) {
                    finishedHandler.handleFile(stream, event.getFileName(), event.getMimeType(),
                            event.getBytesReceived());
                }

            }
            if (uploadQueue.isEmpty()) {
                if (allFilesUploadedHandler != null) {
                    allFilesUploadedHandler.uploaded();
                }
                window.hide();
                window.cleanTotals();
            }
        } finally {
            receiver.deleteTempFile();
        }
    }

    @Override
    public OutputStream getOutputStream() {
        return receiver.receiveUpload();
    }

    @Override
    public void streamingFailed(StreamVariable.StreamingErrorEvent event) {
        //        Logger.getLogger(getClass().getName()).log(Level.FINE,
        //                "Streaming failed", event.getException());
        receiver.deleteTempFile();
    }

    @Override
    public void onProgress(StreamVariable.StreamingProgressEvent event) {
        long difference = event.getBytesReceived() - currentUploadingLayout.getFileDetailBean().getBytesReceived();
        currentUploadingLayout.setProgress(event.getBytesReceived(), event.getContentLength());
        currentUploadingLayout.getFileDetailBean().setBytesReceived(event.getBytesReceived());
        window.setTotalBytesReceived(window.getTotalBytesReceived() + difference);
    }

    @Override
    public void filesQueued(Collection<FileDetail> pendingFileNames) {
        long totalContentLength = 0;
        for (FileDetail fileDetail : pendingFileNames) {
            uploadQueue.add(new FileDetailBean(fileDetail, this));
            totalContentLength += fileDetail.getContentLength();
        }
        table.refreshContainerDatasource(uploadQueue);
        window.setTotalContentLength(window.getTotalContentLength() + totalContentLength);
        currentUploadingLayout.startStreaming(uploadQueue.get(0));
        this.show();
    }

    private void show() {
        setVisible(true);
        window.show();
    }

    private void hide() {
        setVisible(false);
        window.hide();
    }

    public void removeFromQueue(FileDetailBean fileDetail) {
        uploadQueue.remove(fileDetail);
        table.refreshContainerDatasource(uploadQueue);
        if (uploadQueue.isEmpty()) {
            this.hide();
        }
    }

    public SmartMultiUpload getMultiUpload() {
        return multiUpload;
    }

    public void setMultiUpload(SmartMultiUpload multiUpload) {
        this.multiUpload = multiUpload;
    }

    public void setFinishedHandler(UploadFinishedHandler finishedHandler) {
        this.finishedHandler = finishedHandler;
    }

    public void setAllFilesUploadedHandler(final AllFilesUploadedHandler allFilesUploadedHandler) {
        this.allFilesUploadedHandler = allFilesUploadedHandler;
    }

    public UploadStateWindow getWindow() {
        return window;
    }

    public void interruptUpload(FileDetailBean fileDetail) {
        multiUpload.interruptUpload(fileDetail.getId());
        removeFromQueue(fileDetail);
        window.setTotalContentLength(window.getTotalContentLength() - fileDetail.getContentLength());
        window.setTotalBytesReceived(window.getTotalBytesReceived() - fileDetail.getBytesReceived());
    }

    public void interruptAll() {
        for (int i = uploadQueue.size() - 1; i >= 0; i--) {
            interruptUpload(uploadQueue.get(i));
        }
        window.hide();
        window.cleanTotals();
    }

    public UploadQueueTable getTable() {
        return table;
    }

    public boolean hasUploadInProgress() {
        return !uploadQueue.isEmpty();
    }

    public UploadStateLayout getCurrentUploadingLayout() {
        return this.currentUploadingLayout;
    }
}

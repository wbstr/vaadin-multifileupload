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
package com.wcs.wcslib.vaadin.widget.multifileupload.component;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.StreamVariable.StreamingProgressEvent;
import com.vaadin.server.StreamVariable.StreamingStartEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.LegacyComponent;
import java.util.ListIterator;

/**
 * Server side component for the VMultiUpload widget. Pretty much hacked up together to test new Receiver support in the
 * GWT terminal.
 *
 *
 * This is a modified version of org.vaadin.easyuploads.MultiUpload.java which is part of the EasyUploads 7.0.0 Vaadin
 * addon.
 *
 */
@SuppressWarnings("serial")
public class MultiUpload extends AbstractComponent implements LegacyComponent, UploadComponent {

    List<FileDetail> pendingFiles = new ArrayList<FileDetail>();
    private Long removedFileId;
    private MultiUploadHandler receiver;
    private String buttonCaption = "...";
    private boolean uploading;
    private boolean ready;
    private boolean interrupted = false;
    private long maxFileSize;
    private String sizeErrorMsg;
    private String acceptFilter;
    StreamVariable streamVariable = new StreamVariable() {
        @Override
        public void streamingStarted(StreamingStartEvent event) {
            uploading = true;
            interrupted = false;
            final FileDetail next = getPendingFileNames().iterator().next();
            receiver.streamingStarted(new StreamingStartEvent() {
                @Override
                public String getMimeType() {
                    return next.getMimeType();
                }

                @Override
                public String getFileName() {
                    return next.getFileName();
                }

                @Override
                public long getContentLength() {
                    return next.getContentLength();
                }

                @Override
                public long getBytesReceived() {
                    return 0;
                }

                @Override
                public void disposeStreamVariable() {
                }
            });
        }

        @Override
        public void streamingFinished(final StreamingEndEvent event) {
            uploading = false;
            interrupted = false;
            final FileDetail next = getPendingFileNames().iterator().next();

            receiver.streamingFinished(new StreamingEndEvent() {
                @Override
                public String getMimeType() {
                    return next.getMimeType();
                }

                @Override
                public String getFileName() {
                    return next.getFileName();
                }

                @Override
                public long getContentLength() {
                    return next.getContentLength();
                }

                @Override
                public long getBytesReceived() {
                    return event.getBytesReceived();
                }
            });
            pendingFiles.remove(0);
        }

        @Override
        public void streamingFailed(StreamingErrorEvent event) {
            uploading = false;
            interrupted = false;
            receiver.streamingFailed(event);
            pendingFiles.remove(0);
        }

        @Override
        public void onProgress(StreamingProgressEvent event) {
            receiver.onProgress(event);
        }

        @Override
        public boolean listenProgress() {
            return true;
        }

        @Override
        public boolean isInterrupted() {
            return interrupted;
        }

        @Override
        public OutputStream getOutputStream() {
            return receiver.getOutputStream();
        }
    };

    public void setHandler(MultiUploadHandler receiver) {
        this.receiver = receiver;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addVariable(this, "target", streamVariable);
        target.addAttribute("maxFileSize", maxFileSize);
        target.addAttribute("sizeErrorMsg", sizeErrorMsg);
        target.addAttribute("acceptFilter", acceptFilter);
        if (ready) {
            target.addAttribute("ready", true);
            ready = false;
        }
        target.addAttribute("buttoncaption", getButtonCaption());
        if (removedFileId != null) {
            target.addAttribute("removedFileId", removedFileId.longValue());
            removedFileId = null;
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey("filequeue")) {
            String[] filequeue = (String[]) variables.get("filequeue");
            List<FileDetail> newFiles = new ArrayList<FileDetail>(filequeue.length);
            for (String string : filequeue) {
                newFiles.add(new FileDetail(string));
            }

            pendingFiles.addAll(newFiles);
            receiver.filesQueued(newFiles);

            markAsDirty();
            if (!isUploading()) {
                ready = true;
            }
        }
    }

    @Override
    public void interruptUpload(long fileId) {
        int ndx = 0;
        for (ListIterator<FileDetail> it = pendingFiles.listIterator(); it.hasNext();) {
            FileDetail fileDetail = it.next();
            if (fileDetail.getId() == fileId) {
                if (ndx == 0) {
                    interrupted = true;
                    return;
                } else {
                    it.remove();
                    removedFileId = fileId;
                    markAsDirty();
                    return;
                }
            }
            ndx++;
        }
    }

    public Collection<FileDetail> getPendingFileNames() {
        return Collections.unmodifiableCollection(pendingFiles);
    }

    @Override
    public void setButtonCaption(String buttonCaption) {
        this.buttonCaption = buttonCaption;
    }

    public String getButtonCaption() {
        return buttonCaption;
    }

    public boolean isUploading() {
        return uploading;
    }

    @Override
    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void setSizeErrorMsgPattern(String sizeErrorMsg) {
        this.sizeErrorMsg = sizeErrorMsg;
    }

    @Override
    public void setAcceptFilter(String acceptFilter) {
        this.acceptFilter = acceptFilter;
    }
}

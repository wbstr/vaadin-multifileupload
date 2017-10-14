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

import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Upload;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author gergo
 */
@Slf4j
public class SimpleFileUploadListener implements
    Upload.StartedListener, Upload.ProgressListener, Upload.FailedListener, Upload.FinishedListener {

    private final MultiUploadHandler handler;

    public SimpleFileUploadListener(MultiUploadHandler handler) {
        this.handler = handler;
    }

    private Collection<FileDetail> getFileDetails(Upload.StartedEvent event) {
        log.debug("getFileDetails");
        List<FileDetail> files = new ArrayList<>();
        FileDetail fileDetail = new FileDetail(
            event.getFilename(),
            event.getMIMEType(),
            (int) event.getContentLength());
        files.add(fileDetail);
        return Collections.unmodifiableCollection(files);
    }

    @Override
    public void uploadStarted(final Upload.StartedEvent event) {
        log.debug("uploadStarted");
        handler.filesQueued(getFileDetails(event));

        handler.streamingStarted(new StreamVariable.StreamingStartEvent() {
            @Override
            public void disposeStreamVariable() {
                log.debug("streamingStarted.disposeStreamVariable");
            }

            @Override
            public String getFileName() {
                log.debug("streamingStarted.getFileName : {}", event.getFilename());
                return event.getFilename();
            }

            @Override
            public String getMimeType() {
                log.debug("streamingStarted.getMimeType : {}", event.getMIMEType());
                return event.getMIMEType();
            }

            @Override
            public long getContentLength() {
                log.debug("streamingStarted.getContentLength : {}", event.getContentLength());
                return event.getContentLength();
            }

            @Override
            public long getBytesReceived() {
                log.debug("streamingStarted.getBytesReceived : {}", event.getUpload().getBytesRead());
                return event.getUpload().getBytesRead();
            }
        });
    }

    @Override
    public void updateProgress(final long readBytes, final long contentLength) {
        log.debug("updateProgress");
        handler.onProgress(new StreamVariable.StreamingProgressEvent() {
            @Override
            public String getFileName() {
                log.debug("updateProgress.getFileName");
                return "";
            }

            @Override
            public String getMimeType() {
                log.debug("updateProgress.getMimeType");
                return "";
            }

            @Override
            public long getContentLength() {
                log.debug("updateProgress.getContentLength : {}", contentLength);
                return contentLength;
            }

            @Override
            public long getBytesReceived() {
                log.debug("updateProgress.getBytesReceived : {}", readBytes);
                return readBytes;
            }
        });
    }

    @Override
    public void uploadFailed(final Upload.FailedEvent event) {
        log.debug("uploadFailed");
        handler.streamingFailed(new StreamVariable.StreamingErrorEvent() {
            @Override
            public Exception getException() {
                log.debug("uploadFailed.getException : {}", event.getReason());
                return event.getReason();
            }

            @Override
            public String getFileName() {
                log.debug("uploadFailed.getFileName : {}", event.getFilename());
                return event.getFilename();
            }

            @Override
            public String getMimeType() {
                log.debug("uploadFailed.getMimeType : {}", event.getMIMEType());
                return event.getMIMEType();
            }

            @Override
            public long getContentLength() {
                log.debug("uploadFailed.getContentLength : {}", event.getLength());
                return event.getLength();
            }

            @Override
            public long getBytesReceived() {
                log.debug("uploadFailed.getBytesReceived : {}", event.getUpload().getBytesRead());
                return event.getUpload().getBytesRead();
            }
        });
    }

    @Override
    public void uploadFinished(final Upload.FinishedEvent event) {
        log.debug("uploadFinished");
        handler.streamingFinished(new StreamVariable.StreamingEndEvent() {
            @Override
            public String getFileName() {
                log.debug("uploadFinished.getFileName : {}", event.getFilename());
                return event.getFilename();
            }

            @Override
            public String getMimeType() {
                log.debug("uploadFinished.getMimeType : {}", event.getMIMEType());
                return event.getMIMEType();
            }

            @Override
            public long getContentLength() {
                log.debug("uploadFinished.getContentLength : {}", event.getLength());
                return event.getLength();
            }

            @Override
            public long getBytesReceived() {
                log.debug("uploadFinished.getBytesReceived : {}", event.getUpload().getUploadSize());
                return event.getUpload().getUploadSize();
            }
        });
    }
}

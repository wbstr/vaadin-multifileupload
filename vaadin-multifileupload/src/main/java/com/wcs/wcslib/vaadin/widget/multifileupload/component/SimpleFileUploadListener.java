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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gergo
 */
public class SimpleFileUploadListener implements
        Upload.StartedListener, Upload.ProgressListener, Upload.FailedListener, Upload.FinishedListener {
    private static final long serialVersionUID = 6054389583426914366L;
    private final MultiUploadHandler handler;

    public SimpleFileUploadListener(MultiUploadHandler handler) {
        this.handler = handler;
    }

    private Collection<FileDetail> getFileDetails(Upload.StartedEvent event) {
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
        handler.filesQueued(getFileDetails(event));

        handler.streamingStarted(new StreamVariable.StreamingStartEvent() {
            @Override
            public void disposeStreamVariable() {
            }

            @Override
            public String getFileName() {
                return event.getFilename();
            }

            @Override
            public String getMimeType() {
                return event.getMIMEType();
            }

            @Override
            public long getContentLength() {
                return event.getContentLength();
            }

            @Override
            public long getBytesReceived() {
                return event.getUpload().getBytesRead();
            }
        });
    }

    @Override
    public void updateProgress(final long readBytes, final long contentLength) {
        handler.onProgress(new StreamVariable.StreamingProgressEvent() {
            @Override
            public String getFileName() {
                return "";
            }

            @Override
            public String getMimeType() {
                return "";
            }

            @Override
            public long getContentLength() {
                return contentLength;
            }

            @Override
            public long getBytesReceived() {
                return readBytes;
            }
        });
    }

    @Override
    public void uploadFailed(final Upload.FailedEvent event) {
        handler.streamingFailed(new StreamVariable.StreamingErrorEvent() {
            @Override
            public Exception getException() {
                return event.getReason();
            }

            @Override
            public String getFileName() {
                return event.getFilename();
            }

            @Override
            public String getMimeType() {
                return event.getMIMEType();
            }

            @Override
            public long getContentLength() {
                return event.getLength();
            }

            @Override
            public long getBytesReceived() {
                return event.getUpload().getBytesRead();
            }
        });
    }

    @Override
    public void uploadFinished(final Upload.FinishedEvent event) {
        handler.streamingFinished(new StreamVariable.StreamingEndEvent() {
            @Override
            public String getFileName() {
                return event.getFilename();
            }

            @Override
            public String getMimeType() {
                return event.getMIMEType();
            }

            @Override
            public long getContentLength() {
                return event.getLength();
            }

            @Override
            public long getBytesReceived() {
                return event.getUpload().getUploadSize();
            }
        });
    }
}

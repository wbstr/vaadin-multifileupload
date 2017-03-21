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

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.ui.Notification;
import java.io.OutputStream;
import java.util.*;

/**
 * Server side component for the VMultiUpload widget. Pretty much hacked up together to test new Receiver support in the
 * GWT terminal.
 * <p>
 * <p>
 * This is a modified version of org.vaadin.easyuploads.MultiUpload.java which is part of the EasyUploads 7.0.0 Vaadin
 * addon.
 */
@SuppressWarnings("serial")
public class MultiUpload extends AbstractComponent implements LegacyComponent, UploadComponent {

    private boolean focus = false;
    private int tabIndex = 0;
    List<FileDetail> pendingFiles = new ArrayList<>();
    private List<Long> interruptedFileIds = new ArrayList<>();
    private MultiUploadHandler receiver;
    private String buttonCaption = "...";
    private boolean uploading;
    private boolean ready;
    private boolean interrupted = false;
    private long maxFileSize;
    private String sizeErrorMsg;
    private String acceptFilter;
    private List<String> acceptedMimeTypes;
    private boolean enabled = true;
    private String mimeTypeErrorMsg;
    private int maxFileCount;
    private String fileCountErrorMsg;

    StreamVariable streamVariable = new StreamVariable() {
        @Override
        public void streamingStarted(StreamingStartEvent event) {
            uploading = true;
            interrupted = false;
            Iterator<FileDetail> iterator = getPendingFileNames().iterator();
            if (!iterator.hasNext()) {
                return;
            }

            final FileDetail next = iterator.next();
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
            Iterator<FileDetail> iterator = getPendingFileNames().iterator();
            if (!iterator.hasNext()) {
                return;
            }
            final FileDetail next = iterator.next();

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
            if (!pendingFiles.isEmpty()) {
                pendingFiles.remove(0);
            }
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
        if (acceptedMimeTypes != null) {
            target.addAttribute("acceptedMimeTypes", acceptedMimeTypes.toArray(new String[acceptedMimeTypes.size()]));
        } else {
            target.addAttribute("acceptedMimeTypes", new String[]{});
        }

        target.addAttribute("mimeTypeErrorMsg", mimeTypeErrorMsg);
        target.addAttribute("enabled", enabled);
        if (ready) {
            target.addAttribute("ready", true);
            ready = false;
        }
        target.addAttribute("buttoncaption", getButtonCaption());
        if (!interruptedFileIds.isEmpty()) {
            target.addAttribute("interruptedFileIds", interruptedFileIds.toArray(new Long[interruptedFileIds.size()]));
            interruptedFileIds = new ArrayList<>();
        }
        if (focus) {
            target.addAttribute("focus", true);
            focus = false;
        }
        if (tabIndex >= 0) {
            target.addAttribute("tabindex", tabIndex);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey("filequeue")) {
            String[] fileQueue = (String[]) variables.get("filequeue");
            List<FileDetail> newFiles = new ArrayList<>(fileQueue.length);

            for (String string : fileQueue) {
                if (pendingFiles.size() + newFiles.size() >= maxFileCount) {
                    Notification.show(UploadUtil.formatErrorMessage(fileCountErrorMsg, maxFileCount), Notification.Type.WARNING_MESSAGE);
                    break;
                }
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
                    interruptedFileIds.add(fileId);
                    markAsDirty();
                    return;
                }
            }
            ndx++;
        }
    }

    public void registerDropComponent(MultiUploadDropHandler dropHandler) {
        dropHandler.addFilesReceivedListener((List<Html5File> html5Files) -> {
            final List<FileDetail> newFiles = new ArrayList<>();
            for (Html5File html5File : html5Files) {
                if (pendingFiles.size() + newFiles.size() >= maxFileCount) {
                    Notification.show(UploadUtil.formatErrorMessage(fileCountErrorMsg, maxFileCount), Notification.Type.WARNING_MESSAGE);
                    break;
                }
                html5File.setStreamVariable(streamVariable);
                newFiles.add(new FileDetail(html5File.getFileName(), html5File.getType(), html5File.getFileSize()));
            }
            pendingFiles.addAll(newFiles);
            receiver.filesQueued(newFiles);

            markAsDirty();
            if (!isUploading()) {
                ready = true;
            }
        });
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
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }

    @Override
    public void setSizeErrorMsgPattern(String sizeErrorMsg) {
        this.sizeErrorMsg = sizeErrorMsg;
    }

    @Override
    public void setAcceptFilter(String acceptFilter) {
        this.acceptFilter = acceptFilter;
    }

    @Override
    public void setAcceptedMimeTypes(List<String> acceptedMimeTypes) {
        this.acceptedMimeTypes = acceptedMimeTypes;
    }

    @Override
    public void setMimeTypeErrorMsgPattern(String pattern) {
        this.mimeTypeErrorMsg = pattern;
    }

    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

    public void setFileCountErrorMsgPattern(String pattern) {
        this.fileCountErrorMsg = pattern;
    }

    @Override
    public void focus() {
        focus = true;
    }

    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }
}

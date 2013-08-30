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

import com.vaadin.server.WebBrowser;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Upload;
import java.io.OutputStream;

/**
 *
 * @author gergo
 */
public class SmartMultiUpload extends CustomComponent {

    private static final String DEFAULT_UPLOAD_BUTTON_CAPTION = "...";
    private static final int MAXIMUM_FILE_SIZE = 1073741824; //1GB
    private MultiUploadHandler handler;
    private UploadComponent upload;
    private WebBrowser webBrowser;
    private String singleUploadCaption = DEFAULT_UPLOAD_BUTTON_CAPTION;
    private String multiUploadCaption = DEFAULT_UPLOAD_BUTTON_CAPTION;
    private boolean multiple;
    private int maxFileSize = Integer.MAX_VALUE;
    private String sizeErrorMsgPattern = "File is too big:";
    private String acceptFilter = "";
    private boolean enabled = true;

    public SmartMultiUpload(MultiUploadHandler handler, final boolean multiple) {
        this.handler = handler;
        this.multiple = multiple;
    }

    @Override
    public void attach() {
        super.attach();
        webBrowser = getUI().getPage().getWebBrowser();
        createUpload(multiple);
        setCompositionRoot(upload);
        initUploadButtonCaptions();
        initMaxFileSize();
        initSizeErrorMsg();
        initAcceptFilter();
        initEnabled();
    }

    public void setUploadButtonCaptions(String singleUploadCaption, String multiUploadCaption) {
        this.singleUploadCaption = singleUploadCaption;
        this.multiUploadCaption = multiUploadCaption;
        initUploadButtonCaptions();
    }

    public UploadComponent getUpload() {
        return upload;
    }

    public void interruptUpload(long fileId) {
        if (upload != null) {
            upload.interruptUpload(fileId);
        }
    }

    private void initUploadButtonCaptions() {
        if (upload instanceof MultiUpload) {
            upload.setButtonCaption(multiUploadCaption);
        } else if (upload instanceof Upload) {
            upload.setButtonCaption(singleUploadCaption);
        }
    }

    private void createUpload(boolean multiple) {
        if (!multiple || isBrowserNotHtml5Capable()) {
            initSingleUpload();
        } else {
            initMultiUpload();
        }
    }

    private void initMultiUpload() {
        upload = new MultiUpload();
        MultiUpload multiUpload = (MultiUpload) upload;
        multiUpload.setHandler(handler);
        multiUpload.setImmediate(true);
    }

    private void initSingleUpload() {
        upload = new CustomUpload();
        Upload singleUpload = (Upload) upload;
        singleUpload.setReceiver(new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
                return handler.getOutputStream();
            }
        });
        singleUpload.setImmediate(true);

        SimpleFileUploadListener uploadEventListener = new SimpleFileUploadListener(handler);
        singleUpload.addStartedListener(uploadEventListener);
        singleUpload.addProgressListener(uploadEventListener);
        singleUpload.addFailedListener(uploadEventListener);
        singleUpload.addFinishedListener(uploadEventListener);
    }

    private boolean isBrowserNotHtml5Capable() {
        return webBrowser.isOpera()
                || (webBrowser.isIE())
                || webBrowser.isTooOldToFunctionProperly();
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = Math.min(maxFileSize, MAXIMUM_FILE_SIZE);
        initMaxFileSize();
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public String getSizeErrorMsg() {
        return sizeErrorMsgPattern;
    }

    public void setSizeErrorMsgPattern(String sizeErrorMsgPattern) {
        this.sizeErrorMsgPattern = sizeErrorMsgPattern;
        initSizeErrorMsg();
    }

    private void initSizeErrorMsg() {
        if (upload != null) {
            upload.setSizeErrorMsgPattern(sizeErrorMsgPattern);
        }
    }

    private void initMaxFileSize() {
        if (upload != null) {
            upload.setMaxFileSize(maxFileSize);
        }
    }

    public String getAcceptFilter() {
        return acceptFilter;
    }

    public void setAcceptFilter(String acceptFilter) {
        this.acceptFilter = acceptFilter;
        initAcceptFilter();
    }

    private void initAcceptFilter() {
        if (upload != null) {
            upload.setAcceptFilter(acceptFilter);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
        initEnabled();
    }

    private void initEnabled() {
        if (upload != null) {
            upload.setEnabled(enabled);
        }
    }
}

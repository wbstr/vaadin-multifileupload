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
package com.wcs.wcslib.vaadin.widget.multifileupload.client;

import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.VUpload;
import com.vaadin.client.ui.dd.VHtml5File;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Node;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.Icon;
import java.util.List;

/**
 *
 * @author gergo
 */
public class VCustomUpload extends VUpload {

    private long maxFileSize;
    private String sizeErrorMsg;
    private String mimeTypeErrorMsg;
    private List<String> acceptedMimeTypes;
    private InputElement input;
    public Icon icon;

    @Override
    public void submit() {
        if (checkSize()) {
            super.submit();
        } else {
            ((InputElement) fu.getElement().cast()).setValue(null);
        }
    }

    private boolean isValidFileSize(VHtml5File file) {
        if (file.getSize() > maxFileSize || file.getSize() <= 0) {
            String formattedErrorMsg = UploadClientUtil.getSizeErrorMessage(
                    sizeErrorMsg, maxFileSize, file.getSize(), file.getName());
            VNotification.createNotification(1000,
                    client.getUIConnector().getWidget()).show(formattedErrorMsg, VNotification.CENTERED, "warning");
            return false;
        }
        return true;
    }

    private boolean isValidMimeType(VHtml5File file) {
        if (acceptedMimeTypes != null && !acceptedMimeTypes.isEmpty() && !acceptedMimeTypes.contains(file.getType())) {
            String formattedErrorMsg = UploadClientUtil.getMimeTypeErrorMessage(mimeTypeErrorMsg, file.getName());
            VNotification.createNotification(1000,
                    client.getUIConnector().getWidget()).show(formattedErrorMsg, VNotification.CENTERED, "warning");
            return false;
        }
        return true;
    }

    private boolean checkSize() {
        try {
            InputElement ie = (InputElement) fu.getElement().cast();
            JsArray<VHtml5File> files = getFiles(ie);
            for (int i = 0; i < files.length(); i++) {
                VHtml5File file = files.get(i);
                if (!isValidFileSize(file) || !isValidMimeType(file)) {
                    return false;
                }
            }
        } catch (Exception e) {
            VConsole.error("Detecting file size failed");
        }
        return true;
    }

    private static native final JsArray<VHtml5File> getFiles(InputElement ie) /*-{
     return ie.files;
     }-*/;

    private InputElement getInput() {
        if (input == null || !getElement().isOrHasChild((Node) input)) {
            input = fu.getElement().cast();
        }
        return input;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setSizeErrorMsg(String sizeErrorMsg) {
        this.sizeErrorMsg = sizeErrorMsg;
    }

    public void setAcceptFilter(String acceptFilter) {
        getInput().setAccept(acceptFilter);
    }

    public void setMimeTypeErrorMsg(String mimeTypeErrorMsg) {
        this.mimeTypeErrorMsg = mimeTypeErrorMsg;
    }

    public void setAcceptedMimeTypes(List<String> acceptedMimeTypes) {
        this.acceptedMimeTypes = acceptedMimeTypes;
    }
}

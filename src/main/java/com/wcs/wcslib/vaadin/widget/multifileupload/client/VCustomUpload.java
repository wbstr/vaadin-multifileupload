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

/**
 *
 * @author gergo
 */
public class VCustomUpload extends VUpload {

    private int maxFileSize;
    private String sizeErrorMsg;
    private InputElement input;

    @Override
    public void submit() {
        if (checkSize(maxFileSize)) {
            super.submit();
        } else {
            ((InputElement) fu.getElement().cast()).setValue(null);
        }
    }

    private boolean checkSize(int maxSize) {
        try {
            InputElement ie = (InputElement) fu.getElement().cast();
            JsArray<VHtml5File> files = getFiles(ie);
            for (int i = 0; i < files.length(); i++) {
                VHtml5File file = files.get(i);
                if (file.getSize() > maxSize || file.getSize() <= 0) {
                    String formattedErrorMsg = UploadClientUtil.getSizeErrorMessage(
                            sizeErrorMsg, maxFileSize, file.getSize(), file.getName());
                    VNotification.createNotification(1000,
                            client.getUIConnector().getWidget()).show(formattedErrorMsg, VNotification.CENTERED, "warning");
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

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setSizeErrorMsg(String sizeErrorMsg) {
        this.sizeErrorMsg = sizeErrorMsg;
    }

    public void setAcceptFilter(String acceptFilter) {
        getInput().setAccept(acceptFilter);
    }
}

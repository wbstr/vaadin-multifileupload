package com.wcs.wcslib.vaadin.widget.multifileupload.client;

import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.VUpload;
import com.vaadin.client.ui.dd.VHtml5File;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.InputElement;
import com.vaadin.client.VConsole;
import java.text.MessageFormat;

/**
 *
 * @author gergo
 */
public class VCustomUpload extends VUpload {

    private int maxFileSize;
    private String sizeErrorMsg;

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

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setSizeErrorMsg(String sizeErrorMsg) {
        this.sizeErrorMsg = sizeErrorMsg;
    }
}

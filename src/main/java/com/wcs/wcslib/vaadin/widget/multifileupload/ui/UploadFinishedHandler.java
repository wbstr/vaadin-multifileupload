package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

import java.io.InputStream;

/**
 *
 * @author gergo
 */
public interface UploadFinishedHandler {

    public void handleFile(InputStream stream, String fileName, String mimeType, long length);
}

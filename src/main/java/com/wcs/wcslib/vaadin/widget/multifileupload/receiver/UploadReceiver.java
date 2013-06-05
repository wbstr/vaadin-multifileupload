package com.wcs.wcslib.vaadin.widget.multifileupload.receiver;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author gergo
 */
public interface UploadReceiver {

    public OutputStream receiveUpload();

    public void deleteTempFile();

    public InputStream getStream();
}

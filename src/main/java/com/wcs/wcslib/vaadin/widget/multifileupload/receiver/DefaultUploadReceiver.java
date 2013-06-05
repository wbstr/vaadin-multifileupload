package com.wcs.wcslib.vaadin.widget.multifileupload.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author gergo
 */
public class DefaultUploadReceiver implements UploadReceiver {

    private static final String TEMPFILE_NAME_PREFIX = "upload_tmpfile_";
    private File file;

    @Override
    public OutputStream receiveUpload() {
        try {
            if (file == null) {
                file = createTempFile();
                file.deleteOnExit();
            }
            return new FileOutputStream(file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createTempFile() {
        final String tempFileName = TEMPFILE_NAME_PREFIX + System.currentTimeMillis();
        try {
            return File.createTempFile(tempFileName, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTempFile() {
        if (file != null && file.exists()) {
            file.delete();
            file = null;
        }
    }

    @Override
    public InputStream getStream() {
        try {
            if (file != null) {
                return new FileInputStream(file);
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

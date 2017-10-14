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
package com.wcs.wcslib.vaadin.widget.multifileupload.receiver;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author gergo
 */
@Slf4j
public class DefaultUploadReceiver implements UploadReceiver {

    private static final String TEMPFILE_NAME_PREFIX = "upload_tmpfile_";
    private File file;

    @Override
    public OutputStream receiveUpload() {
        log.debug("receiveUpload");
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
        log.debug("createTempFile");
        final String tempFileName = TEMPFILE_NAME_PREFIX + System.currentTimeMillis();
        try {
            return File.createTempFile(tempFileName, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTempFile() {
        log.debug("deleteTempFile");
        if (file != null && file.exists()) {
            file.delete();
            file = null;
        }
    }

    @Override
    public InputStream getStream() {
        log.debug("getStream");
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

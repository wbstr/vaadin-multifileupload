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

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gergo
 */
@Slf4j
public class FileDetail {

    private static final AtomicLong idCounter = new AtomicLong();

    private static final String DELIMITER = "---xXx---";
    private final long id;
    private final String fileName;
    private String mimeType;
    private final long contentLength;

    public FileDetail(String data) {
        log.debug("FileDetail : {}", data);
        String[] split = data.split(DELIMITER);
        id = Long.parseLong(split[0]);
        contentLength = Long.parseLong(split[1]);
        fileName = split[2];
        if (split.length > 3) {
            mimeType = split[3];
        }
    }

    public FileDetail(String caption, String mimeType, long contentLength) {
        log.debug("FileDetail : {}", caption);
        this.id = idCounter.getAndIncrement();
        this.fileName = caption;
        this.mimeType = mimeType;
        this.contentLength = contentLength;
    }

    public long getId() {
        return id;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }
}

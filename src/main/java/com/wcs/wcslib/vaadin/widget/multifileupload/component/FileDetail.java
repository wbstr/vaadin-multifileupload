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

/**
 *
 * @author gergo
 */
public class FileDetail {

    private static final String DELIMITER = "---xXx---";
    private long id;
    private String fileName;
    private String mimeType;
    private long contentLength;

    public FileDetail(String data) {
        String[] split = data.split(DELIMITER);
        id = Long.parseLong(split[0]);
        contentLength = Long.parseLong(split[1]);
        fileName = split[2];
        if (split.length > 3) {
            mimeType = split[3];
        }
    }

    public FileDetail(String caption, String mimeType, long parseLong) {
        this.fileName = caption;
        this.mimeType = mimeType;
        this.contentLength = parseLong;
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
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
package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

import com.vaadin.server.Resource;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.FileDetail;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.UploadUtil;
import java.io.Serializable;

/**
 *
 * @author gergo
 */
public class FileDetailBean implements Serializable {
    private static final long serialVersionUID = -314729461547381486L;
    private final long id;
    private final String fileName;
    private final long contentLength;
    private long bytesReceived;
    private final String readableContentLength;
    private final Resource cancelIcon;

    public FileDetailBean(FileDetail fileDetail, UploadStatePanel uploadStatePanel) {
        this.id = fileDetail.getId();
        this.fileName = fileDetail.getFileName();
        this.contentLength = fileDetail.getContentLength();
        this.readableContentLength = UploadUtil.getHumanReadableByteCount(contentLength, false);
        this.cancelIcon = uploadStatePanel.getWindow().getCancelIconResource();
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getReadableContentLength() {
        return readableContentLength;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileDetailBean other = (FileDetailBean) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public Resource getCancelIcon() {
        return cancelIcon;
    }

}

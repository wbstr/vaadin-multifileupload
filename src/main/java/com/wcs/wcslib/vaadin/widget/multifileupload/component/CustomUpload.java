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

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.Upload;

/**
 *
 * @author gergo
 */
public class CustomUpload extends Upload implements UploadComponent {

    private long maxFileSize;
    private String sizeErrorMsg;

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("maxFileSize", maxFileSize);
        target.addAttribute("sizeErrorMsg", sizeErrorMsg);
    }

    @Override
    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void setSizeErrorMsgPattern(String sizeErrorMsg) {
        this.sizeErrorMsg = sizeErrorMsg;
    }

    @Override
    public void interruptUpload(long fileId) {
        interruptUpload();
    }
}

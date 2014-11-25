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

import java.text.MessageFormat;

/**
 *
 * @author gergo
 */
public class UploadUtil {

    public static String getHumanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getSizeErrorMessage(String pattern, Long maxFileSize, Long fileSize, String fileName) {
        return MessageFormat.format(pattern,
                getHumanReadableByteCount(maxFileSize, false),
                getHumanReadableByteCount(fileSize, false),
                fileName);
    }

    public static String getMimeTypeErrorMessage(String pattern, String fileName) {
        return MessageFormat.format(pattern, fileName);
    }
}

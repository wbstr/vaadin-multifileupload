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
package com.wcs.wcslib.vaadin.widget.multifileupload.client;

import com.google.gwt.i18n.client.NumberFormat;

/**
 *
 * @author gergo
 */
public class UploadClientUtil {

    public static String getHumanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");

        //return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
        return NumberFormat.getFormat("#.0").format(bytes / Math.pow(unit, exp)) + " " + pre + "B";
    }

    public static String getSizeErrorMessage(String pattern, Integer maxFileSize, Integer fileSize, String fileName) {
        //java.text.MessageFormat is not available on client side
        return pattern.replace("{0}", getHumanReadableByteCount(maxFileSize, false)).
                replace("{1}", getHumanReadableByteCount(fileSize, false)).
                replace("{2}", fileName);
    }
}

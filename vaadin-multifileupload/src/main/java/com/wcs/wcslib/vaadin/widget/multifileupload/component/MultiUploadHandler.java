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

import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.StreamVariable.StreamingProgressEvent;
import com.vaadin.server.StreamVariable.StreamingStartEvent;
import java.io.OutputStream;
import java.util.Collection;

/**
 *
 * @author gergo
 */
public interface MultiUploadHandler {

    void streamingStarted(StreamingStartEvent event);

    void streamingFinished(StreamingEndEvent event);

    OutputStream getOutputStream();

    void streamingFailed(StreamingErrorEvent event);

    void onProgress(StreamingProgressEvent event);

    void filesQueued(Collection<FileDetail> pendingFileNames);
}

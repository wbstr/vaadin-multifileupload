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

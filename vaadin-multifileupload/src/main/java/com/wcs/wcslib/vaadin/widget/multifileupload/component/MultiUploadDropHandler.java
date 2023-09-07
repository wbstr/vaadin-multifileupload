package com.wcs.wcslib.vaadin.widget.multifileupload.component;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Sander on 10-11-2016.
 */
public class MultiUploadDropHandler extends DragAndDropWrapper implements DropHandler {
    private static final long serialVersionUID = 8013374463449156963L;
    private final Collection<FilesReceivedListener> listeners = new ArrayList<>();

    public MultiUploadDropHandler(Component root) {
        super(root);
        setDropHandler(this);
    }

    public void addFilesReceivedListener(FilesReceivedListener filesReceivedListener) {
        listeners.add(filesReceivedListener);
    }

    public void removeFilesReceivedListener(FilesReceivedListener filesReceivedListener) {
        listeners.remove(filesReceivedListener);
    }

    @Override
    public void drop(DragAndDropEvent dropEvent) {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        WrapperTransferable tr = (WrapperTransferable) dropEvent.getTransferable();
        Html5File[] files = tr.getFiles();
        if (files != null && files.length > 0) {
            listeners.stream().forEach((filesReceivedListener) -> {
                filesReceivedListener.filesReceived(Arrays.asList(files));
            });
        }

    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }

    protected interface FilesReceivedListener {

        void filesReceived(List<Html5File> html5Files);
    }
}

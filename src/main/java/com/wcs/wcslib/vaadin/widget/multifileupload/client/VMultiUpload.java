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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.dd.VHtml5File;
import java.util.ListIterator;

/**
 * Upload counterpart with "multiple" support.
 *
 *
 * This is a modified version of org.vaadin.easyuploads.client.ui.VMultiUpload.java which is part of the EasyUploads
 * 7.0.0 Vaadin addon.
 *
 *
 * Not finished enough for extension.
 */
public class VMultiUpload extends SimplePanel implements Paintable {

    private int maxFileSize;
    private String sizeErrorMsg;
    private InputElement input;

    private final class MyFileUpload extends FileUpload {

        public MyFileUpload() {
            getElement().setAttribute("multiple", "multiple");
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (hasFiles()) {
                submit();
            }
        }

        public boolean hasFiles() {
            return getFileCount(getElement()) > 0;
        }
    }

    public static final native int getFileCount(Element el) /*-{
     return el.files.length;
     }-*/;

    public static final native VHtml5File getFile(Element el, int i) /*-{
     return el.files[i];
     }-*/;
    public static final String CLASSNAME = "v-upload";
    private static final String DELIM = "---xXx---";
    /**
     * FileUpload component that opens native OS dialog to select file.
     */
    MyFileUpload fu = new MyFileUpload();
    Panel panel = new FlowPanel();
    ApplicationConnection client;
    private String paintableId;
    /**
     * Button that initiates uploading
     */
    private final VButton submitButton;
    private boolean enabled = true;
    private String receiverUri;
    private ReadyStateChangeHandler readyStateChangeHandler = new ReadyStateChangeHandler() {
        @Override
        public void onReadyStateChange(XMLHttpRequest xhr) {
            if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                xhr.clearOnReadyStateChange();

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (isAttached() && !fileQueue.isEmpty()) {
                            client.updateVariable(paintableId, "ready", true,
                                    true);
                            postNextFileFromQueue();
                        }
                    }
                });
            }
        }
    };

    ;

	public VMultiUpload() {
        super(com.google.gwt.dom.client.Document.get().createDivElement());

        setWidget(panel);
        panel.add(fu);
        submitButton = new VButton();
        submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // fire click on upload (eg. focused button and hit space)
                fireNativeClick(fu.getElement());
            }
        });
        panel.add(submitButton);

        setStyleName(CLASSNAME);
        fu.sinkEvents(Event.ONCHANGE);
        fu.sinkEvents(Event.ONFOCUS);
        addStyleName(CLASSNAME + "-immediate");
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        addStyleName(CLASSNAME + "-immediate");


        this.client = client;
        paintableId = uidl.getId();
        receiverUri = client.translateVaadinUri(uidl.getStringVariable("target"));
        submitButton.setText(uidl.getStringAttribute("buttoncaption"));
        fu.setName(paintableId + "_file");

        if (uidl.hasAttribute("enabled")) {
            if (uidl.getBooleanAttribute("enabled")) {
                enableUpload();
            } else {
                disableUpload();
            }
        }

        if (uidl.hasAttribute("maxFileSize")) {
            maxFileSize = uidl.getIntAttribute("maxFileSize");
        }
        if (uidl.hasAttribute("sizeErrorMsg")) {
            sizeErrorMsg = uidl.getStringAttribute("sizeErrorMsg");
        }
        if (uidl.hasAttribute("acceptFilter")) {
            getInput().setAccept(uidl.getStringAttribute("acceptFilter"));
        }
        if (uidl.hasAttribute("interruptedFileIds")) {
            removeFromFileQueue(uidl.getIntArrayAttribute("interruptedFileIds"));
        }
        if (uidl.hasAttribute("ready")) {
            postNextFileFromQueue();
        }
    }

    private InputElement getInput() {
        if (input == null || !getElement().isOrHasChild((Node) input)) {
            input = fu.getElement().cast();
        }
        return input;
    }

    private void removeFromFileQueue(int[] interruptedFileIds) {
        for (ListIterator<FileWrapper> it = fileQueue.listIterator(); it.hasNext();) {
            FileWrapper fileWrapper = it.next();
            for (int id : interruptedFileIds) {
                if (fileWrapper.getId() == id) {
                    it.remove();
                    break;
                }
            }
        }
    }

    private void postNextFileFromQueue() {
        if (!fileQueue.isEmpty()) {
            final VHtml5File file = fileQueue.remove(0).getFile();
            ExtendedXHR extendedXHR = (ExtendedXHR) ExtendedXHR.create();
            extendedXHR.setOnReadyStateChange(readyStateChangeHandler);
            extendedXHR.open("POST", receiverUri);
            extendedXHR.postFile(file);
            new Timer() {
                @Override
                public void run() {
                    // TODO poll for start or modify response so that we
                    // receive headers received
                    client.sendPendingVariableChanges();
                }
            }.schedule(700);
        }
    }

    static class ExtendedXHR extends XMLHttpRequest {

        protected ExtendedXHR() {
        }

        public final native void postFile(VHtml5File file) /*-{
         this.setRequestHeader('Accept', 'text/html,vaadin/filexhr');
         this.setRequestHeader('Content-Type', 'multipart/form-data');
         this.send(file);
         }-*/;
    }

    private static native void fireNativeClick(Element element) /*-{
     element.click();
     }-*/;

    protected void disableUpload() {
        submitButton.setEnabled(false);
        // Cannot disable the fileupload while submitting or the file won't
        // be submitted at all
        fu.getElement().setPropertyBoolean("disabled", true);
        enabled = false;
    }

    protected void enableUpload() {
        submitButton.setEnabled(true);
        fu.getElement().setPropertyBoolean("disabled", false);
        enabled = true;
    }

    private void submit() {
        if (!enabled) {
            VConsole.log("Submit cancelled (disabled)");
            return;
        }
        int files = getFileCount(fu.getElement());
        List<String> filedetails = new ArrayList<String>();
        StringBuilder errorMsg = new StringBuilder();
        for (int i = 0; i < files; i++) {
            VHtml5File file = getFile(fu.getElement(), i);
            if (file.getSize() > maxFileSize || file.getSize() <= 0) {
                String formattedErrorMsg = UploadClientUtil.getSizeErrorMessage(
                        sizeErrorMsg, maxFileSize, file.getSize(), file.getName());

                errorMsg.append(formattedErrorMsg).append("<br/>");
                continue;
            }
            FileWrapper wrapper = queueFilePost(file);
            filedetails.add(wrapper.serialize());
        }
        client.updateVariable(paintableId, "filequeue", filedetails.toArray(new String[filedetails.size()]), true);

        if (!errorMsg.toString().isEmpty()) {
            VNotification.createNotification(1000, client.getUIConnector().getWidget()).show(
                    errorMsg.toString(), VNotification.CENTERED, "warning");
        }
        disableUpload();
    }
    private List<FileWrapper> fileQueue = new ArrayList<FileWrapper>();
    private long id;

    private FileWrapper queueFilePost(VHtml5File file) {
        FileWrapper fileWrapper = new FileWrapper(++id, file);
        fileQueue.add(fileWrapper);
        return fileWrapper;
    }

    private static final class FileWrapper {

        private long id;
        private VHtml5File file;

        public FileWrapper(long id, VHtml5File file) {
            this.id = id;
            this.file = file;
        }

        public long getId() {
            return id;
        }

        public VHtml5File getFile() {
            return file;
        }

        public String serialize() {
            int size = file.getSize();
            String name = file.getName();
            String type = file.getType();
            return id + DELIM + size + DELIM + name + DELIM + type + DELIM;
        }
    }
}

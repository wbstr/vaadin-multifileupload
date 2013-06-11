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
package com.wcs.wcslib.vaadin.widget.multifileupload;

import com.vaadin.data.Property;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.wcs.wcslib.vaadin.widget.multifileupload.client.UploadClientUtil;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class WidgetTestApplication extends UI {

    private VerticalLayout root = new VerticalLayout();
    private FormLayout form = new FormLayout();
    private UploadStateWindow uploadStateWindow = new UploadStateWindow();
    private UploadFinishedHandler uploadFinishedHandler;
    private double uploadSpeed = 50;

    @Override
    protected void init(VaadinRequest request) {
        setContent(root);
        root.setMargin(true);

        addLabels();

        root.addComponent(form);

        createForm();
    }

    private void createForm() {
        createUploadFinishedHandler();

        addSlowUploadExample("Single upload", false);
        addSlowUploadExample("Multiple upload", true);
        addUploadSpeedSlider();
    }

    private void addLabels() {
        root.addComponent(new Label("These upload fields use the same window for displaying an upload queue and a "
                + "progress indicator for each field."));
        root.addComponent(new Label("You can select multiple files to upload if your browser support it (Firefox, Chrome)."));
        root.addComponent(new Label("You can cancel the current upload or remove files from the upload queue."));
        root.addComponent(new Label("You can force the MultiFileUpload to behave like the stock Upload component."));
        root.addComponent(new Label("In browsers which not support HTML5, this field behaves like the stock Upload component with the shared window functionality."));
        root.addComponent(new Label("You can slow down and speed up the upload speed. This is for the demo only."));
    }

    private void createUploadFinishedHandler() {
        uploadFinishedHandler = new UploadFinishedHandler() {
            @Override
            public void handleFile(InputStream stream, String fileName, String mimeType, long length) {
                Notification.show(fileName + " uploaded.");
            }
        };
    }

    private void addSlowUploadExample(String caption, boolean multiple) {
        SlowUpload slowUpload = new SlowUpload(uploadFinishedHandler, uploadStateWindow, multiple);
        int maxFileSize = 5242880; //5 MB
        slowUpload.setMaxFileSize(maxFileSize);
        String errorMsgPattern = "File is too big (max = {0}): {2} ({1})";
        slowUpload.setSizeErrorMsgPattern(errorMsgPattern);
        slowUpload.setCaption(caption);
        slowUpload.setPanelCaption(caption);
        slowUpload.getSmartUpload().setUploadButtonCaptions("Upload File", "Upload Files");
        form.addComponent(slowUpload);
    }

    private void addUploadSpeedSlider() {
        final Slider slider = new Slider("Delay (ms)");
        slider.setWidth("200px");
        slider.setImmediate(true);
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(uploadSpeed);
        slider.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                uploadSpeed = slider.getValue();
            }
        });
        form.addComponent(slider);
    }

    private class SlowUpload extends MultiFileUpload {

        public SlowUpload(UploadFinishedHandler handler, UploadStateWindow uploadStateWindow) {
            super(handler, uploadStateWindow, true);
        }

        public SlowUpload(UploadFinishedHandler handler, UploadStateWindow uploadStateWindow, boolean multiple) {
            super(handler, uploadStateWindow, multiple);
        }

        @Override
        protected UploadStatePanel createStatePanel(UploadStateWindow uploadStateWindow) {
            return new SlowUploadStatePanel(uploadStateWindow);
        }
    }

    private class SlowUploadStatePanel extends UploadStatePanel {

        public SlowUploadStatePanel(UploadStateWindow window) {
            super(window);
        }

        @Override
        public void onProgress(StreamVariable.StreamingProgressEvent event) {
            try {
                Thread.sleep((int) uploadSpeed);
            } catch (InterruptedException ex) {
                Logger.getLogger(WidgetTestApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.onProgress(event);
        }
    }
}
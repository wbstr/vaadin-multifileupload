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
package com.wcs.wcslib.vaadin.widget.multifileupload.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.*;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.*;

import javax.servlet.annotation.WebServlet;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demo application. Run with mvn jetty:run.
 */
@SuppressWarnings("serial")
@Title("Multifileupload Add-on Demo")
//@Push
@Theme("valo")
public class WidgetTestApplication extends UI {

    private static final String PUSH_OPTION_ID = "push";
    private static final String POLL_OPTION_ID = "poll";
    private static final String DISABLE_UPLOADS_CAPTION = "Disable uploads";
    private static final String ENABLE_UPLOADS_CAPTION = "Enable uploads";

    private static final int POLLING_INTERVAL = 1000;
    private static final int FILE_COUNT = 5;
    private List<MultiFileUpload> uploads = new ArrayList<>();
    private VerticalLayout root = new VerticalLayout();
    private FormLayout form = new FormLayout();
    private UploadStateWindow uploadStateWindow = new UploadStateWindow();
    private UploadFinishedHandler uploadFinishedHandler;
    private double uploadSpeed = 500;
    private boolean uploadFieldsEnabled = true;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = WidgetTestApplication.class, widgetset = "com.wcs.wcslib.vaadin.widget.multifileupload.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        /*
         * ProgressIndicator is deprecated from 7.1, so use UI#setPushMode(PushMode) or UI#setPollInterval(int) 
         * to refresh the ProgressBar.
         */
        setPollInterval(POLLING_INTERVAL);

        setContent(root);
        root.setMargin(true);

        addLabels();

        addRebuildUIBtn();

        root.addComponent(form);

        createForm();
    }

    private void createForm() {
        createUploadFinishedHandler();
        uploads.clear();

        addSlowUploadExample("Single upload", false);
        addSlowUploadExample("Multiple upload", true);

        addUploadAttachedCheckBoxes();

        addWindowPositionSwitcher();
        addUploadSpeedSlider();
        addOverallProgressSwitcher();
        addAllUploadFinishedHandlerSwitcher();
        addIndeterminateSwitcher();
        addPollSwitcher();
        addMaxFileCountSlider();
        addDisableUploadsButton();
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
            public void handleFile(InputStream stream, String fileName, String mimeType, long length, int filesLeftInQueue) {
                Notification.show(fileName + " uploaded (" + length + " bytes). " + filesLeftInQueue + " files left.");
            }
        };
    }

    private void addSlowUploadExample(String caption, boolean multiple) {
        final SlowUpload slowUpload = new SlowUpload(uploadFinishedHandler, uploadStateWindow, multiple);
        int maxFileSize = 5242880; //5 MB
        slowUpload.setMaxFileSize(maxFileSize);
        String errorMsgPattern = "File is too big (max = {0}): {2} ({1})";
        slowUpload.setSizeErrorMsgPattern(errorMsgPattern);
        slowUpload.setCaption(caption);
        slowUpload.setPanelCaption(caption);
        slowUpload.setMaxFileCount(FILE_COUNT);
        slowUpload.getSmartUpload().setUploadButtonCaptions("Upload File", "Upload Files");
        slowUpload.getSmartUpload().setUploadButtonIcon(FontAwesome.UPLOAD);

        form.addComponent(slowUpload, 0);
        uploads.add(slowUpload);

        addFocusBtn(slowUpload);
        if (multiple) {
            addDropArea(slowUpload);
        }
    }

    private void addFocusBtn(final SlowUpload slowUpload) {
        Button focusBtn = new Button("Focus to " + slowUpload.getCaption(), new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                slowUpload.focus();
            }
        });
        form.addComponent(focusBtn, 1);
    }

    private void addUploadSpeedSlider() {
        final Slider slider = new Slider("Delay (ms)");
        slider.setWidth("200px");
        slider.setImmediate(true);
        slider.setMin(0);
        slider.setMax(1000);
        slider.setValue(uploadSpeed);
        slider.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                uploadSpeed = slider.getValue();
            }
        });
        form.addComponent(slider);
    }

    private void addWindowPositionSwitcher() {
        final ComboBox cb = new ComboBox("Window position");
        cb.setNullSelectionAllowed(false);
        for (UploadStateWindow.WindowPosition windowPosition : UploadStateWindow.WindowPosition.values()) {
            cb.addItem(windowPosition);
            cb.setItemCaption(windowPosition, windowPosition.name());
        }
        cb.setValue(UploadStateWindow.WindowPosition.BOTTOM_RIGHT);
        cb.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                uploadStateWindow.setWindowPosition((UploadStateWindow.WindowPosition) cb.getValue());
            }
        });
        form.addComponent(cb);
    }

    private void addOverallProgressSwitcher() {
        final CheckBox cb = new CheckBox("Overall progress");
        cb.setValue(true);
        cb.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                uploadStateWindow.setOverallProgressVisible(cb.getValue());
            }
        });
        form.addComponent(cb);
    }

    private void addRebuildUIBtn() {
        root.addComponent(new Button("ReBuildUI", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                root.removeComponent(form);
                form = new FormLayout();
                root.addComponent(form);
                createForm();
            }
        }));
    }

    private void addUploadAttachedCheckBoxes() {
        for (final MultiFileUpload multiFileUpload : uploads) {
            final CheckBox cb = new CheckBox(multiFileUpload.getCaption() + " attached");
            cb.setValue(true);
            cb.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    if (cb.getValue()) {
                        form.addComponent(multiFileUpload, 0);
                    } else {
                        form.removeComponent(multiFileUpload);
                    }
                }
            });
            form.addComponent(cb);
        }
    }

    private void addPollSwitcher() {
        final OptionGroup optionGroup = new OptionGroup("ProgressBar refresh mode");
        optionGroup.addItem(POLL_OPTION_ID);
        optionGroup.setItemCaption(POLL_OPTION_ID, "Poll (1000 ms)");

        optionGroup.select(POLL_OPTION_ID);

        optionGroup.addItem(PUSH_OPTION_ID);
        optionGroup.setItemCaption(PUSH_OPTION_ID, "Push (Automatic)");
        optionGroup.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (optionGroup.getValue().equals(PUSH_OPTION_ID)) {
                    getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
                    setPollInterval(-1);
                } else {
                    getPushConfiguration().setPushMode(PushMode.DISABLED);
                    setPollInterval(POLLING_INTERVAL);
                }
            }
        });

        form.addComponent(optionGroup);
    }

    private void addAllUploadFinishedHandlerSwitcher() {
        final CheckBox cb = new CheckBox("Notification when all files have been uploaded.");
        final AllUploadFinishedHandler allUploadFinishedHandler = new AllUploadFinishedHandler() {

            @Override
            public void finished() {
                Notification.show("All files have been uploaded.", Notification.Type.TRAY_NOTIFICATION);
            }
        };
        cb.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                for (final MultiFileUpload multiFileUpload : uploads) {
                    if (cb.getValue()) {
                        multiFileUpload.setAllUploadFinishedHandler(allUploadFinishedHandler);
                    } else {
                        multiFileUpload.setAllUploadFinishedHandler(null);
                    }
                }
            }
        });
        cb.setValue(true);
        form.addComponent(cb);
    }

    private void addIndeterminateSwitcher() {
        final CheckBox cb = new CheckBox("Indeterminate ProgressBar");
        cb.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                for (final MultiFileUpload multiFileUpload : uploads) {
                    multiFileUpload.setIndeterminate(cb.getValue());
                }
            }
        });
        form.addComponent(cb);
    }

    private void addDropArea(SlowUpload slowUpload) {
        Label dropLabel = new Label("Drop files here...");
        dropLabel.addStyleName(ValoTheme.LABEL_HUGE);
        Panel dropArea = new Panel(dropLabel);
        dropArea.setWidth(300, Unit.PIXELS);
        dropArea.setHeight(150, Unit.PIXELS);
        
        DragAndDropWrapper dragAndDropWrapper = slowUpload.createDropComponent(dropArea);
        dragAndDropWrapper.setSizeUndefined();
        form.addComponent(dragAndDropWrapper, 2);
    }

    private void addMaxFileCountSlider() {
        final Slider slider = new Slider("Max file count");
        slider.setWidth("200px");
        slider.setImmediate(true);
        slider.setMin(1);
        slider.setMax(10);
        slider.setValue((double) FILE_COUNT);
        slider.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                for (final MultiFileUpload multiFileUpload : uploads) {
                    multiFileUpload.setMaxFileCount(slider.getValue().intValue());
                }
            }
        });
        form.addComponent(slider);
    }

    private void addDisableUploadsButton() {
        final WidgetTestApplication app = this;
        Button disableButton = new Button(DISABLE_UPLOADS_CAPTION, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                app.updateUploadsEnabled(clickEvent);
            }
        });
        form.addComponent(disableButton);
    }

    private void updateUploadsEnabled(Button.ClickEvent e) {
        uploadFieldsEnabled = !uploadFieldsEnabled;

        if (uploadFieldsEnabled) {
            e.getButton().setCaption(DISABLE_UPLOADS_CAPTION);
        } else {
            e.getButton().setCaption(ENABLE_UPLOADS_CAPTION);
        }

        for (MultiFileUpload upload : uploads) {
            upload.setEnabled(uploadFieldsEnabled);
        }
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

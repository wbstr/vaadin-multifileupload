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

import com.lbs.multifileupload.ui.UploadStatePanel;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gergo
 */
@StyleSheet("uploadstatewindow.css")
public class UploadStateWindow extends Window {

    private static final String CANCEL_ICON = "remove_small.png";
    private static final String WINDOW_STYLE_CLASS = "multiple-upload-state-window";
    private static final String WINDOW_LAYOUT_STYLE_CLASS = "multiple-upload-state-window-layout";
    private List<UploadStatePanel> uploadStatePanels = new ArrayList<>();
    private VerticalLayout windowLayout = new VerticalLayout();
    private String uploadStatusCaption = "Upload status";
    private String cancelButtonCaption = "Cancel";
    private Resource cancelIconResource = new ClassResource(UploadStateWindow.class, CANCEL_ICON);
    private ConfirmDialog confirmDialog = new ConfirmDialog();
    private long totalContentLength = 0;
    private long totalBytesReceived = 0;
    private boolean overallProgressVisible;
    private WindowPosition windowPosition;

    public enum WindowPosition {

        BOTTOM("position-bottom"),
        BOTTOM_LEFT("position-bottom-left"),
        BOTTOM_RIGHT("position-bottom-right"),
        TOP("position-top"),
        TOP_LEFT("position-top-left"),
        TOP_RIGHT("position-top-right"),
        LEFT("position-left"),
        RIGHT("position-right"),
        CENTER("");
        private String style;

        private WindowPosition(String style) {
            this.style = style;
        }

        public String getStyle() {
            return style;
        }
    }

    public UploadStateWindow() {
        super();
        initWindow();
    }

    private void initWindow() {
        setCaption(uploadStatusCaption);
        addStyleName(WINDOW_STYLE_CLASS);

        setResizable(false);
        setDraggable(false);

        setVisible(false);
        setWidth(350, Unit.PIXELS);
        setWindowPosition(WindowPosition.BOTTOM_RIGHT);
        setOverallProgressVisible(true);
        setClosable(true);
        addCloseListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
            	for (UploadStatePanel panel : uploadStatePanels) {
					if(panel.hasUploadInProgress()){
						show();
		                confirmDialog.show();
		                break;
					}
				}
            }
        });

        confirmDialog.setAction(new ConfirmAction() {
            @Override
            public void execute() {
                interruptAll();
            }
        });

        windowLayout.setStyleName(WINDOW_LAYOUT_STYLE_CLASS);
        setContent(windowLayout);
        windowLayout.setMargin(false);
    }

    public void refreshVisibility() {
        if (hasVisibleContent()) {
            show();
        } else {
            confirmDialog.hide();
            hide();
            setTotalContentLength(0);
            setTotalBytesReceived(0);
        }
    }

    private boolean hasVisibleContent() {
        for (UploadStatePanel uploadStatePanel : uploadStatePanels) {
            if (uploadStatePanel.isVisible()) {
                return true;
            }
        }
        return false;
    }

    private void show() {
        if (this.getParent() == null) {
            UI.getCurrent().addWindow(this);
        }
        setVisible(true);
    }

    private void hide() {
        setVisible(false);
    }

    public void interruptAll() {
        for (int i = uploadStatePanels.size() - 1; i >= 0; i--) {
            UploadStatePanel panel = uploadStatePanels.get(i);
            panel.interruptAll();
        }
    }

    public void updateOverallProgress() {
        if (overallProgressVisible) {
            int overallProgress = (int) ((totalBytesReceived * 100.0f) / totalContentLength);
            setCaption(uploadStatusCaption + " (" + overallProgress + "%)");
        }else{
            setCaption(uploadStatusCaption);
        }
    }

    public void addPanel(UploadStatePanel panel) {
        uploadStatePanels.add(panel);
        windowLayout.addComponent(panel);
    }

    public void removePanel(UploadStatePanel panel) {
        uploadStatePanels.remove(panel);
        windowLayout.removeComponent(panel);
    }

    public void setUploadStatusCaption(String uploadStatusCaption) {
        this.uploadStatusCaption = uploadStatusCaption;
        setCaption(uploadStatusCaption);
    }

    public void setCancelButtonCaption(String cancelButtonCaption) {
        this.cancelButtonCaption = cancelButtonCaption;
    }

    public String getCancelButtonCaption() {
        return cancelButtonCaption;
    }

    public Resource getCancelIconResource() {
        return cancelIconResource;
    }

    public void setCancelIconResource(Resource cancelIconResource) {
        this.cancelIconResource = cancelIconResource;
    }

    public ConfirmDialog getConfirmDialog() {
        return confirmDialog;
    }

    public void setConfirmDialog(ConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }

    public long getTotalContentLength() {
        return totalContentLength;
    }

    public void setTotalContentLength(long totalContentLength) {
        this.totalContentLength = Math.max(totalContentLength, 0);
    }

    public long getTotalBytesReceived() {
        return totalBytesReceived;
    }

    public void setTotalBytesReceived(long totalBytesReceived) {
        this.totalBytesReceived = Math.max(totalBytesReceived, 0);
        updateOverallProgress();
    }

    public boolean isOverallProgressVisible() {
        return overallProgressVisible;
    }

    public void setOverallProgressVisible(boolean overallProgressVisible) {
        this.overallProgressVisible = overallProgressVisible;
    }

    @Override
    public void center() {
        setWindowPosition(WindowPosition.CENTER);
    }

    public WindowPosition getWindowPosition() {
        return windowPosition;
    }

    public void setWindowPosition(WindowPosition windowPosition) {
        String oldPositionStyle = this.windowPosition != null ? this.windowPosition.getStyle() : "";
        removeStyleName(oldPositionStyle);
        super.center();

        this.windowPosition = windowPosition;
        addStyleName(windowPosition.getStyle());
    }
}
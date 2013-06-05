package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

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
    private List<UploadStatePanel> uploadStatePanels = new ArrayList<UploadStatePanel>();
    private VerticalLayout windowLayout;
    private String uploadStatusCaption = "Upload status";
    private String cancelButtonCaption = "Cancel";
    private Resource cancelIconResource = new ClassResource(UploadStateWindow.class, CANCEL_ICON);

    public UploadStateWindow() {
        super();
        setCaption(uploadStatusCaption);
        addStyleName(WINDOW_STYLE_CLASS);

        setResizable(false);
        setDraggable(false);
        setClosable(false);
        setVisible(false);
        setWidth(350, Unit.PIXELS);

        windowLayout = new VerticalLayout();
        windowLayout.setStyleName(WINDOW_LAYOUT_STYLE_CLASS);
        setContent(windowLayout);
        windowLayout.setMargin(false);
    }

    public void refreshVisibility() {
        if (hasVisibleContent()) {
            show();
        } else {
            hide();
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
        if (!isVisible()) {
            if (this.getParent() == null) {
                UI.getCurrent().addWindow(this);
            }
            setVisible(true);
        }
    }

    private void hide() {
        setVisible(false);
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
}
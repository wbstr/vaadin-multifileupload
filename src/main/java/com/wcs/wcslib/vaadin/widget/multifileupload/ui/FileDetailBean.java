package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.FileDetail;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.UploadUtil;
import java.io.Serializable;

/**
 *
 * @author gergo
 */
public class FileDetailBean implements Serializable {

    public final static String READABLE_CONTENT_LENGTH = "readableContentLength";
    public final static String FILE_NAME = "fileName";
    public final static String ID = "id";
    public final static String CANCEL_BUTTON = "cancelButton";
    private long id;
    private String fileName;
    private long contentLength;
    private String readableContentLength;
    private Button cancelButton;

    public FileDetailBean(FileDetail fileDetail, UploadStatePanel uploadStatePanel) {
        this.id = fileDetail.getId();
        this.fileName = fileDetail.getFileName();
        this.contentLength = fileDetail.getContentLength();
        this.readableContentLength = UploadUtil.getHumanReadableByteCount(contentLength, false);
        createCancelBtn(uploadStatePanel);
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getReadableContentLength() {
        return readableContentLength;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileDetailBean other = (FileDetailBean) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    private Button createCancelBtn(final UploadStatePanel uploadStatePanel) {
        cancelButton = new Button();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                uploadStatePanel.interruptUpload(FileDetailBean.this);
            }
        });
        cancelButton.setIcon(uploadStatePanel.getWindow().getCancelIconResource());
        cancelButton.setStyleName(BaseTheme.BUTTON_LINK);
        cancelButton.setDescription(uploadStatePanel.getWindow().getCancelButtonCaption());
        return cancelButton;
    }
}

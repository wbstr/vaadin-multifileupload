package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.FileDetail;
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
        this.readableContentLength = FileUploadUtil.getHumanReadableByteCount(contentLength, false);
        cancelButton = FileUploadUtil.createCancelBtn(uploadStatePanel, this, false, true);
        cancelButton.setStyleName(BaseTheme.BUTTON_LINK);
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
}

package com.wcs.wcslib.vaadin.widget.multifileupload.component;

/**
 *
 * @author gergo
 */
public class FileDetail {

    private static final String DELIMITER = "---xXx---";
    private long id;
    private String fileName;
    private String mimeType = "todo/todo";
    private int contentLength;

    public FileDetail(String data) {
        String[] split = data.split(DELIMITER);
        id = Long.parseLong(split[0]);
        contentLength = Integer.parseInt(split[1]);
        fileName = split[2];
    }

    public FileDetail(String caption, String mimeType, int parseInt) {
        this.fileName = caption;
        this.mimeType = mimeType;
        this.contentLength = parseInt;
    }

    public long getId() {
        return id;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }
}
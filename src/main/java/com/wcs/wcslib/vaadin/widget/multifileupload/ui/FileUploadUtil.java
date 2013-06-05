package com.wcs.wcslib.vaadin.widget.multifileupload.ui;

import com.vaadin.ui.Button;

/**
 *
 * @author gergo
 */
public class FileUploadUtil {

    public static String getHumanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static Button createCancelBtn(
            final UploadStatePanel uploadStatePanel,
            final FileDetailBean fileDetailBean,
            boolean caption, boolean description) {

        Button cancelButton = new Button();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                uploadStatePanel.getMultiUpload().interruptUpload(fileDetailBean.getId());
                uploadStatePanel.removeFromQueue(fileDetailBean);
            }
        });
        cancelButton.setIcon(uploadStatePanel.getWindow().getCancelIconResource());
        cancelButton.setStyleName("small");
        if (caption) {
            cancelButton.setCaption(uploadStatePanel.getWindow().getCancelButtonCaption());
        }
        if (description) {
            cancelButton.setDescription(uploadStatePanel.getWindow().getCancelButtonCaption());
        }
        return cancelButton;
    }
}

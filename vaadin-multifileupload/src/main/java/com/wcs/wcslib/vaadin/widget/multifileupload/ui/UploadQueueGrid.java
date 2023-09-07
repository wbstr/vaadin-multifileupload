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

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import java.util.List;

/**
 *
 * @author gergo
 */
public class UploadQueueGrid extends Grid<FileDetailBean> {
    private static final long serialVersionUID = -3418839627408741300L;
    private static final String GRID_STYLE_CLASS = "multiple-upload-queue-grid";
    protected int cancelColumnWidth = 50;
    protected int nameColumnWidth = 170;
    protected int sizeColumnWidth = 105;
    private int maxVisibleRows = 4;

    public UploadQueueGrid(UploadStatePanel uploadStatePanel) {
        addColumn(FileDetailBean::getFileName)
                .setWidth(nameColumnWidth).
                setDescriptionGenerator((FileDetailBean fileDetailBean) -> {
                    return fileDetailBean.getFileName();
                });
        addColumn(FileDetailBean::getReadableContentLength)
                .setWidth(sizeColumnWidth).
                setDescriptionGenerator((FileDetailBean fileDetailBean) -> {
                    return fileDetailBean.getReadableContentLength();
                });
        addColumn(FileDetailBean::getCancelIcon)
                .setWidth(cancelColumnWidth)
                .setRenderer(new ImageRenderer(new ClickableRenderer.RendererClickListener<FileDetailBean>() {
                    @Override
                    public void click(ClickableRenderer.RendererClickEvent<FileDetailBean> event) {
                        uploadStatePanel.interruptUpload(event.getItem());
                    }
                }));

        setColumnReorderingAllowed(false);
        setHeightMode(HeightMode.ROW);
        removeHeaderRow(0);
        setWidth(100, Unit.PERCENTAGE);
        setVisible(false);
        addStyleName(GRID_STYLE_CLASS);
    }

    public void refreshItems(List<FileDetailBean> uploadQueue) {
        if (uploadQueue.size() > 1) {
            setItems(uploadQueue.subList(1, uploadQueue.size()));
            setHeightByRows(Math.min(maxVisibleRows, uploadQueue.size() - 1));
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public int getMaxVisibleRows() {
        return maxVisibleRows;
    }

    public void setMaxVisibleRows(int maxVisibleRows) {
        this.maxVisibleRows = Math.max(maxVisibleRows, 0);
    }

}

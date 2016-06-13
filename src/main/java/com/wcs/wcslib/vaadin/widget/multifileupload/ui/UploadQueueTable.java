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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import java.util.List;

/**
 *
 * @author gergo
 */
public class UploadQueueTable extends Table {

    private static final String TABLE_STLYE_CLASS = "multiple-upload-state-table";
    private static final String TABLE_NO_OVERFLOWY_STLYE_CLASS = "multiple-upload-state-table-nooverflowy";
    protected int cancelColumnWidth = 30;
    protected int nameColumnWidth = 180;
    private int maxVisibleRows = 3;
    private BeanItemContainer<FileDetailBean> container;

    public UploadQueueTable() {
        container = new BeanItemContainer<FileDetailBean>(FileDetailBean.class);
        setContainerDataSource(container);

        setVisibleColumns(new Object[]{
            FileDetailBean.FILE_NAME, FileDetailBean.READABLE_CONTENT_LENGTH, FileDetailBean.CANCEL_BUTTON});
        setColumnAlignment(FileDetailBean.CANCEL_BUTTON, Table.Align.CENTER);
        setColumnWidth(FileDetailBean.CANCEL_BUTTON, cancelColumnWidth);
        setColumnWidth(FileDetailBean.FILE_NAME, nameColumnWidth);

        setItemDescriptionGenerator(new UploadQueueTableDescriptionGenerator());

        setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        setColumnReorderingAllowed(false);
        setSortEnabled(false);
        setWidth(100, Unit.PERCENTAGE);
        setVisible(false);
        addStyleName(TABLE_STLYE_CLASS);
    }

    public void refreshContainerDatasource(List<FileDetailBean> uploadQueue) {
        container.removeAllItems();
        if (uploadQueue.size() > 1) {
            container.addAll(uploadQueue.subList(1, uploadQueue.size()));
            setPageLength(Math.min(maxVisibleRows, getContainerDataSource().size()));
            if (getContainerDataSource().size() <= maxVisibleRows) {
                addStyleName(TABLE_NO_OVERFLOWY_STLYE_CLASS);
            } else {
                removeStyleName(TABLE_NO_OVERFLOWY_STLYE_CLASS);
            }
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

    private static class UploadQueueTableDescriptionGenerator implements AbstractSelect.ItemDescriptionGenerator {

        @Override
        public String generateDescription(Component source, Object itemId, Object propertyId) {
            if (FileDetailBean.FILE_NAME.equals(propertyId)) {
                FileDetailBean bean = (FileDetailBean) itemId;
                return bean.getFileName();
            }
            return null;
        }
    }
}

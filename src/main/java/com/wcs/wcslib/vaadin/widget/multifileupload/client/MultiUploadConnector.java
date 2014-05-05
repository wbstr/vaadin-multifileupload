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
package com.wcs.wcslib.vaadin.widget.multifileupload.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.ImageIcon;
import com.vaadin.shared.ui.Connect;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.MultiUpload;

/*
 * This is a modified version of org.vaadin.easyuploads.client.ui.Multiuploadconnector.java 
 * which is part of the EasyUploads 7.0.0 Vaadin addon. 
 */
@Connect(MultiUpload.class)
public class MultiUploadConnector extends AbstractComponentConnector implements Paintable {

    @Override
    protected Widget createWidget() {
        return GWT.create(VMultiUpload.class);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        ((Paintable) getWidget()).updateFromUIDL(uidl, client);
    }

    @Override
    protected void init() {
        super.init();
        addStateChangeHandler("resources", new StateChangeEvent.StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                if (getIcon() != null) {
                    if (getWidget().icon == null) {
                        getWidget().icon = new ImageIcon();
                        Element iconElement = getWidget().icon.getElement();
                        getWidget().submitButton.wrapper.insertBefore(iconElement,
                                getWidget().submitButton.captionElement);
                    }
                    getWidget().icon.setUri(getIconUri());
                } else {
                    if (getWidget().icon != null) {
                        getWidget().submitButton.wrapper.removeChild(getWidget().icon
                                .getElement());
                        getWidget().icon = null;
                    }
                }
            }
        });
    }

    @Override
    public VMultiUpload getWidget() {
        return (VMultiUpload) super.getWidget();
    }
}

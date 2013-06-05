package com.wcs.wcslib.vaadin.widget.multifileupload.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.wcs.wcslib.vaadin.widget.multifileupload.component.MultiUpload;

/*
 * This is a modified version of org.vaadin.easyuploads.client.ui.Multiuploadconnector.java 
 * which is part of the EasyUploads 7.0.0 Vaadin addon. 
 */
@Connect(MultiUpload.class)
public class Multiuploadconnector extends AbstractComponentConnector implements Paintable {

    @Override
    protected Widget createWidget() {
        return GWT.create(VMultiUpload.class);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        ((Paintable) getWidget()).updateFromUIDL(uidl, client);
    }
}

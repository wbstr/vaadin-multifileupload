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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @author gergo
 */
public class ConfirmDialog extends Window {

    private ConfirmAction action;
    private Label question;
    private Button yes;
    private Button no;
    private String confirmHeader = "Interrupting uploads";
    private String confirmQuestion = "This will cause ALL uploads to be interrupted. Are you sure?";
    private String confirmYes = "Yes";
    private String confirmNo = "No";

    public ConfirmDialog() {
        setClosable(false);
        setModal(true);
        setSizeUndefined();
        setResizable(false);
        initForm();
    }

    private void initForm() {
        VerticalLayout windowLayout = new VerticalLayout();
        setContent(windowLayout);
        windowLayout.setSizeUndefined();
        windowLayout.setMargin(true);
        setCaption(confirmHeader);

        question = new Label(confirmQuestion);
        windowLayout.addComponent(question);

        HorizontalLayout buttonLayout = createButtonLayout();
        windowLayout.addComponent(buttonLayout);
        windowLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        yes = new Button(confirmYes);
        yes.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                hide();
                if (action != null) {
                    action.execute();
                }
            }
        });
        buttonLayout.addComponent(yes);

        no = new Button(confirmNo);
        no.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                hide();
            }
        });
        buttonLayout.addComponent(no);

        return buttonLayout;
    }

    public void hide() {
        if (getParent() != null && getParent().getUI() != null) {
            getParent().getUI().removeWindow(this);
        }
    }

    public void show() {
        if (this.getParent() == null) {
            UI.getCurrent().addWindow(this);
        }
    }

    public String getConfirmHeader() {
        return confirmHeader;
    }

    public void setConfirmHeader(String confirmHeader) {
        this.confirmHeader = confirmHeader;
        setCaption(confirmHeader);
    }

    public String getConfirmQuestion() {
        return confirmQuestion;
    }

    public void setConfirmQuestion(String confirmQuestion) {
        this.confirmQuestion = confirmQuestion;
        question.setValue(confirmQuestion);
    }

    public String getConfirmYes() {
        return confirmYes;
    }

    public void setConfirmYes(String confirmYes) {
        this.confirmYes = confirmYes;
        yes.setCaption(confirmYes);
    }

    public String getConfirmNo() {
        return confirmNo;
    }

    public void setConfirmNo(String confirmNo) {
        this.confirmNo = confirmNo;
        no.setCaption(confirmNo);
    }

    public ConfirmAction getAction() {
        return action;
    }

    public void setAction(ConfirmAction action) {
        this.action = action;
    }
}

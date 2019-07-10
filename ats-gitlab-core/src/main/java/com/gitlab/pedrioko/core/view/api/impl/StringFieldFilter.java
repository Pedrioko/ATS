package com.gitlab.pedrioko.core.view.api.impl;

import com.gitlab.pedrioko.core.lang.annotation.FieldFilter;
import com.gitlab.pedrioko.core.view.api.FieldFilterComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;

import java.lang.reflect.Field;

@FieldFilter
public class StringFieldFilter implements FieldFilterComponent {

    @Override
    public Class[] getToClass() {
        return new Class[]{String.class, char.class};
    }

    @Override
    public Component getComponent(Field field) {
        Textbox textbox = new Textbox();
        return textbox;
    }

}
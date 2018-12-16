package com.gitlab.pedrioko.core.zk.component;

import com.gitlab.pedrioko.core.lang.LongRange;
import org.zkoss.zul.Div;
import org.zkoss.zul.Longbox;

public class LongRangeBox extends Div {
    Longbox fin = new Longbox();
    Longbox inicio = new Longbox();
    private LongRange value;

    public LongRangeBox() {
        value = new LongRange();
        load();
    }

    private void load() {
        inicio.setWidth("100%");
        fin.setWidth("100%");
        appendChild(inicio);
        appendChild(fin);
    }

    public LongRange getValue() {
        Long inicio = this.inicio.getValue();

        Long fin = this.fin.getValue();
        if (inicio != null && fin != null) {
            this.value.setInicio(inicio);
            this.value.setFin(fin);
            return this.value;
        } else {
            return null;
        }
    }

    public void setValue(LongRange value) {
        this.value = value;
        value.setInicio(inicio.getValue());
        value.setFin(fin.getValue());
    }
}


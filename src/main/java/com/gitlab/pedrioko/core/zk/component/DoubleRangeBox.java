package com.gitlab.pedrioko.core.zk.component;

import com.gitlab.pedrioko.core.lang.DoubleRange;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;

public class DoubleRangeBox extends Div {
    Doublebox fin = new Doublebox();
    Doublebox inicio = new Doublebox();
    private DoubleRange value;

    public DoubleRangeBox() {
        value = new DoubleRange();
        load();
    }

    private void load() {
        inicio.setWidth("100%");
        fin.setWidth("100%");
        appendChild(inicio);
        appendChild(fin);
    }

    public DoubleRange getValue() {
        Double inicio = this.inicio.getValue();

        Double fin = this.fin.getValue();
        if (inicio != null && fin != null) {
            this.value.setInicio(inicio);
            this.value.setFin(fin);
            return this.value;
        } else {
            return null;
        }
    }

    public void setValue(DoubleRange value) {
        this.value = value;
        value.setInicio(inicio.getValue());
        value.setFin(fin.getValue());
    }
}

